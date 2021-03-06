package com.example.kafka.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class PcStream {

    public static void main(String[] args) {
        PcStream pcStream = new PcStream();

        Topology topology = pcStream.getTopology();
        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, pcStream.getProperties());
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("pc-stream-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    public Properties getProperties() {
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "pc-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        return props;
    }

    public Topology getTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, String> infoTable = builder.table("pc-info");
        KStream<String, String> statusStream = builder.stream("pc-status");
        KStream<String, String> resultStream = statusStream.join(infoTable, (left, right) -> left + "," + right);
        resultStream.to("pc-joined");

        return builder.build();
    }

}
