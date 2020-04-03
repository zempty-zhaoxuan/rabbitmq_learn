package com.zempty.rabbitmqdemo.limit;

import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;


public class LimitCustomer {
    @SneakyThrows
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setPassword("zempty123");
        factory.setUsername("zempty");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = "test01";
        String exchangeName = "zempty_limit";
        String exchangeType = "topic";
        String routeKey = "123.#";
        channel.exchangeDeclare(exchangeName, exchangeType,true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routeKey);
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received '" + message + "'");
//                向 broker 进行反馈，设置成 false 表示 不支持反馈多个
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        //prefetchCount 1 表示 一次消费一个，false 是指 customer 级别的限流
        channel.basicQos(0,1,false);
            //autoAck 设置成 false
        channel.basicConsume(queueName, false,consumer);
    }
}
