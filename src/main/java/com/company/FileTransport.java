package com.company;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.CsvDataFormat;
import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.List;

public class FileTransport {
    public FileTransport(CamelContext camelContext) throws Exception {
        String pathIn = "D:/Development/Project/Files/In";
        String pathInBindy = "D:/Development/Project/Files/In/BindyTest";
        String pathOut = "D:/Development/Project/Files/Test";
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        CsvDataFormat csv = new CsvDataFormat();
        BindyCsvDataFormat bindy = new BindyCsvDataFormat(com.company.MapBindyToJson.class);

        bindy.setLocale("default");
        csv.setDelimiter(";");
        camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

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
                                Object jsonMap;
                                Message msg = exchange.getIn();
                                List<List<String>> rows = (List<List<String>>)msg.getBody();
                                List<String> line = rows.get(0);

                                //Setting the output file name
                                msg.setHeader("CamelFileName", msg.getMessageId() + ".json");

                                //The 1st way of mapping to json
                                jsonMap = new MapToJson(line);

                                //The 2nd way of mapping to json
                                jsonMap = setMap(line);

                                msg.setBody(jsonMap);
                            }
                        })
                        .marshal(jacksonDataFormat)
                        .wireTap("file:" + pathOut)

                        .to("jms:queue:incomingOrders");

                //The third way of mapping to json format using Bindy - something wrong here
                from("file:" + pathInBindy + "?noop=true")

                        .choice().when (header("CamelFileName").endsWith(".csv"))
                        .split(body().tokenize("\n",1,true))
                        .unmarshal(bindy)
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Message msg = exchange.getIn();
                                //Setting the output file name
                                msg.setHeader("CamelFileName", "BindyMap_" + msg.getMessageId() + ".json");
                            }
                        })
                        .marshal(jacksonDataFormat)
                        .wireTap("file:" + pathOut)

                        .to("jms:queue:incomingOrders");
            }
        });

    }

public static HashMap<String, Object> setMap(List<String> line){
    HashMap<String, Object> hashMap = new HashMap<>();

    hashMap.put("UniqueID",line.get(0));
    hashMap.put("ProductCode",line.get(1));
    hashMap.put("ProductName",line.get(2));
    hashMap.put("PriceWholesale",Double.parseDouble(line.get(3)));
    hashMap.put("PriceRetail",Double.parseDouble(line.get(4)));
    hashMap.put("InStock",Integer.parseInt(line.get(5)));

    return hashMap;
}
}
