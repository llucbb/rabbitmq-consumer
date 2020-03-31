package org.llucbb.rabbitmqconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.llucbb.rabbitmqconsumer.model.Picture;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class SpringRetryPictureConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "q.spring.image.work")
    public void listerImage(String message) throws IOException {
        var picture = objectMapper.readValue(message, Picture.class);
        log.info("Consuming image : {}", picture.getName());

        if (picture.getSize() > 9000) {
            throw new IOException(String.format("Image %s size too large : %d", picture.getName(), picture.getSize()));
        }

        log.info("Creating thumbnail and publishing image : {}", picture.getName());
    }

    @RabbitListener(queues = "q.spring.vector.work")
    public void listerVector(String message) throws IOException {
        var picture = objectMapper.readValue(message, Picture.class);
        log.info("Consuming vector : {}", picture.getName());
        log.info("Converting to image, creating thumbnail and publishing image : {}", picture.getName());
    }
}
