package camel.msg.adapter;

import camel.msg.adapter.data.Coordinates;
import camel.msg.adapter.data.CurrentWeather;
import camel.msg.adapter.data.MsgA;
import camel.msg.adapter.data.MsgB;
import camel.msg.adapter.data.weather.Weather;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.http.HttpMethods;

import java.util.Date;

public class CamelRoutes extends EndpointRouteBuilder {

    final String weatherUrl = "https://api.openweathermap.org/data/2.5/weather";
    final String apikey = "f465148ee89b812ecf2ce551a19ce0bc";
//    String city = "Yoshkar-Ola"; //  "London";
    final String paramUri = "&appid=" + apikey + "&lang=ru&units=metric&bridgeEndpoint=true";

    final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure() {

        from("direct:start")
//                .log("START")
                .to("direct:adapter");

        from("direct:adapter")
                .to("direct:getLng").filter(body().isNotNull())
                .to("direct:getMsgA")
                .log("<<${body}")
                .choice()
                .when(body().isNull())
                .to("direct:error")
                .otherwise()
                .to("direct:message")
                .end()
        ;

        from("direct:getLng")
                .process(exchange -> {
                    final String lng = exchange.getIn().getBody(MsgA.class).getLng();
                    if (!lng.matches("ru|RU")) exchange.getIn().setBody(null);
                    else exchange.getIn().setHeader("lng",lng);
                });

        from("direct:getMsgA")
                .process(exchange -> {
                    final String msg = exchange.getIn().getBody(MsgA.class).getMsg();
                    if (msg == null || msg.equals("")) exchange.getIn().setBody(null);
                });

        from("direct:error")
                .setBody(simple("Invalid input message"))
                .log("Error: ${body}");

        from("direct:message")
//                .log("${body}")
                .process(exchange -> {
                    exchange.getIn().setHeader("msg", exchange.getIn().getBody(MsgA.class).getMsg());
                    final Coordinates coordinates = exchange.getIn().getBody(MsgA.class).getCoordinates();
                    exchange.getIn().setBody(coordinates.toString());
                })
                .to("direct:getTemp")
//                .log("${header.name} : ${header.description}")
                .to("jms:queue:MsgB")
        ;

        from("direct:getTemp")
//                .log("POS:${body}")
                .setHeader(Exchange.HTTP_QUERY, simple("${body}"+paramUri))
                .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
                .to(weatherUrl)
                .process(exchange -> {
                    final CurrentWeather current = mapper.readValue(exchange.getIn().getBody(String.class), CurrentWeather.class);
//                    exchange.getIn().setHeader("name", current.getName());
                    Weather weather = current.getWeather().get(0);
//                    exchange.getIn().setHeader("description", weather.getDescription());
                    exchange.getIn().setBody(new MsgB(
                            current.getName()+" : "+weather.getDescription(),
//                            exchange.getIn().getHeader("msg", String.class),
                            new Date(), (double) current.getMain().getTemp()).toString()
                    );
                })
        ;

        from("jms:queue:MsgB")
                .to("direct:serviceB")
                .transform(simple("${body}"));

        from("direct:serviceB")
                .log(">>${body}");
    }
}