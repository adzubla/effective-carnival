package com.example.scterm.iso;

import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {

    private ConcurrentHashMap<ConnectionId, ConnectionData> map = new ConcurrentHashMap<>();

    public void add(ConnectionId id, ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
        if (map.containsKey(id)) {
            return;
        }
        map.put(id, new ConnectionData(channelHandlerContext, isoMessage));
    }

    public ConnectionData get(ConnectionId id) {
        return map.get(id);
    }

    public void remove(ConnectionId id) {
        map.remove(id);
    }

    public Collection<ConnectionData> list() {
        return map.values();
    }

    public static class ConnectionData {
        private ChannelHandlerContext channelHandlerContext;
        private IsoMessage isoMessage;

        public ConnectionData(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
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
