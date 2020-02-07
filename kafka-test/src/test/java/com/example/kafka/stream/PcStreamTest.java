package com.example.kafka.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PcStreamTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> pcInfo;
    private TestInputTopic<String, String> pcStatus;
    private TestOutputTopic<String, String> pcJoined;

    @BeforeEach
    public void init() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "test:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        testDriver = new TopologyTestDriver(PcStream.createTopology(), props);

        pcInfo = testDriver.createInputTopic("pc-info", new StringSerializer(), new StringSerializer());
        pcStatus = testDriver.createInputTopic("pc-status", new StringSerializer(), new StringSerializer());
        pcJoined = testDriver.createOutputTopic("pc-joined", new StringDeserializer(), new StringDeserializer());
    }

    @AfterEach
    public void close() {
        testDriver.close();
    }

    @Test
    public void testTopology() {
        pcInfo.pipeInput("1", "Shopping Barueri");
        pcInfo.pipeInput("2", "Shopping Iguatemi");
        pcInfo.pipeInput("3", "Farmacia");

        KeyValue<String, String> kv;

        pcStatus.pipeInput("1", "on");
        kv = pcJoined.readKeyValue();
        assertEquals("1", kv.key);
        assertEquals("on,Shopping Barueri", kv.value);

        pcStatus.pipeInput("3", "off");
        kv = pcJoined.readKeyValue();
        assertEquals("3", kv.key);
        assertEquals("off,Farmacia", kv.value);

        pcStatus.pipeInput("2", "on");
        kv = pcJoined.readKeyValue();
        assertEquals("2", kv.key);
        assertEquals("on,Shopping Iguatemi", kv.value);

        pcStatus.pipeInput("1", "on");
        kv = pcJoined.readKeyValue();
        assertEquals("1", kv.key);
        assertEquals("on,Shopping Barueri", kv.value);
    }

}
