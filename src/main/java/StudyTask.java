import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.ArrayList;
import java.util.List;
import javax.jms.ConnectionFactory;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
//import org.apache.camel.model.dataformat.JsonLibrary;
//import org.apache.commons.csv.writer.CSVConfig;
//import org.apache.commons.csv.writer.CSVField;


public class StudyTask {
    public static void main(String[] args) throws Exception {
        String pathIn = "D:/Development/Project/Files/In";
        String pathOut = "D:/Development/Project/Files/Test";
        CamelContext camelContext = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        camelContext.addComponent("jms",JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure()  throws Exception {
                CsvDataFormat csv = new CsvDataFormat();
                JsonDataFormat json = new JsonDataFormat();

                csv.setSkipHeaderRecord(true);
                csv.setDelimiter(";");

                onException(Exception.class)
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,Throwable.class);
                        System.out.println("-------------------test exception----------------------------------------------------------------------");
                        System.out.println(caused.getMessage());
                    }
                        });

                 from("file:" + pathIn + "?noop=true")
                .choice().when (header("CamelFileName").endsWith(".csv"))
                .unmarshal(csv)
                .split(body())
                         .parallelProcessing()
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Message msg = exchange.getIn();

                        List<String> rows = (List<String>)msg.getBody();
                        msg.setHeader("CamelFileName", msg.getMessageId() + ".json");
                        //create json-formatted text
                        // ConvertToJson convJson = new ConvertToJson(rows);
                        //msg.setBody(convJson.resString);

                    }
                })
                .marshal(json) //  .marshal(csv) doesn't workfor the csv splitted

                .wireTap("file:" + pathOut)

                //Exception occurred during execution on the exchange: Exchange[ID-DESKTOP-JD6PL56-1615908974322-0-6]
                .to("jms:queue:incomingOrders");

            }
        });
        camelContext.start();

//sleep while file is copying
        Thread.sleep(3000);
        camelContext.stop();

//Cannot resolve method SetConfig in CsvDataFormat

//        CsvDataFormat csv = new CsvDataFormat();
//        CSVConfig csvConf = new CSVConfig();
//        csvConf.setDelimiter(';');
//        csvConf.addField(new CSVField("UniqueID"));
//        csvConf.addField(new CSVField("ProductCode"));
//        csvConf.addField(new CSVField("ProductName"));
//        csvConf.addField(new CSVField("PriceWholeSale"));
//        csvConf.addField(new CSVField("PriceRetail"));
//        csvConf.addField(new CSVField("InStock"));
//        csv.SetConfig(csvCon);
    }
}