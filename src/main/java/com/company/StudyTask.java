package com.company;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

public class StudyTask {
    public static void main(String[] args) throws Exception {
        String pathIn = "D:/Development/Project/Files/In/Map";
        String pathOut = "D:/Development/Project/Files/Test";
        CamelContext camelContext = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        //FileTransport fileTransport = new FileTransport(camelContext);
        //FileTransportMap fileTransportMap = new FileTransportMap(camelContext,pathIn,pathOut);
        //FileTransportBindy fileTransportBindy = new FileTransportBindy(camelContext);

        FileMove fileMove = new FileMove(pathIn,pathOut);
        camelContext.addRoutes(fileMove);

        camelContext.start();
        Thread.sleep(5000);
        camelContext.stop();
        }
}