/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.util;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.eclipse.jetty.client.util.AbstractTypedContentProvider;

@Deprecated
public class BytesContentProvider
extends AbstractTypedContentProvider {
    private final byte[][] bytes;
    private final long length;

    public BytesContentProvider(byte[] ... bytes) {
        this("application/octet-stream", bytes);
    }

    public BytesContentProvider(String contentType, byte[] ... bytes) {
        super(contentType);
        this.bytes = bytes;
        long length = 0L;
        for (byte[] buffer : bytes) {
            length += (long)buffer.length;
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
                return this.index < BytesContentProvider.this.bytes.length;
            }

            @Override
            public ByteBuffer next() {
                try {
                    return ByteBuffer.wrap(BytesContentProvider.this.bytes[this.index++]);
                }
                catch (ArrayIndexOutOfBoundsException x) {
                    throw new NoSuchElementException();
                }
            }
        };
    }
}

