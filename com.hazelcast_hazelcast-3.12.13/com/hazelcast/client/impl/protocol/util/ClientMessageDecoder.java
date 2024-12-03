/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.BufferBuilder;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.nio.InboundHandlerWithCounters;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.collection.Long2ObjectHashMap;
import com.hazelcast.util.function.Consumer;
import java.nio.ByteBuffer;

public class ClientMessageDecoder
extends InboundHandlerWithCounters<ByteBuffer, Consumer<ClientMessage>> {
    private final Long2ObjectHashMap<BufferBuilder> builderBySessionIdMap = new Long2ObjectHashMap();
    private final Connection connection;
    private ClientMessage message = ClientMessage.create();

    public ClientMessageDecoder(Connection connection, Consumer<ClientMessage> dst) {
        this.dst(dst);
        this.connection = connection;
    }

    @Override
    public void handlerAdded() {
        this.initSrcBuffer();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HandlerStatus onRead() {
        ((ByteBuffer)this.src).flip();
        try {
            boolean complete;
            while (((ByteBuffer)this.src).hasRemaining() && (complete = this.message.readFrom((ByteBuffer)this.src))) {
                BufferBuilder builder;
                if (this.message.isFlagSet((short)192)) {
                    this.handleMessage(this.message);
                    this.message = ClientMessage.create();
                    continue;
                }
                if (this.message.isFlagSet((short)128)) {
                    builder = new BufferBuilder();
                    this.builderBySessionIdMap.put(this.message.getCorrelationId(), builder);
                    builder.append(this.message.buffer(), 0, this.message.getFrameLength());
                } else {
                    builder = this.builderBySessionIdMap.get(this.message.getCorrelationId());
                    if (builder.position() == 0) {
                        throw new IllegalStateException();
                    }
                    builder.append(this.message.buffer(), this.message.getDataOffset(), this.message.getFrameLength() - this.message.getDataOffset());
                    if (this.message.isFlagSet((short)64)) {
                        int msgLength = builder.position();
                        ClientMessage cm = ClientMessage.createForDecode(builder.buffer(), 0);
                        cm.setFrameLength(msgLength);
                        this.handleMessage(cm);
                        this.builderBySessionIdMap.remove(this.message.getCorrelationId());
                    }
                }
                this.message = ClientMessage.create();
            }
            HandlerStatus handlerStatus = HandlerStatus.CLEAN;
            return handlerStatus;
        }
        finally {
            IOUtil.compactOrClear((ByteBuffer)this.src);
        }
    }

    private void handleMessage(ClientMessage message) {
        message.index(message.getDataOffset());
        message.setConnection(this.connection);
        this.normalPacketsRead.inc();
        ((Consumer)this.dst).accept(message);
    }
}

