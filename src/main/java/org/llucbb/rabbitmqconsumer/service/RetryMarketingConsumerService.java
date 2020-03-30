package org.llucbb.rabbitmqconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.llucbb.rabbitmqconsumer.model.Employee;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RetryMarketingConsumerService {

    private final ObjectMapper objectMapper;

    public RetryMarketingConsumerService() {
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = "q.guideline2.marketing.work", ackMode = "MANUAL")
    public void listen(Message message, Channel channel) throws IOException {
        var e = objectMapper.readValue(message.getBody(), Employee.class);
        log.info("On marketing : " + e);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}