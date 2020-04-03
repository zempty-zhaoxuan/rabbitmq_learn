package com.zempty.springrabbitmq;

import java.io.File;
import java.util.Map;

/**
 * @author zempty
 * @ClassName MessageDelegate.java
 * @Description TODO
 * @createTime 2020年04月03日 11:58:00
 */
public class MessageDelegate {

//    public void handMessage(byte[] messageBody) {
//        System.out.println("默认方法，消息内容：" + new String(messageBody));
//    }
//
//    public void consumeMessage(byte[] messageBody) {
//        System.out.println("consumeMessage 方法，消息内容：" + new String(messageBody));
//    }

    public void consumeMessage(String messageBody) {
        System.out.println("字符串方法，消息内容："+ messageBody);
    }

    public void method1(String messageBody) {
        System.out.println("method1 ,消息内容是："+new String(messageBody));
    }
    public void method2(String messageBody) {
        System.out.println("method1 ,消息内容是："+new String(messageBody));
    }

    public void consumeMessage(Map messageBody) {
        System.out.println("map 方法，消息内容 ："+ messageBody);
    }


    public void consumeMessage(Order order) {
        System.out.println("order 方法，消息内容是："+order);
    }

    public void consumeMessage(File file) {
        System.out.println("图片方法，消息的内容是："+file.getName());
    }


}
