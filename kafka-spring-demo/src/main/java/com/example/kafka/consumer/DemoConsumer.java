package com.example.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.stereotype.Component;

@Component
public class DemoConsumer {

    private static Logger LOG = LoggerFactory.getLogger(DemoConsumer.class);

    @KafkaListener(topics = "${demo.topic-name}")
    public void listen(ConsumerRecord<?, ?> record) throws Exception {
        LOG.info(String.format("Recv - offset = %d key = %s value = %s", record.offset(), record.key(), record.value()));
        for (Header h : record.headers()) {
            LOG.info("\theader: {}", h);
        }
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        RecordFilterStrategy<? super Object, ? super Object> myFilter = new RecordFilterStrategy<>() {
            @Override
            public boolean filter(ConsumerRecord<Object, Object> record) {
                for (Header h : record.headers()) {
                    if (h.key().equals("X-Custom-Header")) {
                        String value = new String(h.value());
                        //System.out.println("X-Custom-Header: " + value);
                        return valid(value);
                    }
                }
                return false;
            }

            private boolean valid(String value) {
                long n = Long.valueOf(value);
                return n % 2 == 0;
            }
        };
        factory.setRecordFilterStrategy(myFilter);

        configurer.configure(factory, kafkaConsumerFactory);

        return factory;
    }

}
