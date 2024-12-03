/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket;

public interface WebSocketMessage<T> {
    public T getPayload();

    public int getPayloadLength();

    public boolean isLast();
}

