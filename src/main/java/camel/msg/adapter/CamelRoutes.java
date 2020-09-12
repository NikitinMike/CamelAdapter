package camel.msg.adapter;

import camel.msg.adapter.data.Coordinates;
import camel.msg.adapter.data.MsgA;
import camel.msg.adapter.data.MsgB;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Exchange;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

public class CamelRoutes extends EndpointRouteBuilder {

  @Override
  public void configure() {
    restConfiguration()
        .component("undertow")
        .bindingMode(RestBindingMode.json)
        .host("localhost").port(8080)
        .apiProperty("cors", "true");

    from("direct:getmsg")
        .process(exchange -> {
          String msg = exchange.getIn().getBody(MsgA.class).getMsg();
          if (msg == null || msg.equals("")) {
            exchange.getIn().setBody(null);
          }
        });

    from("direct:getlng")
        .process(exchange -> {
          String lng = exchange.getIn().getBody(MsgA.class).getLng();
          if (!lng.matches("ru|RU")) {
            exchange.getIn().setBody(null);
          }
        });

    from("direct:start")
        .to("rest:post:adapter");

    from("rest:post:adapter")
        .unmarshal().json(JsonLibrary.Jackson, MsgA.class)
        .log("ADAPTER< ${body}")
        .to("direct:getlng").filter(body().isNotNull())
        .to("direct:getmsg")
        .choice()
        .when(body().isNull())
        .to("direct:error")
        .otherwise()
        .to("direct:message")
        .end()
        .marshal().json(JsonLibrary.Jackson, MsgB.class);

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
//            .log("COORDINATES> ${header.coordinates}")
        .to("direct:weather")
//            .log("TEMPERATURE> ${header.currentTemp}")
        .process(exchange -> {
          Integer currentTemp = exchange.getIn().getHeader("currentTemp", Integer.class);
          String msg = exchange.getIn().getHeader("msg", String.class);
          exchange.getIn().setBody(
              new MsgB(msg, new Date().toString(), currentTemp).toString()
          );
        })
        .to("jms:queue:MsgB");

    from("direct:weather")
//            .log("WEATHER< ${header.coordinates}")
//            .log("WEATHER< ${body}")
        .setHeader(Exchange.HTTP_METHOD, simple("GET"))
        .setHeader("X-Yandex-API-Key",
            constant("6667c51e-dc0b-4b1c-9218-4ed662bcdab5"))
        .to("https://api.weather.yandex.ru/v2/forecast?${header.coordinates}"
            + "&bridgeEndpoint=true"
        )
//            .log(">${body}")
        .process(exchange -> {
          final String rows = exchange.getIn().getBody(String.class)
              .replaceAll("\"", "")
              .replaceAll(" ", "");
          Matcher m = Pattern.compile("fact:\\{temp:(\\d+)").matcher(rows);
          exchange.getIn().setHeader("currentTemp", m.find() ? m.group(1) : null);
        });

    from("direct:serviceb")
        .log("ServiceB> ${body}");

    from("jms:queue:MsgB")
        .to("direct:serviceb")
        .transform(simple("${body}"));

  }
}

