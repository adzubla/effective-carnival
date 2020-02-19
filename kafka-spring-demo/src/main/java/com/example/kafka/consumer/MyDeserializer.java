package com.example.kafka.consumer;

import org.apache.kafka.common.header.Headers;

public class MyDeserializer extends org.apache.kafka.common.serialization.StringDeserializer {

    @Override
    public String deserialize(String topic, byte[] data) {
        System.out.println("@@@@@@@@@@@ " + new String(data));
        return super.deserialize(topic, data);
    }

    @Override
    public String deserialize(String topic, Headers headers, byte[] data) {
        System.out.println("%%%%%%%%%%% " + new String(data));
        return super.deserialize(topic, headers, data);
    }
}
