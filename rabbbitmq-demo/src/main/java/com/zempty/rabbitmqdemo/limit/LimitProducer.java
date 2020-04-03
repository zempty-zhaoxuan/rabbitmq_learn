package com.zempty.rabbitmqdemo.limit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LimitProducer {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setUsername("zempty");
        factory.setPassword("zempty123");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            for (int i = 0; i <5 ; i++) {
                String message = "hello world123";
                //生产者把信息投递到 zempty1的exchange
                String exchangeName = "zempty_limit";
                String routeKey = "123";
                channel.basicPublish(exchangeName, routeKey , null, message.getBytes());
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
