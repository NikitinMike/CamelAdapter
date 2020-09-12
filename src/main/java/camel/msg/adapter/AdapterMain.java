package camel.msg.adapter;

import static java.lang.Math.random;
import static java.lang.Math.round;

import camel.msg.adapter.data.Coordinates;
import camel.msg.adapter.data.MsgA;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public final class AdapterMain {

  static CamelContext camel = new DefaultCamelContext();

  public static void main(String[] args) throws Exception {

    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
        "vm://localhost?broker.persistent=false");
    connectionFactory.setTrustAllPackages(true);
    camel.addComponent("jms",
        JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

    camel.addRoutes(new CamelRoutes());
    camel.start();
    testingCamel(1);
    Thread.sleep(100_000);
    camel.stop();

  }

  static void testingCamel(int n) {
    ProducerTemplate template = camel.createProducerTemplate();
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
    for (int i = 1; i <= n; i++) {
      template.sendBody("direct:start",
          new MsgA("current weather " + i, "ru",
              new Coordinates(round(random() * 90), round(random() * 90)))
      );
    }
  }

}
