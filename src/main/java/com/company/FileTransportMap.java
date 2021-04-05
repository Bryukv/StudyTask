package com.company;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.CsvDataFormat;
import java.util.HashMap;
import java.util.List;

public class FileTransportMap   implements Processor{
    public FileTransportMap(CamelContext camelContext,String pathIn,String pathOut) throws Exception {


        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        CsvDataFormat csv = new CsvDataFormat();
        FileTransportMap ftm = this;
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
                        .process(ftm)
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

        //Setting the output file name
        msg.setHeader("CamelFileName", "Map_" + msg.getMessageId() + ".json");
        //Set the message body as a HashMap
        msg.setBody(setMap(line));

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