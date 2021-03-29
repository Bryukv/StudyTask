package com.company;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;

public class FileTransportBindy {
    public FileTransportBindy(CamelContext camelContext) throws Exception {
        String pathIn = "D:/Development/Project/Files/In/Bindy";
        String pathOut = "D:/Development/Project/Files/Test";

        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        BindyCsvDataFormat bindy = new BindyCsvDataFormat(com.company.MapBindyToJson.class);

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

}
