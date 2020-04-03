package com.zempty.springrabbitmq;

import com.rabbitmq.client.AMQP;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringRabbitmqApplicationTests {


    @Autowired
    private RabbitAdmin admin;

    @Autowired
    private RabbitTemplate template;


    @Test
    public void testAdmin() {
        // 定义三中常用的 exchange
        admin.declareExchange(new DirectExchange("direct_exchange_spring"));
        admin.declareExchange(new TopicExchange("topic_exchange_spring"));
        admin.declareExchange(new FanoutExchange("fanout_exchange_spring"));

        // 定义三个queue
        String direct = admin.declareQueue(new Queue("direct_queue_spring"));
        String topic = admin.declareQueue(new Queue("topic_queue_spring"));
        String fanout = admin.declareQueue(new Queue("fanout_queue_spring"));


        //建立绑定的关系
        admin.declareBinding(new Binding(direct, Binding.DestinationType.QUEUE, "direct_exchange_spring", "zempty.zhao", null));
        admin.declareBinding(new Binding(topic, Binding.DestinationType.QUEUE, "topic_exchange_spring", "zempty.#", null));
        admin.declareBinding(new Binding(fanout, Binding.DestinationType.QUEUE, "fanout_exchange_spring", "", null));


        //另外一中初始化的方法：
        admin.declareBinding(BindingBuilder
                .bind(new Queue("topic2_queue_spring"))
                .to(new TopicExchange("topic2_exchange_spring"))
                .with("zempty2.#"));

    }

    @Test
    public void testTemplate() {
        template.convertAndSend("topic_exchange_spring","zempty","hello world");
    }

}
