/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ConsumingNHttpEntity;

@Deprecated
class NullNHttpEntity
extends HttpEntityWrapper
implements ConsumingNHttpEntity {
    private final ByteBuffer buffer = ByteBuffer.allocate(2048);

    public NullNHttpEntity(HttpEntity httpEntity) {
        super(httpEntity);
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Does not support blocking methods");
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Does not support blocking methods");
    }

    @Override
    public void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        int lastRead;
        do {
            this.buffer.clear();
        } while ((lastRead = decoder.read(this.buffer)) > 0);
    }

    @Override
    public void finish() {
    }
}

