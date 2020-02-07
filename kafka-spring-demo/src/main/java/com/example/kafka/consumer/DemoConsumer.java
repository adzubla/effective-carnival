package com.example.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DemoConsumer {

    private static Logger LOG = LoggerFactory.getLogger(DemoConsumer.class);

    @KafkaListener(topics = "${demo.topic-name}")
    public void listen(ConsumerRecord<?, ?> record) throws Exception {
        LOG.info(String.format("Received offset = %d key = %s value = %s", record.offset(), record.key(), record.value()));
    }

}
