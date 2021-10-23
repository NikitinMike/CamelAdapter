package camel.msg.adapter;

import camel.msg.adapter.data.Coordinates;
import camel.msg.adapter.data.MsgA;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import static java.lang.Math.random;

public final class AdapterMain {

    final static CamelContext camel = new DefaultCamelContext();
    final static double lat = 56.638771;
    final static double lon = 47.890781;

    public static void main(String[] args) throws Exception {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "vm://localhost?broker.persistent=false");
        connectionFactory.setTrustAllPackages(true);
        camel.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        camel.addRoutes(new CamelRoutes());
        camel.start();
        testingCamel(9);
        Thread.sleep(1_000);
        camel.stop();

    }

    static double rnd(int n) {
        return random() * n;
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
                new MsgA("current ", "ru", new Coordinates(lat, lon))
        );
        for (int i = 1; i <= n; i++) { // cloud points
            template.sendBody("direct:start",
                    new MsgA("current " + i + 1, "ru",
                            new Coordinates(lat - rnd(n), lon - rnd(n))));
            template.sendBody("direct:start",
                    new MsgA("current " + i + 2, "ru",
                            new Coordinates(lat + rnd(n), lon + rnd(n))));
            template.sendBody("direct:start",
                    new MsgA("current " + i + 3, "ru",
                            new Coordinates(lat + rnd(n), lon - rnd(n))));
            template.sendBody("direct:start",
                    new MsgA("current " + i + 4, "ru",
                            new Coordinates(lat - rnd(n), lon + rnd(n))));
        }
    }

}
