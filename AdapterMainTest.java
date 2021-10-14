package camel.springboot.msg.adapter;

import static java.lang.Math.random;
import static java.lang.Math.round;

import camel.springboot.msg.adapter.data.Coordinates;
import camel.springboot.msg.adapter.data.MsgA;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdapterMainTest {

  @Autowired
  CamelContext camelContext;

  @Test
  void main() {

    ProducerTemplate template = camelContext.createProducerTemplate();
    template.sendBody("direct:start",
        new MsgA(null, "en", new Coordinates(12.34, 56.78))
    );
    template.sendBody("direct:start",
        new MsgA(null, "ru", new Coordinates(12.34, 56.78))
    );
    template.sendBody("direct:start",
        new MsgA("", "RU", new Coordinates(12.34, 56.78))
    );
    template.sendBody("direct:start",
        new MsgA("current weather", "ru", new Coordinates(54.35, 52.52))
    );
    for (int i = 1; i <= 9; i++) {
      template.sendBody("direct:start",
          new MsgA("current weather " + i, "ru",
              new Coordinates(round(random() * 90), round(random() * 90)))
      );
    }
  }

}
