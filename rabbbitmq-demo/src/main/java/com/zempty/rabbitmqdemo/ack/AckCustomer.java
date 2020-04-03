package com.zempty.rabbitmqdemo.ack;

import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;

public class AckCustomer {

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
        String queueName = "test_ack";
        String exchangeName = "zempty_ack";
        String exchangeType = "topic";
        String routeKey = "ack.#";
        channel.exchangeDeclare(exchangeName, exchangeType,true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routeKey);
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received '" + message + "'");
                if((Integer)(properties.getHeaders().get("num")) == 0){
                    // 最后一个参数如果是 true 就返回队列
                    channel.basicNack(envelope.getDeliveryTag(), false, true);
                }else {
//                向 broker 进行反馈，设置成 false 表示 不支持反馈多个
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        //该行暂不使用，不限流
//        channel.basicQos(0,1,false);
        //autoAck 设置成 false
        channel.basicConsume(queueName, false,consumer);
    }
}
