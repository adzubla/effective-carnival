package com.example.scterm.iso;

import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {

    private ConcurrentHashMap<ConnectionId, ConnectionInfo> map = new ConcurrentHashMap<>();

    public void add(ConnectionId id, ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
        if (map.containsKey(id)) {
            return;
        }
        map.put(id, new ConnectionInfo(channelHandlerContext, isoMessage));
    }

    public ConnectionInfo get(ConnectionId id) {
        return map.get(id);
    }

    public void remove(ConnectionId id) {
        map.remove(id);
    }

    public static class ConnectionInfo {
        private ChannelHandlerContext channelHandlerContext;
        private IsoMessage isoMessage;

        public ConnectionInfo(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
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
