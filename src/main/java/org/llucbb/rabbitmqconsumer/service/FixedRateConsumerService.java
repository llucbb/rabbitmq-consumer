package org.llucbb.rabbitmqconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class FixedRateConsumerService {

    @RabbitListener(queues = "course.fixedrate", concurrency = "3")
    public void listen(String message) {
        log.info("Consuming {} on thread {}", message, Thread.currentThread().getName());

        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(2000));
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
