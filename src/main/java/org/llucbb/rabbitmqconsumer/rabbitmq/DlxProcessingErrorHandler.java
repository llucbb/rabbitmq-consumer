package org.llucbb.rabbitmqconsumer.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

/**
 * Handles RabbitMQ processing error that might occur on <code>try-catch</code>. This will not handle invalid message
 * conversion though (for example if you has Employee JSON structure to process, but got Animal JSON structure instead
 * from Rabbit MQ queue).
 */
public interface DlxProcessingErrorHandler {

    /**
     * Handle AMQP message consume error
     *
     * @param message AMQP message that caused error
     * @param channel channel for AMQP message
     */
    void handleErrorProcessingMessage(Message message, Channel channel);
}
