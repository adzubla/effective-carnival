package com.example.servconn.jms;

import com.example.servconn.iso.RequestManager;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class QueueReceiver {
    private static Logger LOG = LoggerFactory.getLogger(QueueReceiver.class);

    @Autowired
    private MessageFactory<IsoMessage> messageFactory;

    @Autowired
    private RequestManager requestManager;

    @JmsListener(destination = "QTEST", concurrency = "2")
    public void receiveMessage(String message) {
        LOG.debug("Received from queue: {}", message);

        Integer id = getId(message);

        RequestManager.Data data = requestManager.get(id);

        if (data == null) {
            LOG.debug("Discarding {}", message);
        } else {
            sendResponse(data.getChannelHandlerContext(), data.getIsoMessage(), message.toUpperCase());

            requestManager.remove(id);
        }
    }

    private Integer getId(String message) {
        Scanner scanner = new Scanner(message).useDelimiter(",");
        return scanner.nextInt();
    }

    public void sendResponse(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage, String message) {
        final IsoMessage response = messageFactory.createResponse(isoMessage);
        response.setField(39, IsoType.ALPHA.value("00", 2));
        response.setField(60, IsoType.LLLVAR.value("XXX", 3));
        response.setField(126, IsoType.LLLVAR.value(message, 16));

        LOG.debug("Responding to client: {}", message);
        channelHandlerContext.writeAndFlush(response);
    }

}
