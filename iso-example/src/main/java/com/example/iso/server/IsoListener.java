package com.example.iso.server;

import com.example.iso.Util;
import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsoListener implements IsoMessageListener<IsoMessage> {
    private static Logger LOG = LoggerFactory.getLogger(IsoListener.class);

    @Autowired
    private MessageFactory<IsoMessage> messageFactory;

    @Override
    public boolean applies(IsoMessage isoMessage) {
        LOG.debug("applies: {}", isoMessage);
        return isoMessage.getType() == 0x200;
    }

    @Override
    public boolean onMessage(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
        LOG.debug("onMessage: {} {}", channelHandlerContext, isoMessage);

        Util.print("-- REQUEST --------------------------------", isoMessage);

        String respText = "RESP:" + isoMessage.getField(41).getValue();

        final IsoMessage response = messageFactory.createResponse(isoMessage);
        response.setField(39, IsoType.ALPHA.value("00", 2));
        response.setField(60, IsoType.LLLVAR.value("XXX", 3));
        response.setField(126, IsoType.LLLVAR.value(respText, 16));

        Util.print("-- RESPONSE -------------------------------", response);

        channelHandlerContext.writeAndFlush(response);
        return false;
    }
}
