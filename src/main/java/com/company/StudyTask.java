package com.company;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

public class StudyTask {
    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        FileTransport fileTransport = new FileTransport(camelContext);
        FileTransportMap fileTransportMap = new FileTransportMap(camelContext);
        FileTransportBindy fileTransportBindy = new FileTransportBindy(camelContext);

        camelContext.start();
        Thread.sleep(15000);
        camelContext.stop();
        }
}