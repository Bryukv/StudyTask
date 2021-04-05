package com.company;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.CsvDataFormat;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileMove extends RouteBuilder implements Processor {
    String pathIn;
    String pathOut;
    JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
    CsvDataFormat csv = new CsvDataFormat();
    int counter;

    public FileMove(String pathIn, String pathOut) throws Exception {

        this.pathIn = pathIn;
        this.pathOut = pathOut;
        this.csv.setDelimiter(";");
        this.counter = 0;
    }
    @Override
    public void configure() throws Exception{
        from("file:" + this.pathIn + "?noop=true")
                .choice().when (header("CamelFileName").endsWith(".csv"))
                .split(body().tokenize("\n",1,true)).streaming().parallelProcessing()
                .unmarshal(csv)
                .process(this)
                .marshal(jacksonDataFormat)
                .wireTap("file:" + this.pathOut)
                .to("jms:queue:incomingOrders");
    }
    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg = exchange.getIn();
        List<List<String>> rows = (List<List<String>>)msg.getBody();
        List<String> line = rows.get(0);

        this.counter++;
        //System.out.println("  counter after =" + this.counter  + "   tread " + Thread.currentThread().toString() +"  " + line.get(0));
        //Setting the output file name
        String filename = msg.getHeader("CamelFileName").toString().replace(".csv","_" + line.get(0) + ".json");
        msg.setHeader("CamelFileName", filename);
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
