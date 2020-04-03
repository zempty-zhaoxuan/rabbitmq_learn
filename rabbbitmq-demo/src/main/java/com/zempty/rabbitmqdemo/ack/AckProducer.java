package com.zempty.rabbitmqdemo.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AckProducer {
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
                Map<String, Object> map = new HashMap<>();
                map.put("num", i);
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                        .headers(map)
                        .deliveryMode(2)
                        .build();
                String message = "hello world123 "+i;
                //生产者把信息投递到 zempty1的exchange
                String exchangeName = "zempty_ack";
                String routeKey = "ack";
                channel.basicPublish(exchangeName, routeKey , properties, message.getBytes());
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
