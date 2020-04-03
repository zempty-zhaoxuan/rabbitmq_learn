package com.zempty.rabbitmqdemo.dlx;

import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DlxCustomer {
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
        String queueName = "test_dlx";
        String exchangeName = "zempty_dlx_exchange";
        String exchangeType = "topic";
        String routeKey = "ack.#";

        //下面定义死信队列的绑定
        String dlxExchange = "dlx_exchange";
        String dlxQueueName = "dlx_queue";
        String routingKey = "#";
        channel.exchangeDeclare(dlxExchange, exchangeType, true);
        channel.queueDeclare(dlxQueueName, true, false, false, null);
        channel.queueBind(dlxQueueName, dlxExchange, routingKey);

        channel.exchangeDeclare(exchangeName, exchangeType,true);
        // 设置死信队列
        Map<String,Object> map = new HashMap();
        map.put("x-dead-letter-exchange", "dlx_exchange");
        channel.queueDeclare(queueName, true, false, false, map);
        channel.queueBind(queueName, exchangeName, routeKey);
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received '" + message + "'");
            }
        };
        channel.basicConsume(queueName, false,consumer);
    }

}
