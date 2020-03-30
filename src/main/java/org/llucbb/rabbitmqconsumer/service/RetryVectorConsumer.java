package org.llucbb.rabbitmqconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.llucbb.rabbitmqconsumer.model.Picture;
import org.llucbb.rabbitmqconsumer.rabbitmq.DlxDirectProcessingErrorHandler;
import org.llucbb.rabbitmqconsumer.rabbitmq.DlxProcessingErrorHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class RetryVectorConsumer {

    private static final String DEAD_EXCHANGE_NAME = "x.guideline.dead";

    private final DlxProcessingErrorHandler dlxDirectProcessingErrorHandler;
    private final ObjectMapper objectMapper;

    public RetryVectorConsumer() {
        objectMapper = new ObjectMapper();
        dlxDirectProcessingErrorHandler = new DlxDirectProcessingErrorHandler(DEAD_EXCHANGE_NAME);
    }

    @RabbitListener(queues = "q.guideline.vector.work", ackMode = "MANUAL")
    public void listen(Message message, Channel channel) {
        try {
            var p = objectMapper.readValue(message.getBody(), Picture.class);
            // process the image
            if (p.getSize() > 9000) {
                // throw exception, we will use DLX handler for retry mechanism
                throw new IOException("Size too large");
            } else {
                log.info("Convert to image, creating thumbnail, & publishing : " + p);
                // you must acknowledge that message already processed
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (IOException e) {
            log.warn("Error processing message : " + new String(message.getBody()) + " : " + e.getMessage());
            dlxDirectProcessingErrorHandler.handleErrorProcessingMessage(message, channel);
        }
    }
}