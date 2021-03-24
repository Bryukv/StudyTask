package com.company;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class StudyTask {
    public static void main(String[] args) throws Exception {	// write your code here
        CamelContext camelContext = new DefaultCamelContext();
        FileTransport fileTransport = new FileTransport(camelContext);
        camelContext.start();
        Thread.sleep(15000);
        camelContext.stop();
        }
}