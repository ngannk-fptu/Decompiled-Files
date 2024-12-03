/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.function.Supplier;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PrivateApi
public class TextEncoder
extends OutboundHandler<Supplier<TextCommand>, ByteBuffer> {
    public static final String TEXT_ENCODER = "textencoder";
    private final TcpIpConnection connection;
    private final Map<Long, TextCommand> responses = new ConcurrentHashMap<Long, TextCommand>(100);
    private long currentRequestId;
    private TextCommand command;

    public TextEncoder(TcpIpConnection connection) {
        this.connection = connection;
    }

    @Override
    public void handlerAdded() {
        this.initDstBuffer();
    }

    public void enqueue(TextCommand response) {
        long requestId = response.getRequestId();
        if (requestId == -1L) {
            this.connection.write(response);
        } else if (this.currentRequestId == requestId) {
            this.connection.write(response);
            ++this.currentRequestId;
            this.processWaitingResponses();
        } else {
            this.responses.put(requestId, response);
        }
    }

    private void processWaitingResponses() {
        TextCommand response = this.responses.remove(this.currentRequestId);
        while (response != null) {
            this.connection.write(response);
            ++this.currentRequestId;
            response = this.responses.remove(this.currentRequestId);
        }
    }

    @Override
    public HandlerStatus onWrite() {
        IOUtil.compactOrClear((ByteBuffer)this.dst);
        try {
            while (true) {
                if (this.command == null) {
                    this.command = (TextCommand)((Supplier)this.src).get();
                    if (this.command == null) {
                        HandlerStatus handlerStatus = HandlerStatus.CLEAN;
                        return handlerStatus;
                    }
                }
                if (!this.command.writeTo((ByteBuffer)this.dst)) break;
                this.command = null;
            }
            HandlerStatus handlerStatus = HandlerStatus.DIRTY;
            return handlerStatus;
        }
        finally {
            ((ByteBuffer)this.dst).flip();
        }
    }
}

