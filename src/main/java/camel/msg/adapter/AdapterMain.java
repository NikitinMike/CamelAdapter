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
    private static double lat = 56.638771;
    private static double lon = 47.890781;

    public static void main(String[] args) throws Exception {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "vm://localhost?broker.persistent=false");
        connectionFactory.setTrustAllPackages(true);
        camel.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        camel.addRoutes(new CamelRoutes());
        camel.start();
        testingCamel(10);
        Thread.sleep(1_000);
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
                new MsgA("current ", "ru", new Coordinates(lat, lon))
        );
        for (int i = 1; i <= n; i++) { // cloud points
            template.sendBody("direct:start",
                    new MsgA("current " + i, "ru",
                            new Coordinates(lat - round(random() * n), lon - round(random() * n))));
            template.sendBody("direct:start",
                    new MsgA("current " + i, "ru",
                            new Coordinates(lat + round(random() * n), lon + round(random() * n))));
            template.sendBody("direct:start",
                    new MsgA("current " + i, "ru",
                            new Coordinates(lat + round(random() * n), lon - round(random() * n))));
            template.sendBody("direct:start",
                    new MsgA("current " + i, "ru",
                            new Coordinates(lat - round(random() * n), lon + round(random() * n))));
        }
    }

}
