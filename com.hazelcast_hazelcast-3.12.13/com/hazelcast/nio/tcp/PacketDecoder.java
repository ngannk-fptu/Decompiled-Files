/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.nio.InboundHandlerWithCounters;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.PacketIOHelper;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.util.function.Consumer;
import java.nio.ByteBuffer;

public class PacketDecoder
extends InboundHandlerWithCounters<ByteBuffer, Consumer<Packet>> {
    protected final TcpIpConnection connection;
    private final PacketIOHelper packetReader = new PacketIOHelper();

    public PacketDecoder(TcpIpConnection connection, Consumer<Packet> dst) {
        this.connection = connection;
        this.dst = dst;
    }

    @Override
    public void handlerAdded() {
        this.initSrcBuffer();
    }

    @Override
    public HandlerStatus onRead() throws Exception {
        ((ByteBuffer)this.src).flip();
        try {
            Packet packet;
            while (((ByteBuffer)this.src).hasRemaining() && (packet = this.packetReader.readFrom((ByteBuffer)this.src)) != null) {
                this.onPacketComplete(packet);
            }
            HandlerStatus handlerStatus = HandlerStatus.CLEAN;
            return handlerStatus;
        }
        finally {
            IOUtil.compactOrClear((ByteBuffer)this.src);
        }
    }

    protected void onPacketComplete(Packet packet) {
        if (packet.isFlagRaised(16)) {
            this.priorityPacketsRead.inc();
        } else {
            this.normalPacketsRead.inc();
        }
        packet.setConn(this.connection);
        ((Consumer)this.dst).accept(packet);
    }
}

