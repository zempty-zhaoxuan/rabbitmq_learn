package com.zempty.springrabbitmq;

import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

/**
 * @author zempty
 * @ClassName ImageMessageConverter.java
 * @Description TODO
 * @createTime 2020年04月03日 14:43:00
 */
public class ImageMessageConverter implements MessageConverter {
    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(o.toString().getBytes(), messageProperties);
    }

    @Override
    @SneakyThrows
    public Object fromMessage(Message message) throws MessageConversionException {

        System.out.println("------------------Image MessageConverter-----------------");
        Object _extName = message.getMessageProperties().getHeaders().get("extName");
        String extName = _extName == null ? "png" : _extName.toString();

        byte[] body = message.getBody();
        String fileName = UUID.randomUUID().toString()+"."+extName;
        String path = "./" + fileName;
        File file = new File(path);
        Files.copy(new ByteArrayInputStream(body), file.toPath());
        return file;
    }
}
