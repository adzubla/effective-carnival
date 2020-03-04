package com.example.scterm.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

@Configuration
public class JmsConfig {
    private static Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    /*
        @Bean
        public DefaultJmsListenerContainerFactory myFactory(DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) throws JMSException {
            DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
            configurer.configure(factory, connectionFactory);

            MQConnectionFactory cf = ((MQConnectionFactory) connectionFactory);
            cf.setStringProperty(WMQConstants.WMQ_TEMPORARY_MODEL, "DEV.APP.MODEL.QUEUE");

            return factory;
        }
    */

    @Autowired
    ReplyToHolder replyToHolder;

    @Bean
    public DynamicDestinationResolver destinationResolver() {
        DynamicDestinationResolver dynamicDestinationResolver = new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException {
                LOG.debug("session = {} destinationName = {}", session, destinationName);

                if ("REPLY_TO_DYNAMIC_QUEUE".equals(destinationName)) {
                    TemporaryQueue temporaryQueue = session.createTemporaryQueue();
                    replyToHolder.setReplyToQueue(temporaryQueue);
                    LOG.debug("Created temporary queue: {}", temporaryQueue);
                    return temporaryQueue;
                } else {
                    return super.resolveDestinationName(session, destinationName, pubSubDomain);
                }
            }
        };
        return dynamicDestinationResolver;
    }

}
