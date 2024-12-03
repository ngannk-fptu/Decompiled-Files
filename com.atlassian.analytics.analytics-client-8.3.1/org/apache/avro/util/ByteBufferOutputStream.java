/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ByteBufferOutputStream
extends OutputStream {
    public static final int BUFFER_SIZE = 8192;
    private List<ByteBuffer> buffers;

    public ByteBufferOutputStream() {
        this.reset();
    }

    public List<ByteBuffer> getBufferList() {
        List<ByteBuffer> result = this.buffers;
        this.reset();
        for (Buffer buffer : result) {
            buffer.flip();
        }
        return result;
    }

    public void prepend(List<ByteBuffer> lists) {
        for (Buffer buffer : lists) {
            buffer.position(buffer.limit());
        }
        this.buffers.addAll(0, lists);
    }

    public void append(List<ByteBuffer> lists) {
        for (Buffer buffer : lists) {
            buffer.position(buffer.limit());
        }
        this.buffers.addAll(lists);
    }

    public void reset() {
        this.buffers = new ArrayList<ByteBuffer>();
        this.buffers.add(ByteBuffer.allocate(8192));
    }

    public void write(ByteBuffer buffer) {
        this.buffers.add(buffer);
    }

    @Override
    public void write(int b) {
        ByteBuffer buffer = this.buffers.get(this.buffers.size() - 1);
        if (buffer.remaining() < 1) {
            buffer = ByteBuffer.allocate(8192);
            this.buffers.add(buffer);
        }
        buffer.put((byte)b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        ByteBuffer buffer = this.buffers.get(this.buffers.size() - 1);
        int remaining = buffer.remaining();
        while (len > remaining) {
            buffer.put(b, off, remaining);
            len -= remaining;
            off += remaining;
            buffer = ByteBuffer.allocate(8192);
            this.buffers.add(buffer);
            remaining = buffer.remaining();
        }
        buffer.put(b, off, len);
    }

    public void writeBuffer(ByteBuffer buffer) throws IOException {
        if (buffer.remaining() < 8192) {
            this.write(buffer.array(), buffer.position(), buffer.remaining());
        } else {
            ByteBuffer dup = buffer.duplicate();
            dup.position(buffer.limit());
            this.buffers.add(dup);
        }
    }
}

