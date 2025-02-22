/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import java.nio.ByteBuffer;

public class SimpleCommand
extends AbstractTextCommand {
    private ByteBuffer response;

    public SimpleCommand(TextCommandConstants.TextCommandType type) {
        super(type);
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
        while (dst.hasRemaining() && this.response.hasRemaining()) {
            dst.put(this.response.get());
        }
        return !this.response.hasRemaining();
    }

    @Override
    public String toString() {
        return "SimpleCommand [" + (Object)((Object)this.type) + "]{" + '}' + super.toString();
    }
}

