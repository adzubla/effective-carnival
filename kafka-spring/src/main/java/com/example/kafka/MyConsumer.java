package com.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MyConsumer {

    private static Logger LOG = LoggerFactory.getLogger(MyConsumer.class);

    @KafkaListener(topics = "my-topic")
    public void listen(ConsumerRecord<?, ?> record) throws Exception {
        LOG.info(String.format("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value()));
    }

}
