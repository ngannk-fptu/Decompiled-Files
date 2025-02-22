/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import java.nio.ByteBuffer;

public class IncrementCommand
extends AbstractTextCommand {
    private String key;
    private int value;
    private boolean noreply;
    private ByteBuffer response;

    public IncrementCommand(TextCommandConstants.TextCommandType type, String key, int value, boolean noReply) {
        super(type);
        this.key = key;
        this.value = value;
        this.noreply = noReply;
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        while (dst.hasRemaining() && this.response.hasRemaining()) {
            dst.put(this.response.get());
        }
        return !this.response.hasRemaining();
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }

    @Override
    public boolean shouldReply() {
        return !this.noreply;
    }

    public String getKey() {
        return this.key;
    }

    public int getValue() {
        return this.value;
    }

    public void setResponse(byte[] value) {
        this.response = ByteBuffer.wrap(value);
    }
}

