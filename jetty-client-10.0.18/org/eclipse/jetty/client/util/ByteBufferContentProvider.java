/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.util;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.eclipse.jetty.client.util.AbstractTypedContentProvider;

@Deprecated
public class ByteBufferContentProvider
extends AbstractTypedContentProvider {
    private final ByteBuffer[] buffers;
    private final int length;

    public ByteBufferContentProvider(ByteBuffer ... buffers) {
        this("application/octet-stream", buffers);
    }

    public ByteBufferContentProvider(String contentType, ByteBuffer ... buffers) {
        super(contentType);
        this.buffers = buffers;
        int length = 0;
        for (ByteBuffer buffer : buffers) {
            length += buffer.remaining();
        }
        this.length = length;
    }

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    public boolean isReproducible() {
        return true;
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return new Iterator<ByteBuffer>(){
            private int index;

            @Override
            public boolean hasNext() {
                return this.index < ByteBufferContentProvider.this.buffers.length;
            }

            @Override
            public ByteBuffer next() {
                try {
                    ByteBuffer buffer = ByteBufferContentProvider.this.buffers[this.index];
                    ByteBufferContentProvider.this.buffers[this.index] = buffer.slice();
                    ++this.index;
                    return buffer;
                }
                catch (ArrayIndexOutOfBoundsException x) {
                    throw new NoSuchElementException();
                }
            }
        };
    }
}

