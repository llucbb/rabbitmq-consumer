package org.llucbb.rabbitmqconsumer.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Date;

/**
 * @see org.llucbb.rabbitmqconsumer.rabbitmq.DlxProcessingErrorHandler
 */
@Slf4j
@Getter
public class DlxDirectProcessingErrorHandler implements DlxProcessingErrorHandler {

    @NonNull
    private final String deadExchangeName;

    private final int maxRetryCount = 3;

    /**
     * Constructor. Will retry for n times (default is 3) and on the next retry will consider message as dead, put it on
     * dead exchange with given <code>dlxExchangeName</code> and <code>routingKey</code>
     *
     * @param deadExchangeName dead exchange name. Not a dlx for work queue, but exchange name for really dead message
     *                         (wont processed anymore).
     * @throws IllegalArgumentException if <code>dlxExchangeName</code> or <code>dlxRoutingKey</code> is null or empty.
     */
    public DlxDirectProcessingErrorHandler(String deadExchangeName) throws IllegalArgumentException {
        if (StringUtils.isEmpty(deadExchangeName)) {
            throw new IllegalArgumentException("Must define dlx exchange name");
        }
        this.deadExchangeName = deadExchangeName;
    }

    /**
     * @see org.llucbb.rabbitmqconsumer.rabbitmq.DlxProcessingErrorHandler#handleErrorProcessingMessage(Message,
     * Channel)
     * <p>
     * This implementation will put message to dead letter exchange for <code>maxRetryCount</code> times, thus two
     * variables are required when creating this object:
     * <code>dlxExchangeName</code> and <code>dlxRoutingKey</code>. <br/>
     * <code>maxRetryCount</code> is 3 by default.
     */
    @Override
    public void handleErrorProcessingMessage(Message message, Channel channel) {
        var rabbitMqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

        try {
            if (rabbitMqHeader.getFailedRetryCount() >= maxRetryCount) {
                // publish to dead and ack
                log.warn("[DEAD] Error at " + new Date() + " on retry " + rabbitMqHeader.getFailedRetryCount()
                        + " for message " + message);

                channel.basicPublish(getDeadExchangeName(), message.getMessageProperties().getReceivedRoutingKey(),
                        null, message.getBody());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                log.warn("[REQUEUE] Error at " + new Date() + " on retry " + rabbitMqHeader.getFailedRetryCount()
                        + " for message " + message);

                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            }

        } catch (IOException e) {
            log.error("[HANDLER-FAILED] Error at " + new Date() + " on retry " + rabbitMqHeader.getFailedRetryCount()
                    + " for message " + message);
        }
    }
}