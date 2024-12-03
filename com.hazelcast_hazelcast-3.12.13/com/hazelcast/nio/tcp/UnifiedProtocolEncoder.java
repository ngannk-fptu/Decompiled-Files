/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.client.impl.protocol.util.ClientMessageEncoder;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.networking.ChannelOption;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ascii.TextEncoder;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.util.StringUtil;
import java.nio.ByteBuffer;

public class UnifiedProtocolEncoder
extends OutboundHandler<Void, ByteBuffer> {
    private final IOService ioService;
    private final HazelcastProperties props;
    private volatile String inboundProtocol;
    private boolean clusterProtocolBuffered;

    public UnifiedProtocolEncoder(IOService ioService) {
        this.ioService = ioService;
        this.props = ioService.properties();
    }

    @Override
    public void handlerAdded() {
        this.initDstBuffer(3);
        if (this.channel.isClientMode()) {
            this.inboundProtocol = "HZC";
        }
    }

    void signalProtocolEstablished(String inboundProtocol) {
        assert (!this.channel.isClientMode()) : "Signal protocol should only be made on channel in serverMode";
        this.inboundProtocol = inboundProtocol;
        this.channel.outboundPipeline().wakeup();
    }

    @Override
    public HandlerStatus onWrite() {
        IOUtil.compactOrClear((ByteBuffer)this.dst);
        try {
            if (this.inboundProtocol == null) {
                HandlerStatus handlerStatus = HandlerStatus.CLEAN;
                return handlerStatus;
            }
            if ("HZC".equals(this.inboundProtocol)) {
                if (!this.clusterProtocolBuffered) {
                    this.clusterProtocolBuffered = true;
                    ((ByteBuffer)this.dst).put(StringUtil.stringToBytes("HZC"));
                    HandlerStatus handlerStatus = HandlerStatus.DIRTY;
                    return handlerStatus;
                }
                if (!this.isProtocolBufferDrained()) {
                    HandlerStatus handlerStatus = HandlerStatus.DIRTY;
                    return handlerStatus;
                }
                this.initChannelForCluster();
            } else if ("CB2".equals(this.inboundProtocol)) {
                this.initChannelForClient();
            } else {
                this.initChannelForText();
            }
            HandlerStatus handlerStatus = HandlerStatus.CLEAN;
            return handlerStatus;
        }
        finally {
            ((ByteBuffer)this.dst).flip();
        }
    }

    private boolean isProtocolBufferDrained() {
        return ((ByteBuffer)this.dst).position() == 0;
    }

    private void initChannelForCluster() {
        this.channel.options().setOption(ChannelOption.SO_SNDBUF, this.props.getInteger(GroupProperty.SOCKET_SEND_BUFFER_SIZE) * 1024);
        TcpIpConnection connection = (TcpIpConnection)this.channel.attributeMap().get(TcpIpConnection.class);
        OutboundHandler[] handlers = this.ioService.createOutboundHandlers(EndpointQualifier.MEMBER, connection);
        this.channel.outboundPipeline().replace(this, handlers);
    }

    private void initChannelForClient() {
        this.channel.options().setOption(ChannelOption.SO_SNDBUF, this.clientSndBuf());
        this.channel.outboundPipeline().replace(this, new ClientMessageEncoder());
    }

    private void initChannelForText() {
        this.channel.options().setOption(ChannelOption.SO_SNDBUF, this.clientSndBuf());
        TextEncoder encoder = (TextEncoder)this.channel.attributeMap().remove("textencoder");
        this.channel.outboundPipeline().replace(this, encoder);
    }

    private int clientSndBuf() {
        int sndBuf = this.props.getInteger(GroupProperty.SOCKET_CLIENT_SEND_BUFFER_SIZE);
        if (sndBuf == -1) {
            sndBuf = this.props.getInteger(GroupProperty.SOCKET_SEND_BUFFER_SIZE);
        }
        return sndBuf * 1024;
    }
}

