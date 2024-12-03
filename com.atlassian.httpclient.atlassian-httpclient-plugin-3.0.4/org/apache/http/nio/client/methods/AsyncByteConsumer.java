/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.client.methods;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.util.Asserts;

public abstract class AsyncByteConsumer<T>
extends AbstractAsyncResponseConsumer<T> {
    private final ByteBuffer bbuf;

    public AsyncByteConsumer(int bufSize) {
        this.bbuf = ByteBuffer.allocate(bufSize);
    }

    public AsyncByteConsumer() {
        this(8192);
    }

    protected abstract void onByteReceived(ByteBuffer var1, IOControl var2) throws IOException;

    @Override
    protected final void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException {
    }

    @Override
    protected final void onContentReceived(ContentDecoder decoder, IOControl ioControl) throws IOException {
        Asserts.notNull(this.bbuf, "Byte buffer");
        int bytesRead = decoder.read(this.bbuf);
        if (bytesRead <= 0) {
            return;
        }
        this.bbuf.flip();
        this.onByteReceived(this.bbuf, ioControl);
        this.bbuf.clear();
    }

    @Override
    protected void releaseResources() {
    }
}

