package com.zempty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zempty
 * @ClassName RabbitMqSender.java
 * @Description TODO
 * @createTime 2020年04月05日 15:19:00
 */
@Service
@EnableBinding(Barista.class)
public class RabbitMqSender {


    @Autowired
    private Barista barista;


    public void SendMessage(Object message, Map<String,Object> properties) {
        MessageHeaders mhs = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message, mhs);
        boolean sendStatus = barista.logoutput().send(msg);
        System.out.println("=========================================");
        System.out.println("发送数据： " + message + ", sendStatus :" + sendStatus);
    }
}
