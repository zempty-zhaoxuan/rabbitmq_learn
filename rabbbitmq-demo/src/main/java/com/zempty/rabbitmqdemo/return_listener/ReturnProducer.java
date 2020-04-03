package com.zempty.rabbitmqdemo.return_listener;

import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;

public class ReturnProducer {

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
        String exchangeName = "zempty_return";
        String routeKey = "zempty_return";
        String error_key = "zempty_error";
        String exchangeType = "topic";
        String queueName = "zempty_return_queue";
        channel.exchangeDeclare(exchangeName, exchangeType);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routeKey);
        String message = "test confirm message";


        //测试路由不到指定 queue 的时候做一个反馈机制
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("replyCode :" +replyCode);
                System.out.println("replyText :" +replyText);
                System.out.println("exchange :" +exchange);
                System.out.println("routingKey :" +routingKey);
                System.out.println("properties :" +properties);
                System.out.println("body :" + new String(body));
            }
        });
        //demo1 : 测试正常发送
        channel.basicPublish(exchangeName, routeKey, true,null,message.getBytes());
        //demo2 : 测试路由不到绑定的 queue,但是 mandatory 设置为 true, returnlistener 起作用
//        channel.basicPublish(exchangeName, error_key, true,null,message.getBytes());
        //demo3 : 测试路由不到绑定的 queue，但是 mandatory 设置为 false
//        channel.basicPublish(exchangeName, error_key, false,null,message.getBytes());
    }
}
