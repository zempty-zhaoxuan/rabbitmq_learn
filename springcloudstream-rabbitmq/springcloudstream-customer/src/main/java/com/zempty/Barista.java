package com.zempty;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author zempty
 * @ClassName Barista.java
 * @Description TODO
 * @createTime 2020年04月05日 15:27:00
 */
public interface Barista {

    String INPUT_CHANNEL = "input_channel";

    @Input(Barista.INPUT_CHANNEL)
    SubscribableChannel loginput();
}
