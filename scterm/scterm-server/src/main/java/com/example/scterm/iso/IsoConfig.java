package com.example.scterm.iso;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class IsoConfig {

    @Value("${scterm.iso.threads}")
    public int quantThreads;

    @Bean
    public MessageFactory<IsoMessage> messageFactory() throws IOException {
        MessageFactory<IsoMessage> messageFactory = ConfigParser.createFromClasspathConfig("j8583-config.xml");
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        return messageFactory;
    }

    @Bean
    public ExecutorService threadPoolExecutor() {
        return Executors.newFixedThreadPool(quantThreads);
    }

}
