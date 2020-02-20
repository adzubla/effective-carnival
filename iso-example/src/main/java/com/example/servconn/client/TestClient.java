package com.example.servconn.client;

import com.example.servconn.Util;
import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

public class TestClient {
    private static Logger LOG = LoggerFactory.getLogger(TestClient.class);

    private String hostname;
    private int port;
    private IsoMessageListener<IsoMessage> listener;
    private MessageFactory<IsoMessage> factory;
    private Iso8583Client<IsoMessage> client;

    public TestClient() throws IOException {
        factory = ConfigParser.createFromClasspathConfig("j8583-config.xml");
        factory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        factory.setUseBinaryMessages(false);
        factory.setAssignDate(true);
        factory.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setListener(IsoMessageListener<IsoMessage> listener) {
        this.listener = listener;
    }

    private void connect() throws InterruptedException {
        SocketAddress socketAddress = new InetSocketAddress(hostname, port);
        client = new Iso8583Client<>(socketAddress, factory);
        client.addMessageListener(listener);
        //client.getConfiguration().replyOnError();
        client.init();
        client.connect();
        if (!client.isConnected()) {
            throw new IllegalStateException("Unconnected");
        }
    }

    private void sendMessage(int numeric, String text) throws InterruptedException {
        IsoMessage message = buildMessage(factory, numeric, text);
        Util.print("-- REQUEST --------------------------------", message);
        client.send(message);
    }

    private void close() {
        client.shutdown();
    }

    private IsoMessage buildMessage(MessageFactory<IsoMessage> messageFactory, int numeric, String text) {
        IsoMessage m = messageFactory.newMessage(0x200);
        m.setValue(4, new BigDecimal("501.25"), IsoType.AMOUNT, 0);
        m.setValue(12, new Date(), IsoType.TIME, 0);
        m.setValue(15, new Date(), IsoType.DATE4, 0);
        m.setValue(17, new Date(), IsoType.DATE_EXP, 0);
        m.setValue(37, numeric, IsoType.NUMERIC, 12);
        m.setValue(41, text, IsoType.ALPHA, 16);
        return m;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        TestClient testClient = new TestClient();
        testClient.setHostname("localhost");
        testClient.setPort(7777);

        testClient.setListener(new IsoMessageListener<>() {

            @Override
            public boolean applies(IsoMessage isoMessage) {
                LOG.debug("applies: {}", isoMessage);
                return isoMessage.getType() == 0x210;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
                LOG.debug("onMessage: {} {}", channelHandlerContext, isoMessage);
                Util.print("-- RESPONSE -------------------------------", isoMessage);
                return false;
            }
        });

        testClient.connect();

        Thread.sleep(500);

        readInput(testClient);

        testClient.close();
    }

    private static void readInput(TestClient testClient) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            int id = scanner.nextInt();
            if (id == 0) {
                return;
            }
            String value = scanner.next();
            testClient.sendMessage(id, value);
            Thread.sleep(500);
        }
    }

}
