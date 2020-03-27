package org.llucbb.rabbitmqconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HelloRabbitConsumerService {

    @RabbitListener(queues = "course.hello")
    public void listen(String message) {
        log.info("Consuming " + message);
    }
}
