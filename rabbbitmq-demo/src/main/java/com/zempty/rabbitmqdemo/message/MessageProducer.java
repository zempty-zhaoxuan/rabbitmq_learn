package com.zempty.rabbitmqdemo.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class MessageProducer {


    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.zempty.sg");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setUsername("zempty");
        factory.setPassword("zempty123");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            String message = "test message";
            Map<String, Object> headers = new HashMap<>();
            headers.put("zempty1", "cool");
            headers.put("zempty2", "handsome");
            AMQP.BasicProperties properties = new AMQP.BasicProperties()
                    .builder()
                    .deliveryMode(2)
                    .headers(headers)
                    .build();
            channel.basicPublish("", "test01", properties, message.getBytes());
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
