/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.memcache.MemcacheEntry;
import com.hazelcast.nio.IOUtil;
import java.nio.ByteBuffer;

public class GetCommand
extends AbstractTextCommand {
    protected final String key;
    private ByteBuffer value;
    private ByteBuffer endMarker;

    public GetCommand(TextCommandConstants.TextCommandType type, String key) {
        super(type);
        this.key = key;
    }

    public GetCommand(String key) {
        this(TextCommandConstants.TextCommandType.GET, key);
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }

    public void setValue(MemcacheEntry entry) {
        if (entry != null) {
            this.value = entry.toNewBuffer();
        }
        this.endMarker = ByteBuffer.wrap(TextCommandConstants.END);
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        if (this.value != null) {
            IOUtil.copyToHeapBuffer(this.value, dst);
        }
        IOUtil.copyToHeapBuffer(this.endMarker, dst);
        return (this.value == null || !this.value.hasRemaining()) && !this.endMarker.hasRemaining();
    }

    @Override
    public String toString() {
        return "GetCommand{key='" + this.key + ", value=" + this.value + '\'' + "} " + super.toString();
    }
}

