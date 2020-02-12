package com.example.kafka.producer;

import com.example.kafka.DemoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Component
public class DemoProducer {
    private static Logger LOG = LoggerFactory.getLogger(DemoProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DemoProperties demoProperties;

    @Scheduled(fixedDelayString = "${demo.delay}")
    public void produce() {
        sendMessageWithCallback("Demo... " + Instant.now());
    }

    public void sendMessageAsynch(String value) {
        kafkaTemplate.send(demoProperties.getTopicName(), value);
    }

    public void sendMessageSynch(String value) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(demoProperties.getTopicName(), value);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOG.error("Unable to send value={} due to: {}", value, ex);
        }
    }

    public void sendMessageWithCallback(String value) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(demoProperties.getTopicName(), value);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("Sent - offset = {} key = {} value = {}", result.getRecordMetadata().offset(), result.getProducerRecord().key(), value);
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.error("Unable to send value={} due to: {}", value, ex);
            }
        });
    }

}
