/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.ByteBuffer;

public class MemberProtocolEncoder
extends OutboundHandler<Void, ByteBuffer> {
    private final OutboundHandler[] outboundHandlers;
    private volatile boolean mustWriteProtocol;
    private boolean clusterProtocolBuffered;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public MemberProtocolEncoder(OutboundHandler[] next) {
        this.outboundHandlers = next;
    }

    @Override
    public void handlerAdded() {
        this.initDstBuffer(3);
        if (this.channel.isClientMode()) {
            this.mustWriteProtocol = true;
        }
    }

    @Override
    public HandlerStatus onWrite() {
        IOUtil.compactOrClear((ByteBuffer)this.dst);
        try {
            if (!this.mustWriteProtocol) {
                HandlerStatus handlerStatus = HandlerStatus.CLEAN;
                return handlerStatus;
            }
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
            TcpIpConnection connection = (TcpIpConnection)this.channel.attributeMap().get(TcpIpConnection.class);
            connection.setType(ConnectionType.MEMBER);
            this.channel.outboundPipeline().replace(this, this.outboundHandlers);
            HandlerStatus handlerStatus = HandlerStatus.CLEAN;
            return handlerStatus;
        }
        finally {
            ((ByteBuffer)this.dst).flip();
        }
    }

    public void signalProtocolLoaded() {
        assert (!this.channel.isClientMode()) : "Signal protocol should only be made on channel in serverMode";
        this.mustWriteProtocol = true;
        this.channel.outboundPipeline().wakeup();
    }

    private boolean isProtocolBufferDrained() {
        return ((ByteBuffer)this.dst).position() == 0;
    }
}

