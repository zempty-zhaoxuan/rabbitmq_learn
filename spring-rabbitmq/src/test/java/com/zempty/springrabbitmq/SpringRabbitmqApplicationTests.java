package com.zempty.springrabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
        TopicExchange topicExchange = new TopicExchange("topic2_exchange_spring");
        Queue queue = new Queue("topic2_queue_spring");
        admin.declareExchange(topicExchange);
        admin.declareQueue(queue);
        admin.declareBinding(BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with("zempty2.#"));

    }

    @Test
    public void testTemplate() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("zempty is handsome".getBytes(), messageProperties);
        template.send("topic_exchange_spring", "zempty", message);
    }


    //发送 json 信息
    @Test
    @SneakyThrows
    public void testSendJsonMessage() {
        Order order = new Order().setId("1")
                .setName("订单")
                .setContent("订单信息");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.out.println("order 的 json 信息：" + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);
        template.send("topic2_exchange_spring","zempty2",message);
    }



    @Test
    @SneakyThrows
    //测试消费端使用 java 类接收
    public void testSendJavaMessage() {
        Order order = new Order().setId("1")
                .setName("订单")
                .setContent("订单信息");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.out.println("order 的 json 信息：" + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "com.zempty.springrabbitmq.Order");
        Message message = new Message(json.getBytes(), messageProperties);
        template.send("topic2_exchange_spring","zempty2",message);
    }



    @Test
    @SneakyThrows
    // 测试消费类接收 java 类
    public void testSendMappingMessage() {
        Order order = new Order().setId("1")
                .setName("订单")
                .setContent("订单信息");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.out.println("order 的 json 信息：" + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

//        关键区别在这里是指定的 order并不是全类名
        messageProperties.getHeaders().put("__TypeId__", "order");
        Message message = new Message(json.getBytes(), messageProperties);
        template.send("topic2_exchange_spring","zempty2",message);
    }


    @Test
    @SneakyThrows
    //测试消费类接收文件
    public void testSendExtMessage() {
        byte[] body = Files.readAllBytes(Paths.get("./", "test.png"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("image/png");
        Message message = new Message(body, messageProperties);
        template.send("topic2_exchange_spring","zempty2",message);
    }


}
