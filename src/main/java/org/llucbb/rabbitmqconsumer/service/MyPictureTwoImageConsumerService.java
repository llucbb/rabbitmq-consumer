package org.llucbb.rabbitmqconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.llucbb.rabbitmqconsumer.model.Picture;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MyPictureTwoImageConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Dead letter exchange to handle errors at consumer using org.springframework.amqp.core.Message and
     * com.rabbitmq.client.Channel.
     */
    @RabbitListener(queues = "q.mypicture.image", ackMode = "MANUAL")
    public void listen(Message message, Channel channel) throws IOException {
        var p = objectMapper.readValue(message.getBody(), Picture.class);

        if (p.getSize() > 5000) {
            // Reject a message. Requeue parameter is true if the rejected message should be requeued rather than
            // dead-lettered. In that case will be dead-lettered to q.mypicture.dlx queue.
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }

        log.info("On image : {}", p.toString());
        // The listener must acknowledge all messages as acknowledge mode has been set to manual
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
