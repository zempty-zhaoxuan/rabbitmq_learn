package com.zempty.rabbitmqdemo.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicProducer {


    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.zempty.sg");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setUsername("zempty");
        factory.setPassword("zempty123");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            String message = "hello world123";
            //生产者把信息投递到 zempty1的exchange
            String exchangeName = "zempty2_topic";
            String routeKey1 = "123";
            String routeKey2 = "123.456";
            String routeKey3 = "123.457.zhao";
            channel.basicPublish(exchangeName, routeKey1 , null, message.getBytes());
            channel.basicPublish(exchangeName, routeKey2 , null, message.getBytes());
            channel.basicPublish(exchangeName, routeKey3 , null, message.getBytes());

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
