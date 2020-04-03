package com.zempty.springrabbitmq;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class RabbitMqConfig {


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses("localhost:5672");
        cachingConnectionFactory.setUsername("zempty");
        cachingConnectionFactory.setPassword("zempty123");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        return template;
    }



//    String direct = admin.declareQueue(new Queue("direct_queue_spring"));
//    String topic = admin.declareQueue(new Queue("topic_queue_spring"));
//    String fanout = admin.declareQueue(new Queue("fanout_queue_spring"));
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("direct_queue_spring","topic_queue_spring","fanout_queue_spring");
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        //不需要重回队列
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue+" - "+ UUID.randomUUID().toString();
            }
        });

        //第一种方式直接通过 MessageListener 来实现消息的消费
//        container.setMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(Message message) {
//                String msg = new String(message.getBody());
//                System.out.println("收到的消息是" + msg);
//            }
//        });



        return container;
    }
}
