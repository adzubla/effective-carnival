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
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Cliente sincrono para ISO-8583. Não é thread-safe.
 */
public class SynchIsoClient {
    private static Logger LOG = LoggerFactory.getLogger(SynchIsoClient.class);

    private String hostname;
    private int port;
    private MessageFactory<IsoMessage> factory;
    private Iso8583Client<IsoMessage> client;

    private CountDownLatch latch;

    public SynchIsoClient() throws IOException {
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

    public void connect() throws InterruptedException {
        SocketAddress socketAddress = new InetSocketAddress(hostname, port);
        client = new Iso8583Client<>(socketAddress, factory);

        client.addMessageListener(new IsoMessageListener<>() {
            @Override
            public boolean applies(IsoMessage isoMessage) {
                return isoMessage.getType() == 0x210 || isoMessage.getType() == 0x800;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
                LOG.debug("onMessage: {} {}", ctx, isoMessage);
                if (isoMessage.getType() == 0x800) {
                    handleEcho(ctx, isoMessage);
                } else {
                    LOG.info("id: {} request: {} response: {}", isoMessage.getField(41).getValue(),
                            isoMessage.getField(43).getValue(), isoMessage.getField(126).getValue());
                    latch.countDown();
                }
                return false;
            }

            private void handleEcho(ChannelHandlerContext ctx, IsoMessage isoMessage) {
                ctx.writeAndFlush(factory.createResponse(isoMessage));
            }
        });

        client.init();
        client.connect();
        if (!client.isConnected()) {
            throw new IllegalStateException("Unconnected");
        }
    }

    public void close() {
        client.shutdown();
    }

    public void sendAndWait(String id, String text) {
        IsoMessage message = buildMessage(factory, id, text);
        LOG.debug("Sending {} {}", id, text);

        latch = new CountDownLatch(1);

        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start("send");
            client.send(message);
            stopWatch.stop();

            stopWatch.start("response");
            latch.await();
            stopWatch.stop();

            LOG.info("{} seconds", stopWatch.getTotalTimeSeconds());
        } catch (Throwable e) {
            LOG.error("Erro no send", e);
        }

    }

    private IsoMessage buildMessage(MessageFactory<IsoMessage> messageFactory, String id, String text) {
        IsoMessage m = messageFactory.newMessage(0x200);
        m.setValue(4, new BigDecimal("501.25"), IsoType.AMOUNT, 0);
        m.setValue(12, new Date(), IsoType.TIME, 0);
        m.setValue(15, new Date(), IsoType.DATE4, 0);
        m.setValue(17, new Date(), IsoType.DATE_EXP, 0);
        m.setValue(37, 127, IsoType.NUMERIC, 12);
        m.setValue(41, id, IsoType.ALPHA, 16);
        m.setValue(43, text, IsoType.ALPHA, 40);
        return m;
    }

}
