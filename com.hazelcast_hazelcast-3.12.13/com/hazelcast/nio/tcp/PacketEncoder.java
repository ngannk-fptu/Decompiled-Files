/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.PacketIOHelper;
import com.hazelcast.util.function.Supplier;
import java.nio.ByteBuffer;

public class PacketEncoder
extends OutboundHandler<Supplier<Packet>, ByteBuffer> {
    private final PacketIOHelper packetWriter = new PacketIOHelper();
    private Packet packet;

    @Override
    public void handlerAdded() {
        this.initDstBuffer();
    }

    @Override
    public HandlerStatus onWrite() {
        IOUtil.compactOrClear((ByteBuffer)this.dst);
        try {
            while (true) {
                if (this.packet == null) {
                    this.packet = (Packet)((Supplier)this.src).get();
                    if (this.packet == null) {
                        HandlerStatus handlerStatus = HandlerStatus.CLEAN;
                        return handlerStatus;
                    }
                }
                if (!this.packetWriter.writeTo(this.packet, (ByteBuffer)this.dst)) break;
                this.packet = null;
            }
            HandlerStatus handlerStatus = HandlerStatus.DIRTY;
            return handlerStatus;
        }
        finally {
            ((ByteBuffer)this.dst).flip();
        }
    }
}

