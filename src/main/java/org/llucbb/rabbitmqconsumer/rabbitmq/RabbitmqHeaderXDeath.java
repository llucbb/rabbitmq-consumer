package org.llucbb.rabbitmqconsumer.rabbitmq;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * Represents RabbitMQ Header, part x-death.
 */
@Data
@EqualsAndHashCode
public class RabbitmqHeaderXDeath {

    private int count;
    private String exchange;
    private String queue;
    private String reason;
    private List<String> routingKeys;
    private Date time;

}
