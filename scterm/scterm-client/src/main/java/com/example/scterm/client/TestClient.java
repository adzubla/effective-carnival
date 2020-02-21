package com.example.scterm.client;

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

    private static void multiClients() throws IOException, InterruptedException {
        TestClient[] clients = new TestClient[N];

        for (int i = 0; i < N; i++) {
            System.out.println("Connection #" + i);
            clients[i] = getTestClient();
            clients[i].connect();
        }
        System.out.println("All connected!");

        Thread.sleep(1000 * 2);

        System.out.println("Sending!");

        long pid = ProcessHandle.current().pid();

        while (true) {
            for (int i = 0; i < N; i++) {
                clients[i].sendMessage(pid, "msg" + i);
                Thread.sleep(500);
            }
        }
    }

    private static void prompt() throws InterruptedException, IOException {
        TestClient testClient = getTestClient();

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
            Thread.sleep(100);
        }
    }

    private static TestClient getTestClient() throws IOException {
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
                LOG.info("{} -> {}", isoMessage.getField(41).getValue(), isoMessage.getField(126).getValue());
                return false;
            }
        });
        return testClient;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length > 0) {
            prompt();
        } else {
            multiClients();
        }
    }

    private static final int N = 64;

    public void connect() throws InterruptedException {
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

    public void close() {
        client.shutdown();
    }

    public void sendMessage(long numeric, String text) throws InterruptedException {
        IsoMessage message = buildMessage(factory, numeric, text);
        LOG.debug("Sending {} {}", numeric, text);
        client.send(message);
    }

    private IsoMessage buildMessage(MessageFactory<IsoMessage> messageFactory, long numeric, String text) {
        IsoMessage m = messageFactory.newMessage(0x200);
        m.setValue(4, new BigDecimal("501.25"), IsoType.AMOUNT, 0);
        m.setValue(12, new Date(), IsoType.TIME, 0);
        m.setValue(15, new Date(), IsoType.DATE4, 0);
        m.setValue(17, new Date(), IsoType.DATE_EXP, 0);
        m.setValue(37, numeric, IsoType.NUMERIC, 12);
        m.setValue(41, text, IsoType.ALPHA, 16);
        return m;
    }

}
