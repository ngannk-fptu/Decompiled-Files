/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.util;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.http.nio.util.BufferInfo;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.util.Args;

public class ExpandableBuffer
implements org.apache.http.io.BufferInfo,
BufferInfo {
    public static final int INPUT_MODE = 0;
    public static final int OUTPUT_MODE = 1;
    private final ByteBufferAllocator allocator;
    private int mode;
    protected ByteBuffer buffer = null;

    public ExpandableBuffer(int bufferSize, ByteBufferAllocator allocator) {
        Args.notNull(allocator, "ByteBuffer allocator");
        this.allocator = allocator;
        this.buffer = allocator.allocate(bufferSize);
        this.mode = 0;
    }

    protected int getMode() {
        return this.mode;
    }

    protected void setOutputMode() {
        if (this.mode != 1) {
            this.buffer.flip();
            this.mode = 1;
        }
    }

    protected void setInputMode() {
        if (this.mode != 0) {
            if (this.buffer.hasRemaining()) {
                this.buffer.compact();
            } else {
                this.buffer.clear();
            }
            this.mode = 0;
        }
    }

    private void expandCapacity(int capacity) {
        ByteBuffer oldbuffer = this.buffer;
        this.buffer = this.allocator.allocate(capacity);
        oldbuffer.flip();
        this.buffer.put(oldbuffer);
    }

    protected void expand() throws BufferOverflowException {
        int newCapacity = this.buffer.capacity() + 1 << 1;
        if (newCapacity < 0) {
            int vmBytes = 8;
            int javaBytes = 8;
            int headRoom = 8;
            newCapacity = 0x7FFFFFF7;
            if (newCapacity <= this.buffer.capacity()) {
                throw new BufferOverflowException();
            }
        }
        this.expandCapacity(newCapacity);
    }

    protected void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > this.buffer.capacity()) {
            this.expandCapacity(requiredCapacity);
        }
    }

    @Override
    public int capacity() {
        return this.buffer.capacity();
    }

    public boolean hasData() {
        this.setOutputMode();
        return this.buffer.hasRemaining();
    }

    @Override
    public int length() {
        this.setOutputMode();
        return this.buffer.remaining();
    }

    @Override
    public int available() {
        this.setInputMode();
        return this.buffer.remaining();
    }

    protected void clear() {
        this.buffer.clear();
        this.mode = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[mode=");
        if (this.getMode() == 0) {
            sb.append("in");
        } else {
            sb.append("out");
        }
        sb.append(" pos=");
        sb.append(this.buffer.position());
        sb.append(" lim=");
        sb.append(this.buffer.limit());
        sb.append(" cap=");
        sb.append(this.buffer.capacity());
        sb.append("]");
        return sb.toString();
    }
}

