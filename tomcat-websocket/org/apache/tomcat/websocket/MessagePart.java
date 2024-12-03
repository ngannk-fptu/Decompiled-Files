/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.SendHandler
 */
package org.apache.tomcat.websocket;

import java.nio.ByteBuffer;
import javax.websocket.SendHandler;

class MessagePart {
    private final boolean fin;
    private final int rsv;
    private final byte opCode;
    private final ByteBuffer payload;
    private final SendHandler intermediateHandler;
    private volatile SendHandler endHandler;
    private final long blockingWriteTimeoutExpiry;

    MessagePart(boolean fin, int rsv, byte opCode, ByteBuffer payload, SendHandler intermediateHandler, SendHandler endHandler, long blockingWriteTimeoutExpiry) {
        this.fin = fin;
        this.rsv = rsv;
        this.opCode = opCode;
        this.payload = payload;
        this.intermediateHandler = intermediateHandler;
        this.endHandler = endHandler;
        this.blockingWriteTimeoutExpiry = blockingWriteTimeoutExpiry;
    }

    public boolean isFin() {
        return this.fin;
    }

    public int getRsv() {
        return this.rsv;
    }

    public byte getOpCode() {
        return this.opCode;
    }

    public ByteBuffer getPayload() {
        return this.payload;
    }

    public SendHandler getIntermediateHandler() {
        return this.intermediateHandler;
    }

    public SendHandler getEndHandler() {
        return this.endHandler;
    }

    public void setEndHandler(SendHandler endHandler) {
        this.endHandler = endHandler;
    }

    public long getBlockingWriteTimeoutExpiry() {
        return this.blockingWriteTimeoutExpiry;
    }
}

