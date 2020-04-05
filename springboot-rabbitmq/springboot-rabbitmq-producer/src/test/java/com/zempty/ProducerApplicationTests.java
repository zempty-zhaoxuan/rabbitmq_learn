package com.zempty;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zempty
 * @ClassName ProducerApplicationTests.java
 * @Description TODO
 * @createTime 2020年04月05日 12:13:00
 */
@SpringBootTest
public class ProducerApplicationTests {


    @Autowired
    private RabbitSender rabbitSender;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    @Test
    public void testSender1() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", "12345");
        properties.put("sent_time", dateTimeFormatter.format(LocalDateTime.now()));
        rabbitSender.send("hello,zempty,welocome to springboot rabbitmq",properties);
    }

    @Test
    public void testSendOrder() {
        Order order = new Order().setId(UUID.randomUUID().toString())
                .setDescription("这是一个测试的 order");
        rabbitSender.sendOrder(order);
    }
}
