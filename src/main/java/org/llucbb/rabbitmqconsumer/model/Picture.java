package org.llucbb.rabbitmqconsumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Picture {

    private String name;
    private String type;
    private String source;
    private long size;

}
