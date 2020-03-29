package org.llucbb.rabbitmqconsumer.rabbitmq;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents RabbitMQ Header
 */
@Data
public class RabbitmqHeader {

    private static final String KEYWORD_QUEUE_WAIT = "wait";

    private List<RabbitmqHeaderXDeath> deaths = new ArrayList<>(2);

    private String firstDeathExchange = StringUtils.EMPTY;

    private String firstDeathQueue = StringUtils.EMPTY;

    private String firstDeathReason = StringUtils.EMPTY;

    @SuppressWarnings("unchecked")
    public RabbitmqHeader(Map<String, Object> headers) {
        if (headers != null) {
            var firstDeathExchange = Optional.ofNullable(headers.get("x-first-death-exchange"));
            var firstDeathQueue = Optional.ofNullable(headers.get("x-first-death-queue"));
            var firstDeathReason = Optional.ofNullable(headers.get("x-first-death-reason"));

            firstDeathExchange.ifPresent(s -> setFirstDeathExchange(s.toString()));
            firstDeathQueue.ifPresent(s -> setFirstDeathQueue(s.toString()));
            firstDeathReason.ifPresent(s -> setFirstDeathReason(s.toString()));

            var xDeathHeaders = (List<Map<String, Object>>) headers.get("x-death");

            if (xDeathHeaders != null) {
                for (Map<String, Object> x : xDeathHeaders) {
                    RabbitmqHeaderXDeath hdrDeath = new RabbitmqHeaderXDeath();
                    var reason = Optional.ofNullable(x.get("reason"));
                    var count = Optional.ofNullable(x.get("count"));
                    var exchange = Optional.ofNullable(x.get("exchange"));
                    var queue = Optional.ofNullable(x.get("queue"));
                    var routingKeys = Optional.ofNullable(x.get("routing-keys"));
                    var time = Optional.ofNullable(x.get("time"));

                    reason.ifPresent(s -> hdrDeath.setReason(s.toString()));
                    count.ifPresent(s -> hdrDeath.setCount(Integer.parseInt(s.toString())));
                    exchange.ifPresent(s -> hdrDeath.setExchange(s.toString()));
                    queue.ifPresent(s -> hdrDeath.setQueue(s.toString()));
                    routingKeys.ifPresent(r -> {
                        var listR = (List<String>) r;
                        hdrDeath.setRoutingKeys(listR);
                    });
                    time.ifPresent(d -> hdrDeath.setTime((Date) d));

                    deaths.add(hdrDeath);
                }
            }
        }
    }

    public int getFailedRetryCount() {
        // get from queue "wait"
        for (var xDeath : deaths) {
            if (xDeath.getExchange().toLowerCase().endsWith(KEYWORD_QUEUE_WAIT)
                    && xDeath.getQueue().toLowerCase().endsWith(KEYWORD_QUEUE_WAIT)) {
                return xDeath.getCount();
            }
        }
        return 0;
    }
}
