/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.Buffer
 *  org.apache.commons.collections.BufferUtils
 *  org.apache.commons.collections.buffer.UnboundedFifoBuffer
 */
package com.atlassian.core.task;

import com.atlassian.core.task.FifoBuffer;
import java.util.Collection;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

public class LocalFifoBuffer<T>
implements FifoBuffer<T> {
    private final Buffer buffer = BufferUtils.synchronizedBuffer((Buffer)new UnboundedFifoBuffer());

    @Override
    public synchronized T remove() {
        if (!this.buffer.isEmpty()) {
            return (T)this.buffer.remove();
        }
        return null;
    }

    @Override
    public void add(T o) {
        this.buffer.add(o);
    }

    @Override
    public int size() {
        return this.buffer.size();
    }

    @Override
    public Collection<T> getItems() {
        return this.buffer;
    }

    @Override
    public void clear() {
        this.buffer.clear();
    }
}

