package com.example.scterm.iso;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Service
public class IsoListener implements IsoMessageListener<IsoMessage> {
    private static Logger LOG = LoggerFactory.getLogger(IsoListener.class);

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public boolean applies(IsoMessage isoMessage) {
        LOG.trace("applies: {}", isoMessage);
        return isoMessage.getType() == 0x200;
    }

    @Override
    public boolean onMessage(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
        LOG.trace("onMessage: {} {}", channelHandlerContext, isoMessage);
        LOG.debug("Received from client: {}", isoMessage.getField(41).getValue());

        boolean ok = isMessageValid(isoMessage);
        if (ok) {
            dispatch(channelHandlerContext, isoMessage);
        }
        return false;
    }

    private boolean isMessageValid(IsoMessage isoMessage) {
        return true;
    }

    private void dispatch(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
        Long id = Long.valueOf((String) isoMessage.getField(37).getValue());
        String text = (String) isoMessage.getField(41).getValue();

        connectionManager.add(id, channelHandlerContext, isoMessage);

        jmsTemplate.send("DEV.QUEUE.1", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                String s = id + " " + text;
                LOG.debug("Sending to queue: {}", s);
                return session.createTextMessage(s);
            }
        });
    }

}
