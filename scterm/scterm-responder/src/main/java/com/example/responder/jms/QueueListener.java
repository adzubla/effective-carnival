package com.example.responder.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class QueueListener {
    private static Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "DEV.QUEUE.1", concurrency = "4")
    public void receiveMessage(String data, Message message) throws JMSException {
        LOG.debug("Received from queue: {}", data);

        Destination replyTo = message.getJMSReplyTo();

        jmsTemplate.send(replyTo, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(data);
                textMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                return textMessage;
            }
        });
    }

}
