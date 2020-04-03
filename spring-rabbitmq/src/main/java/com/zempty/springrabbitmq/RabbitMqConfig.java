package com.zempty.springrabbitmq;


import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;

import java.util.HashMap;
import java.util.Map;
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
        container.setQueueNames("direct_queue_spring","topic_queue_spring","fanout_queue_spring","topic2_queue_spring");
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


        // 第二种方法使用 MessageListenerAdapter 可以使用自定义的 MessageDelegate 处理消息
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        //默认处理消息的方法是 handleMessage ，也可以通过下面的方法自己定义
//        adapter.setDefaultListenerMethod("consumeMessage");
//        //使用自定义的 TextMessageConverter 来处理消息
//        adapter.setMessageConverter(new TextMessageConverter());

        // 第三种方法 ：MessageListenerAdapter 的 队列名称和方法保持一致
//        Map<String, String> queueOrTagMethodName = new HashMap<>();
//        queueOrTagMethodName.put("topic_queue_spring", "method1");
//        queueOrTagMethodName.put("topic2_queue_spring","method2");
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setQueueOrTagToMethodName(queueOrTagMethodName);
//        container.setMessageListener(adapter);


        // 第四种方法：支持 json 格式的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);


        //第五种方法：支持实体类的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        defaultJackson2JavaTypeMapper.setTrustedPackages("*");
//        jackson2JsonMessageConverter.setJavaTypeMapper(defaultJackson2JavaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);


        // 第六种方法： 支持实体类转换使用 map
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        Map<String, Class<?>> classMap = new HashMap<>();
//        classMap.put("order", Order.class);
//        defaultJackson2JavaTypeMapper.setIdClassMapping(classMap);
//        jackson2JsonMessageConverter.setJavaTypeMapper(defaultJackson2JavaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);





        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

//        这是一个全局的转换器，支持各种转换器
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textMessageConverter = new TextMessageConverter();
        converter.addDelegate("text", textMessageConverter);
        converter.addDelegate("html/text", textMessageConverter);
        converter.addDelegate("xml/text",textMessageConverter);
        converter.addDelegate("`text/plain",textMessageConverter);

        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        converter.addDelegate("json",jsonMessageConverter);
        converter.addDelegate("application/json",jsonMessageConverter);


        //测试接收图片的信息
        ImageMessageConverter imageMessageConverter = new ImageMessageConverter();
        converter.addDelegate("image/png", imageMessageConverter);
        converter.addDelegate("image", imageMessageConverter);



        adapter.setMessageConverter(converter);



        container.setMessageListener(adapter);
        return container;
    }
}
