package com.example.scterm.client;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class IsoClient {
    private static Logger LOG = LoggerFactory.getLogger(IsoClient.class);

    private String hostname;
    private int port;
    private IsoMessageListener<IsoMessage> listener;
    private MessageFactory<IsoMessage> factory;
    private Iso8583Client<IsoMessage> client;

    public IsoClient() throws IOException {
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
        m.setValue(37, 127, IsoType.NUMERIC, 12);
        m.setValue(41, String.valueOf(numeric), IsoType.ALPHA, 16);
        m.setValue(43, text, IsoType.ALPHA, 40);
        return m;
    }

}
