/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl.bufferpool;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.bufferpool.BufferPool;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.BufferObjectDataOutput;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.serialization.Data;
import java.io.Closeable;
import java.util.ArrayDeque;
import java.util.Queue;

public class BufferPoolImpl
implements BufferPool {
    static final int MAX_POOLED_ITEMS = 3;
    protected final InternalSerializationService serializationService;
    final Queue<BufferObjectDataOutput> outputQueue = new ArrayDeque<BufferObjectDataOutput>(3);
    final Queue<BufferObjectDataInput> inputQueue = new ArrayDeque<BufferObjectDataInput>(3);

    public BufferPoolImpl(InternalSerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public BufferObjectDataOutput takeOutputBuffer() {
        BufferObjectDataOutput out = this.outputQueue.poll();
        if (out == null) {
            out = this.serializationService.createObjectDataOutput();
        }
        return out;
    }

    @Override
    public void returnOutputBuffer(BufferObjectDataOutput out) {
        if (out == null) {
            return;
        }
        out.clear();
        BufferPoolImpl.offerOrClose(this.outputQueue, out);
    }

    @Override
    public BufferObjectDataInput takeInputBuffer(Data data) {
        BufferObjectDataInput in = this.inputQueue.poll();
        if (in == null) {
            in = this.serializationService.createObjectDataInput((byte[])null);
        }
        in.init(data.toByteArray(), 8);
        return in;
    }

    @Override
    public void returnInputBuffer(BufferObjectDataInput in) {
        if (in == null) {
            return;
        }
        in.clear();
        BufferPoolImpl.offerOrClose(this.inputQueue, in);
    }

    private static <C extends Closeable> void offerOrClose(Queue<C> queue, C item) {
        if (queue.size() == 3) {
            IOUtil.closeResource(item);
            return;
        }
        queue.offer(item);
    }
}

