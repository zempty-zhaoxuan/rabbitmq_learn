package com.zempty;

import com.zempty.RabbitMqSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zempty
 * @ClassName com.zempty.ProducerApplicationTests.java
 * @Description TODO
 * @createTime 2020年04月05日 17:22:00
 */
@SpringBootTest
public class ProducerApplicationTests {


    @Autowired
    private RabbitMqSender rabbitMqSender;

    private final static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS");

    @Test
    public void testSendMessage() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", "12345");
        properties.put("sent_time", format.format(LocalDateTime.now()));
        for (int i = 0; i < 5 ; i++) {
            rabbitMqSender.SendMessage("zempty test spring cloud stream  "+ i,properties);
        }
    }


}
