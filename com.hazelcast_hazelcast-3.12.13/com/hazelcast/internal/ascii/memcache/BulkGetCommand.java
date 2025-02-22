/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.memcache.MemcacheEntry;
import com.hazelcast.nio.IOUtil;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

public class BulkGetCommand
extends AbstractTextCommand {
    private final List<String> keys;
    private ByteBuffer byteBuffer;

    protected BulkGetCommand(List<String> keys) {
        super(TextCommandConstants.TextCommandType.BULK_GET);
        this.keys = keys;
    }

    public List<String> getKeys() {
        return this.keys;
    }

    @Override
    public boolean readFrom(ByteBuffer src) {
        return true;
    }

    @Override
    public boolean writeTo(ByteBuffer dst) {
        IOUtil.copyToHeapBuffer(this.byteBuffer, dst);
        return !this.byteBuffer.hasRemaining();
    }

    public void setResult(Collection<MemcacheEntry> result) {
        int size = TextCommandConstants.END.length;
        for (MemcacheEntry entry : result) {
            size += entry.getBytes().length;
        }
        this.byteBuffer = ByteBuffer.allocate(size);
        for (MemcacheEntry entry : result) {
            byte[] bytes = entry.getBytes();
            this.byteBuffer.put(bytes);
        }
        this.byteBuffer.put(TextCommandConstants.END);
        this.byteBuffer.flip();
    }
}

