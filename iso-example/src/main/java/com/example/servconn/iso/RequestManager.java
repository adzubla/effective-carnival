package com.example.servconn.iso;

import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestManager {

    private ConcurrentHashMap<Integer, Data> map = new ConcurrentHashMap<>();

    public void add(Integer id, ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
        map.put(id, new Data(channelHandlerContext, isoMessage));
    }

    public Data get(Integer id) {
        return map.get(id);
    }

    public void remove(Integer id) {
        map.remove(id);
    }

    public static class Data {
        private ChannelHandlerContext channelHandlerContext;
        private IsoMessage isoMessage;

        public Data(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
            this.channelHandlerContext = channelHandlerContext;
            this.isoMessage = isoMessage;
        }

        public ChannelHandlerContext getChannelHandlerContext() {
            return channelHandlerContext;
        }

        public IsoMessage getIsoMessage() {
            return isoMessage;
        }
    }

}
