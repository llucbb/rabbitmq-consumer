package org.llucbb.rabbitmqconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.llucbb.rabbitmqconsumer.model.Picture;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PictureFilterConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "q.picture.filter")
    public void listen(String message) throws JsonProcessingException {
        var p = objectMapper.readValue(message, Picture.class);
        log.info("On filter : {}", p.toString());

    }
}
