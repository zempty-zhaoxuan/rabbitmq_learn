package com.zempty.rabbitmqdemo.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;

import java.io.IOException;

public class ConfirmProducer {

    @SneakyThrows
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接时候的参数
        factory.setUsername("zempty");
        factory.setPassword("zempty123");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setHost("localhost");

        //设置 connection
        Connection connection = factory.newConnection();

        //设置 Channel
        Channel channel = connection.createChannel();
        String exchangeName = "zempty_confirm";
        String routingKey = "zempty.#";
        String errorKey="test.error";
        String exchangeType = "topic";
        String queueName = "zempty_confirm_queue";
        channel.exchangeDeclare(exchangeName, exchangeType);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        String message = "test confirm message";

        //确认返回消息
        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("info received succeed");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("infos don not received ,please check");
            }
        });

        channel.basicPublish(exchangeName, "zegjakg", null, message.getBytes());
    }
}
