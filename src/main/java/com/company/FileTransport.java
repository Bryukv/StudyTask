package com.company;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.model.dataformat.CsvDataFormat;
import javax.jms.ConnectionFactory;
import java.util.List;

public class FileTransport {
    public FileTransport(CamelContext camelContext) throws Exception {
        String pathIn = "D:/Development/Project/Files/In";
        String pathOut = "D:/Development/Project/Files/Test";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        CsvDataFormat csv = new CsvDataFormat();
        //csv.setSkipHeaderRecord(true);
        csv.setDelimiter(";");
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class)
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,Throwable.class);
                                System.out.println("-------------------caught exception----------------------------------------------------------------------");
                                System.out.println(caused.getMessage());
                            }
                        });
                from("file:" + pathIn + "?noop=true")

                        .choice().when (header("CamelFileName").endsWith(".csv"))
                        .split(body().tokenize("\n",1,true))
                        .unmarshal(csv)
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Message msg = exchange.getIn();
                                List<List<String>> rows = (List<List<String>>)msg.getBody();
                                msg.setHeader("CamelFileName", msg.getMessageId() + ".json");

                                msg.setBody(new MapToJson(rows.get(0)));
                               // System.out.println("Обработано " + exchange.get);
                            }
                        })
                        .marshal(jacksonDataFormat)
                        .wireTap("file:" + pathOut)

                        .to("jms:queue:incomingOrders");

            }
        });

    }
}
