package com.example.kafka.stream;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PcStreamTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> pcInfo;
    private TestInputTopic<String, String> pcStatus;
    private TestOutputTopic<String, String> pcJoined;

    @BeforeEach
    public void init() {
        PcStream pcStream = new PcStream();

        testDriver = new TopologyTestDriver(pcStream.getTopology(), pcStream.getProperties());

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
