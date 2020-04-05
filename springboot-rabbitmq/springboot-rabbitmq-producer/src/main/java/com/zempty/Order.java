package com.zempty;


/**
 * @author zempty
 * @ClassName Order.java
 * @Description TODO
 * @createTime 2020年04月05日 13:50:00
 */
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Setter
@Getter
@Accessors(chain = true)
@ToString
public class Order implements Serializable {


    private String id;

    private String description;
}
