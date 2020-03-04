package com.example.scterm.jms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

@Configuration
public class JmsConfig {
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
    @Bean
    public DynamicDestinationResolver destinationResolver() {
        DynamicDestinationResolver dynamicDestinationResolver = new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException {
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + destinationName);
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
                //return session.createTemporaryQueue();
            }
        };
        return dynamicDestinationResolver;
    }

}
