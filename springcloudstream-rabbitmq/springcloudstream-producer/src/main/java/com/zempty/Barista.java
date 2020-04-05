package com.zempty;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author zempty
 * @ClassName Barista.java
 * @Description TODO
 * @createTime 2020年04月05日 15:17:00
 */
public interface Barista {

    String OUTPUT_CHANNEL = "output_channel";


    @Output(Barista.OUTPUT_CHANNEL)
    MessageChannel logoutput();

}
