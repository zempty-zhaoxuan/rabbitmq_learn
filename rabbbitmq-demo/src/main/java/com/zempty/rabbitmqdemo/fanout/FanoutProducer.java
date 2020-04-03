package com.zempty.rabbitmqdemo.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutProducer {

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.zempty.sg");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setUsername("zempty");
        factory.setPassword("zempty123");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            String exchangeName = "zempty_fanout";
            String message = "test fannout function";
            channel.basicPublish(exchangeName, "", null , message.getBytes());
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
