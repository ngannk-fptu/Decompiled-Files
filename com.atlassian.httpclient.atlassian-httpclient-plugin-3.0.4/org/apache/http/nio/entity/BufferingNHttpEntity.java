/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.entity.ContentInputStream;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
public class BufferingNHttpEntity
extends HttpEntityWrapper
implements ConsumingNHttpEntity {
    private static final int BUFFER_SIZE = 2048;
    private final SimpleInputBuffer buffer;
    private boolean finished;
    private boolean consumed;

    public BufferingNHttpEntity(HttpEntity httpEntity, ByteBufferAllocator allocator) {
        super(httpEntity);
        this.buffer = new SimpleInputBuffer(2048, allocator);
    }

    @Override
    public void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        this.buffer.consumeContent(decoder);
        if (decoder.isCompleted()) {
            this.finished = true;
        }
    }

    @Override
    public void finish() {
        this.finished = true;
    }

    @Override
    public InputStream getContent() throws IOException {
        Asserts.check(this.finished, "Entity content has not been fully received");
        Asserts.check(!this.consumed, "Entity content has been consumed");
        this.consumed = true;
        return new ContentInputStream(this.buffer);
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        int l;
        Args.notNull(outStream, "Output stream");
        InputStream inStream = this.getContent();
        byte[] buff = new byte[2048];
        while ((l = inStream.read(buff)) != -1) {
            outStream.write(buff, 0, l);
        }
    }
}

