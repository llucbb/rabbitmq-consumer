package org.llucbb.rabbitmqconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.llucbb.rabbitmqconsumer.model.Employee;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MarketingConsumerService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "q.hr.marketing")
    public void listen(String message) {
        try {
            var emp = objectMapper.readValue(message, Employee.class);
            log.info("On marketing employee is {}", emp);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
