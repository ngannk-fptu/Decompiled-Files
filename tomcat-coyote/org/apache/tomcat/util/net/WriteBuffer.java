/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteBufferHolder
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.tomcat.util.buf.ByteBufferHolder;
import org.apache.tomcat.util.net.SocketWrapperBase;

public class WriteBuffer {
    private final int bufferSize;
    private final LinkedBlockingDeque<ByteBufferHolder> buffers = new LinkedBlockingDeque();

    public WriteBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    void clear() {
        this.buffers.clear();
    }

    void add(byte[] buf, int offset, int length) {
        ByteBufferHolder holder = this.getByteBufferHolder(length);
        holder.getBuf().put(buf, offset, length);
    }

    public void add(ByteBuffer from) {
        ByteBufferHolder holder = this.getByteBufferHolder(from.remaining());
        holder.getBuf().put(from);
    }

    private ByteBufferHolder getByteBufferHolder(int capacity) {
        ByteBufferHolder holder = this.buffers.peekLast();
        if (holder == null || holder.isFlipped() || holder.getBuf().remaining() < capacity) {
            ByteBuffer buffer = ByteBuffer.allocate(Math.max(this.bufferSize, capacity));
            holder = new ByteBufferHolder(buffer, false);
            this.buffers.add(holder);
        }
        return holder;
    }

    public boolean isEmpty() {
        return this.buffers.isEmpty();
    }

    ByteBuffer[] toArray(ByteBuffer ... prefixes) {
        ArrayList<ByteBuffer> result = new ArrayList<ByteBuffer>();
        for (ByteBuffer prefix : prefixes) {
            if (!prefix.hasRemaining()) continue;
            result.add(prefix);
        }
        for (ByteBufferHolder buffer : this.buffers) {
            buffer.flip();
            result.add(buffer.getBuf());
        }
        this.buffers.clear();
        return result.toArray(new ByteBuffer[0]);
    }

    boolean write(SocketWrapperBase<?> socketWrapper, boolean blocking) throws IOException {
        Iterator<ByteBufferHolder> bufIter = this.buffers.iterator();
        boolean dataLeft = false;
        while (!dataLeft && bufIter.hasNext()) {
            ByteBufferHolder buffer = bufIter.next();
            buffer.flip();
            if (blocking) {
                socketWrapper.writeBlocking(buffer.getBuf());
            } else {
                socketWrapper.writeNonBlockingInternal(buffer.getBuf());
            }
            if (buffer.getBuf().remaining() == 0) {
                bufIter.remove();
                continue;
            }
            dataLeft = true;
        }
        return dataLeft;
    }

    public boolean write(Sink sink, boolean blocking) throws IOException {
        Iterator<ByteBufferHolder> bufIter = this.buffers.iterator();
        boolean dataLeft = false;
        while (!dataLeft && bufIter.hasNext()) {
            ByteBufferHolder buffer = bufIter.next();
            buffer.flip();
            dataLeft = sink.writeFromBuffer(buffer.getBuf(), blocking);
            if (dataLeft) continue;
            bufIter.remove();
        }
        return dataLeft;
    }

    public static interface Sink {
        public boolean writeFromBuffer(ByteBuffer var1, boolean var2) throws IOException;
    }
}

