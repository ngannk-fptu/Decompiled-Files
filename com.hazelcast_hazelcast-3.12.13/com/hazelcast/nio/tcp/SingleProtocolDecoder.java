/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.tcp.MemberProtocolEncoder;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.ByteBuffer;

public class SingleProtocolDecoder
extends InboundHandler<ByteBuffer, Void> {
    protected final InboundHandler[] inboundHandlers;
    protected final ProtocolType supportedProtocol;
    private final MemberProtocolEncoder encoder;

    public SingleProtocolDecoder(ProtocolType supportedProtocol, InboundHandler next) {
        this(supportedProtocol, new InboundHandler[]{next}, null);
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public SingleProtocolDecoder(ProtocolType supportedProtocol, InboundHandler[] next, MemberProtocolEncoder encoder) {
        this.supportedProtocol = supportedProtocol;
        this.inboundHandlers = next;
        this.encoder = encoder;
    }

    @Override
    public void handlerAdded() {
        this.initSrcBuffer(3);
    }

    @Override
    public HandlerStatus onRead() {
        ((ByteBuffer)this.src).flip();
        try {
            if (((ByteBuffer)this.src).remaining() < 3) {
                HandlerStatus handlerStatus = HandlerStatus.CLEAN;
                return handlerStatus;
            }
            this.verifyProtocol(this.loadProtocol());
            this.initConnection();
            this.setupNextDecoder();
            if (this.shouldSignalProtocolLoaded()) {
                this.encoder.signalProtocolLoaded();
            }
            HandlerStatus handlerStatus = HandlerStatus.CLEAN;
            return handlerStatus;
        }
        finally {
            IOUtil.compactOrClear((ByteBuffer)this.src);
        }
    }

    protected void setupNextDecoder() {
        this.channel.inboundPipeline().replace(this, this.inboundHandlers);
    }

    protected void verifyProtocol(String incomingProtocol) {
        if (!incomingProtocol.equals(this.supportedProtocol.getDescriptor())) {
            throw new IllegalStateException("Unsupported protocol exchange detected, expected protocol: " + this.supportedProtocol.name());
        }
    }

    private String loadProtocol() {
        byte[] protocolBytes = new byte[3];
        ((ByteBuffer)this.src).get(protocolBytes);
        return StringUtil.bytesToString(protocolBytes);
    }

    private void initConnection() {
        if (this.supportedProtocol == ProtocolType.MEMBER) {
            TcpIpConnection connection = (TcpIpConnection)this.channel.attributeMap().get(TcpIpConnection.class);
            connection.setType(ConnectionType.MEMBER);
        }
    }

    private boolean shouldSignalProtocolLoaded() {
        return !this.channel.isClientMode() && this.encoder != null;
    }
}

