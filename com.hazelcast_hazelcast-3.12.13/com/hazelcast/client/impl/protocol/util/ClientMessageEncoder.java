/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.function.Supplier;
import java.nio.ByteBuffer;

public class ClientMessageEncoder
extends OutboundHandler<Supplier<ClientMessage>, ByteBuffer> {
    private ClientMessage message;

    @Override
    public void handlerAdded() {
        this.initDstBuffer();
    }

    @Override
    public HandlerStatus onWrite() {
        IOUtil.compactOrClear((ByteBuffer)this.dst);
        try {
            while (true) {
                if (this.message == null) {
                    this.message = (ClientMessage)((Supplier)this.src).get();
                    if (this.message == null) {
                        HandlerStatus handlerStatus = HandlerStatus.CLEAN;
                        return handlerStatus;
                    }
                }
                if (!this.message.writeTo((ByteBuffer)this.dst)) break;
                this.message = null;
            }
            HandlerStatus handlerStatus = HandlerStatus.DIRTY;
            return handlerStatus;
        }
        finally {
            ((ByteBuffer)this.dst).flip();
        }
    }
}

