/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import java.nio.ByteBuffer;

public class DeleteCommand
extends AbstractTextCommand {
    private ByteBuffer response;
    private final String key;
    private final int expiration;
    private final boolean noreply;

    public DeleteCommand(String key, int expiration, boolean noreply) {
        super(TextCommandConstants.TextCommandType.DELETE);
        this.key = key;
        this.expiration = expiration;
        this.noreply = noreply;
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }

    public void setResponse(byte[] value) {
        this.response = ByteBuffer.wrap(value);
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
    public boolean shouldReply() {
        return !this.noreply;
    }

    public int getExpiration() {
        return this.expiration;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return "DeleteCommand [" + (Object)((Object)this.type) + "]{key='" + this.key + '\'' + ", expiration=" + this.expiration + ", noreply=" + this.noreply + '}' + super.toString();
    }
}

