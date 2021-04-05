package com.company;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.CsvDataFormat;
import java.util.List;

public class FileTransport  implements Processor {
    public FileTransport(CamelContext camelContext) throws Exception {
        String pathIn = "D:/Development/Project/Files/In";
        String pathOut = "D:/Development/Project/Files/Test";

        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        CsvDataFormat csv = new CsvDataFormat();
        csv.setDelimiter(";");
        FileTransport ft = this;
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
                        .process(ft)
                        .marshal(jacksonDataFormat)
                        .wireTap("file:" + pathOut)

                        .to("jms:queue:incomingOrders");
            }
        });

    }
    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg = exchange.getIn();
        List<List<String>> rows = (List<List<String>>)msg.getBody();
        List<String> line = rows.get(0);

        //Set the output file name
        msg.setHeader("CamelFileName", msg.getMessageId() + ".json");
        //Set the message body as a MapToJson
        msg.setBody(new MapToJson(line));
    }

}
