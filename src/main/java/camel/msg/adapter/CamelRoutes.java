package camel.msg.adapter;

import camel.msg.adapter.data.Coordinates;
import camel.msg.adapter.data.CurrentWeather;
import camel.msg.adapter.data.MsgA;
import camel.msg.adapter.data.MsgB;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.model.rest.RestBindingMode;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CamelRoutes extends EndpointRouteBuilder {

    String weatherUrl = "https://api.openweathermap.org/data/2.5/weather";
    String apikey = "f465148ee89b812ecf2ce551a19ce0bc";
    String city = "London";

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure() {
        restConfiguration()
                .component("undertow")
                .bindingMode(RestBindingMode.json)
                .host("localhost").port(8080)
                .apiProperty("cors", "true");

        from("direct:start")
                .log("START")
                .to("direct:adapter");

        String mycoordinates = "lat=54.35&lon=52.52";

        from("direct:temp")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
//                .to(String.format("%s?q=%s&appid=%s&bridgeEndpoint=true", weatherUrl, city, apikey))
                .to(weatherUrl+String.format("?%s&appid=%s&bridgeEndpoint=true", mycoordinates , apikey))
//                .log("${body}")
                .process(exchange -> {
                    final CurrentWeather weather = mapper.readValue(exchange.getIn().getBody(String.class), CurrentWeather.class);
//                    System.out.println(weather);
                    exchange.getIn().setHeader("currentTemp", weather.getMain().getTemp());
                })
//                .log("finish: ${header.currentTemp}")
        ;


        from("direct:adapter")
//                .unmarshal().json(JsonLibrary.Jackson, MsgA.class)
                .log("ADAPTER< ${body}")
                .to("direct:getlng").filter(body().isNotNull())
//            .log("LNG")
                .to("direct:getmsg")
//            .log("MSG")
                .log("${body}")
                .choice()
                    .when(body().isNull())
                    .to("direct:error")
                    .otherwise()
                    .to("direct:message")
                .end()
//                .marshal().json(JsonLibrary.Jackson, MsgB.class)
        ;

//    3
        from("direct:error")
                .setBody(simple("Invalid input message"))
                .log("Error: ${body}");

        from("direct:message")
                .process(exchange -> {
                    Coordinates coordinates = exchange.getIn().getBody(MsgA.class).getCoordinates();
                    exchange.getIn().setHeader("coordinates", String.format("lat=%s&lon=%s",
                            coordinates.getLatitude(), coordinates.getLongitude()));
                    exchange.getIn().setHeader("msg", exchange.getIn().getBody(MsgA.class).getMsg());
                })
                .log("COORDINATES> ${header.coordinates}")
                .to("direct:temp")
//            .log("TEMPERATURE> ${header.currentTemp}")
                .process(exchange -> {
                    Integer currentTemp = exchange.getIn().getHeader("currentTemp", Integer.class);
                    String msg = exchange.getIn().getHeader("msg", String.class);
                    exchange.getIn().setBody(
                            new MsgB(msg, new Date().toString(), currentTemp).toString()
                    );
                })
                .to("jms:queue:MsgB");

        from("direct:serviceb")
                .log("ServiceB> ${body}");

        from("jms:queue:MsgB")
                .to("direct:serviceb")
                .transform(simple("${body}"));

        from("direct:getlng")
                .process(exchange -> {
                    final String lng = exchange.getIn().getBody(MsgA.class).getLng();
                    if (!lng.matches("ru|RU")) exchange.getIn().setBody(null);
                });

        from("direct:getmsg")
                .process(exchange -> {
                    final String msg = exchange.getIn().getBody(MsgA.class).getMsg();
                    if (msg == null || msg.equals("")) exchange.getIn().setBody(null);
                });

        from("direct:weather")
//            .log("WEATHER< ${header.coordinates}")
//            .log("WEATHER< ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                .to(String.format("%s?q=%s&appid=%s&bridgeEndpoint=true", weatherUrl, city, apikey))
//                .log("BODY> ${body}")
                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
//                    System.out.println(body);
                    CurrentWeather weather = mapper.readValue(body, CurrentWeather.class);
//                    System.out.println(weather.getMain());
                    exchange.getIn().setHeader("currentTemp", weather.getMain().getTemp());
                })
//                .log("${header.currentTemp}")
        ;

        from("direct:yandexWeather")
//            .log("WEATHER< ${header.coordinates}")
//            .log("WEATHER< ${body}")
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .setHeader("X-Yandex-API-Key", constant("6667c51e-dc0b-4b1c-9218-4ed662bcdab5"))
                .to("https://api.weather.yandex.ru/v2/forecast?${header.coordinates}&bridgeEndpoint=true")
//            .log(">${body}")
                .process(exchange -> {
                    final String rows = exchange.getIn().getBody(String.class)
                            .replaceAll("\"", "").replaceAll(" ", "");
                    Matcher m = Pattern.compile("fact:\\{temp:(\\d+)").matcher(rows);
                    exchange.getIn().setHeader("currentTemp", m.find() ? m.group(1) : null);
                });
    }
}