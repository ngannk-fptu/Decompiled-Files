/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.GZIPContentDecoder
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.ByteBufferPool
 */
package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.ListIterator;
import org.eclipse.jetty.client.ContentDecoder;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.ByteBufferPool;

public class GZIPContentDecoder
extends org.eclipse.jetty.http.GZIPContentDecoder
implements ContentDecoder {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private long decodedLength;

    public GZIPContentDecoder() {
        this(8192);
    }

    public GZIPContentDecoder(int bufferSize) {
        this(null, bufferSize);
    }

    public GZIPContentDecoder(ByteBufferPool byteBufferPool, int bufferSize) {
        super(byteBufferPool, bufferSize);
    }

    @Override
    public void beforeDecoding(HttpExchange exchange) {
        exchange.getResponse().headers(headers -> {
            ListIterator iterator = headers.listIterator();
            while (iterator.hasNext()) {
                HttpField field = (HttpField)iterator.next();
                HttpHeader header = field.getHeader();
                if (header == HttpHeader.CONTENT_LENGTH) {
                    iterator.remove();
                    continue;
                }
                if (header != HttpHeader.CONTENT_ENCODING) continue;
                String value = field.getValue();
                int comma = value.lastIndexOf(",");
                if (comma < 0) {
                    iterator.remove();
                    continue;
                }
                iterator.set(new HttpField(HttpHeader.CONTENT_ENCODING, value.substring(0, comma)));
            }
        });
    }

    protected boolean decodedChunk(ByteBuffer chunk) {
        this.decodedLength += (long)chunk.remaining();
        super.decodedChunk(chunk);
        return true;
    }

    @Override
    public void afterDecoding(HttpExchange exchange) {
        exchange.getResponse().headers(headers -> {
            headers.remove(HttpHeader.TRANSFER_ENCODING);
            headers.putLongField(HttpHeader.CONTENT_LENGTH, this.decodedLength);
        });
    }

    public static class Factory
    extends ContentDecoder.Factory {
        private final int bufferSize;
        private final ByteBufferPool byteBufferPool;

        public Factory() {
            this(8192);
        }

        public Factory(int bufferSize) {
            this(null, bufferSize);
        }

        public Factory(ByteBufferPool byteBufferPool) {
            this(byteBufferPool, 8192);
        }

        public Factory(ByteBufferPool byteBufferPool, int bufferSize) {
            super("gzip");
            this.byteBufferPool = byteBufferPool;
            this.bufferSize = bufferSize;
        }

        @Override
        public ContentDecoder newContentDecoder() {
            return new GZIPContentDecoder(this.byteBufferPool, this.bufferSize);
        }
    }
}

