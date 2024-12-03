/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.client.methods;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Asserts;

public abstract class AsyncCharConsumer<T>
extends AbstractAsyncResponseConsumer<T> {
    private final ByteBuffer bbuf;
    private final CharBuffer cbuf;
    private CharsetDecoder charDecoder;

    public AsyncCharConsumer(int bufSize) {
        this.bbuf = ByteBuffer.allocate(bufSize);
        this.cbuf = CharBuffer.allocate(bufSize);
    }

    public AsyncCharConsumer() {
        this(8192);
    }

    protected abstract void onCharReceived(CharBuffer var1, IOControl var2) throws IOException;

    protected CharsetDecoder createDecoder(ContentType contentType) {
        Charset charset;
        Charset charset2 = charset = contentType != null ? contentType.getCharset() : null;
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        return charset.newDecoder();
    }

    @Override
    protected final void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException {
        this.charDecoder = this.createDecoder(contentType != null ? contentType : ContentType.DEFAULT_TEXT);
    }

    @Override
    protected final void onContentReceived(ContentDecoder decoder, IOControl ioControl) throws IOException {
        Asserts.notNull(this.bbuf, "Byte buffer");
        int bytesRead = decoder.read(this.bbuf);
        if (bytesRead <= 0) {
            return;
        }
        this.bbuf.flip();
        boolean completed = decoder.isCompleted();
        CoderResult result = this.charDecoder.decode(this.bbuf, this.cbuf, completed);
        this.handleDecodingResult(result, ioControl);
        this.bbuf.compact();
        if (completed) {
            result = this.charDecoder.flush(this.cbuf);
            this.handleDecodingResult(result, ioControl);
        }
    }

    private void handleDecodingResult(CoderResult result, IOControl ioControl) throws IOException {
        if (result.isError()) {
            result.throwException();
        }
        this.cbuf.flip();
        if (this.cbuf.hasRemaining()) {
            this.onCharReceived(this.cbuf, ioControl);
        }
        this.cbuf.clear();
    }

    @Override
    protected void releaseResources() {
    }
}

