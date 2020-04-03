package com.zempty.rabbitmqdemo.firstdemo;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Customer {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setPassword("zempty123");
        factory.setUsername("zempty");
        try(Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            String queueName = "test01";
            channel.queueDeclare("test01", true, false, false, null);
            DefaultConsumer consumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" Received '" + message + "'");
                }
            };
            while (true) {
                String consumerTag = channel.basicConsume(queueName, true,consumer);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
