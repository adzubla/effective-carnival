package com.example.iso.server;

import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class IsoServer {
    private static Logger LOG = LoggerFactory.getLogger(IsoServer.class);

    private Iso8583Server<IsoMessage> server;

    @Autowired
    private IsoListener listener;

    @Autowired
    private MessageFactory<IsoMessage> messageFactory;

    @Bean
    public MessageFactory<IsoMessage> messageFactory() throws IOException {
        MessageFactory<IsoMessage> messageFactory = ConfigParser.createFromClasspathConfig("j8583-config.xml");
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        return messageFactory;
    }

    @PostConstruct
    public void init() throws InterruptedException {
        server = new Iso8583Server<>(7777, messageFactory);

        server.addMessageListener(listener);
        server.getConfiguration().replyOnError();
        server.init();

        LOG.info("Start...");
        server.start();
        Thread.sleep(1000);
        if (!server.isStarted()) {
            throw new IllegalStateException("Server not started");
        }
        LOG.info("Up...");
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Shutdown...");
        server.shutdown();
    }

}
