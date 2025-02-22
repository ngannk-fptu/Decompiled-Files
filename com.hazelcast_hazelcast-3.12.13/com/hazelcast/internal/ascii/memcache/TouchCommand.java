/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import java.nio.ByteBuffer;

public class TouchCommand
extends AbstractTextCommand {
    private String key;
    private int expiration;
    private boolean noreply;
    private ByteBuffer response;

    public TouchCommand(TextCommandConstants.TextCommandType type, String key, int expiration, boolean noReply) {
        super(type);
        this.key = key;
        this.expiration = expiration;
        this.noreply = noReply;
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        if (this.response == null) {
            this.response = ByteBuffer.wrap(TextCommandConstants.STORED);
        }
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

    public int getExpiration() {
        return this.expiration;
    }

    public void setResponse(byte[] value) {
        this.response = ByteBuffer.wrap(value);
    }
}

