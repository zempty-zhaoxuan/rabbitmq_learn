package com.zempty;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author zempty
 * @ClassName RabbitReceiver.java
 * @Description TODO
 * @createTime 2020年04月05日 13:28:00
 */
@Component
public class RabbitReceiver {


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "springboot_queue", durable = "true"),
            exchange= @Exchange(value = "springboot_exchange", durable = "true",
            type = "topic", ignoreDeclarationExceptions = "true"),
            key="springboot.#"
    ))
    @RabbitHandler
    @SneakyThrows
    public void onMessage(Message message, Channel channel) {
        System.out.println("========================================================");
        System.out.println("消费端 Payload :" +message.getPayload());
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工 ACK
        channel.basicAck(deliveryTag, false);
    }


//    详情见 application.properties 的配置文件,读取配置文件的配置
//    spring.rabbitmq.listener.order.queue.name=springboot_queue2
//    spring.rabbitmq.listener.order.queue.durable=true
//    spring.rabbitmq.listener.order.exchange.name=springboot_exchange2
//    spring.rabbitmq.listener.order.exchange.durable=true
//    spring.rabbit.listener.order.exchange.type=topic
//    spring.rabbit.listener.order.ignoreDeclarationExceptions=true
//    spring.rabbit.listener.order.key=springboot.#

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange= @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbit.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbit.listener.order.ignoreDeclarationExceptions}"),
            key="${spring.rabbit.listener.order.key}"
    ))
    @RabbitHandler
    @SneakyThrows
    public void onOrderMessage(@Payload Order order, @Headers Map<String, Object> headers, Channel channel) {
        System.out.println("==============================================================");
        System.out.println(order);
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag,false);

    }
}
