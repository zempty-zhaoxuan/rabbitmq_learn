package com.zempty;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/**
 * @author zempty
 * @ClassName RabbitmqReceiver.java
 * @Description TODO
 * @createTime 2020年04月05日 15:37:00
 */
@Service
@EnableBinding(Barista.class)
public class RabbitmqReceiver {


    @Autowired
    private Barista barista;

    @StreamListener(Barista.INPUT_CHANNEL)
    @SneakyThrows
    public void receiver(Message message) {
        Channel channel =(Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        System.out.println("===================================");
        System.out.println("接收数据： " + message.getPayload());
        channel.basicAck(deliveryTag, false);
    }
}
