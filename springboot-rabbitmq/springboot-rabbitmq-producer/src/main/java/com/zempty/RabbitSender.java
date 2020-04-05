package com.zempty;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author zempty
 * @ClassName RabbitSender.java
 * @Description TODO
 * @createTime 2020年04月05日 11:58:00
 */
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println("correlationData :" + correlationData);
            System.out.println("ack :" + ack);
            if (!ack) {
                System.out.println("异常处理。。。");
            }
        }
    };

    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("exchange :"+ exchange + " , routingKey :" +routingKey + " , replyCode :"+ replyCode +" , replyText :"+replyText);
        }
    };

    public void send(Object message, Map<String, Object> properties) {
        MessageHeaders mhs = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message, mhs);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData cd = new CorrelationData();
        cd.setId(UUID.randomUUID().toString());// id 一般设置全局唯一，可以跟时间戳
        rabbitTemplate.convertAndSend("springboot_exchange","springboot.hello",msg,cd);
    }


    public void sendOrder(Order order) {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData cd = new CorrelationData();
        cd.setId(UUID.randomUUID().toString());// id 一般设置全局唯一，可以跟时间戳
        rabbitTemplate.convertAndSend("springboot_exchange2", "springboot.test", order, cd);
    }
}
