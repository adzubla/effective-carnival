package com.example.scterm.jms;

import com.example.scterm.iso.ConnectionId;
import com.example.scterm.iso.ConnectionManager;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Scanner;

@Service
public class QueueListener {
    private static Logger LOG = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private MessageFactory<IsoMessage> messageFactory;

    @Autowired
    private ConnectionManager connectionManager;

    @JmsListener(destination = "DEV.QUEUE.1", concurrency = "1")
    public void receiveMessage(String data) throws UnsupportedEncodingException, ParseException {
        LOG.debug("Received from queue: {}", data);

        IsoMessage isoMessage = messageFactory.parseMessage(data.getBytes(), 0);

        Scanner scanner = new Scanner((String) isoMessage.getField(41).getValue());
        ConnectionId id = new ConnectionId(scanner.next());

        ConnectionManager.ConnectionData connectionData = connectionManager.get(id);

        if (connectionData == null) {
            LOG.debug("Discarding: {}", data);
        } else {
            String text = ((String) isoMessage.getField(43).getValue()).toUpperCase();
            final IsoMessage response = buildResponse(isoMessage, text);

            LOG.debug("Responding to client: {}", text);
            connectionData.getChannelHandlerContext().writeAndFlush(response);
        }
    }

    private IsoMessage buildResponse(IsoMessage isoMessage, String text) {
        final IsoMessage response = messageFactory.createResponse(isoMessage);
        response.setField(39, IsoType.ALPHA.value("00", 2));
        response.setField(60, IsoType.LLLVAR.value("XXX", 3));
        response.setField(126, IsoType.LLLVAR.value(text));
        return response;
    }

}
