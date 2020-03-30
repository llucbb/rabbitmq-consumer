package org.llucbb.rabbitmqconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.llucbb.rabbitmqconsumer.model.Employee;
import org.llucbb.rabbitmqconsumer.rabbitmq.DlxFanoutProcessingErrorHandler;
import org.llucbb.rabbitmqconsumer.rabbitmq.DlxProcessingErrorHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RetryAccountingConsumerService {

    private static final String DEAD_EXCHANGE_NAME = "x.guideline2.dead";
    private static final String ROUTING_KEY = "accounting";

    private final DlxProcessingErrorHandler dlxFanoutProcessingErrorHandler;
    private final ObjectMapper objectMapper;

    public RetryAccountingConsumerService() {
        objectMapper = new ObjectMapper();
        dlxFanoutProcessingErrorHandler = new DlxFanoutProcessingErrorHandler(DEAD_EXCHANGE_NAME, ROUTING_KEY);
    }

    @RabbitListener(queues = "q.guideline2.accounting.work", ackMode = "MANUAL")
    public void listen(Message message, Channel channel) throws IOException {
        try {
            var emp = objectMapper.readValue(message.getBody(), Employee.class);

            if (StringUtils.isEmpty(emp.getName())) {
                throw new IllegalArgumentException("Name is empty");
            } else {
                log.info("On accounting : {}", emp);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }

        } catch (IllegalArgumentException e) {
            log.warn("Error processing message : {} : {}", new String(message.getBody()), e.getMessage());
            dlxFanoutProcessingErrorHandler.handleErrorProcessingMessage(message, channel);
        }
    }
}