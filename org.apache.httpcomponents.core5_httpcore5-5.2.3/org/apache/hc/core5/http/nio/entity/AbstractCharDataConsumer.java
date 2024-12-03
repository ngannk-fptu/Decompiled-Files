/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.nio.AsyncDataConsumer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.util.Args;

public abstract class AbstractCharDataConsumer
implements AsyncDataConsumer {
    protected static final int DEF_BUF_SIZE = 8192;
    private static final ByteBuffer EMPTY_BIN = ByteBuffer.wrap(new byte[0]);
    private final CharBuffer charBuffer;
    private final CharCodingConfig charCodingConfig;
    private volatile Charset charset;
    private volatile CharsetDecoder charsetDecoder;
    private volatile ByteBuffer byteBuffer;

    protected AbstractCharDataConsumer(int bufSize, CharCodingConfig charCodingConfig) {
        this.charBuffer = CharBuffer.allocate(Args.positive(bufSize, "Buffer size"));
        this.charCodingConfig = charCodingConfig != null ? charCodingConfig : CharCodingConfig.DEFAULT;
    }

    public AbstractCharDataConsumer() {
        this(8192, CharCodingConfig.DEFAULT);
    }

    protected abstract int capacityIncrement();

    protected abstract void data(CharBuffer var1, boolean var2) throws IOException;

    protected abstract void completed() throws IOException;

    protected final void setCharset(Charset charset) {
        this.charset = charset != null ? charset : this.charCodingConfig.getCharset();
        this.charsetDecoder = null;
    }

    @Override
    public final void updateCapacity(CapacityChannel capacityChannel) throws IOException {
        capacityChannel.update(this.capacityIncrement());
    }

    private void checkResult(CoderResult result) throws IOException {
        if (result.isError()) {
            result.throwException();
        }
    }

    private void doDecode(boolean endOfStream) throws IOException {
        this.charBuffer.flip();
        this.data(this.charBuffer, endOfStream);
        this.charBuffer.clear();
    }

    private CharsetDecoder getCharsetDecoder() {
        if (this.charsetDecoder == null) {
            Charset charset = this.charset;
            if (charset == null) {
                charset = this.charCodingConfig.getCharset();
            }
            if (charset == null) {
                charset = StandardCharsets.US_ASCII;
            }
            this.charsetDecoder = charset.newDecoder();
            if (this.charCodingConfig.getMalformedInputAction() != null) {
                this.charsetDecoder.onMalformedInput(this.charCodingConfig.getMalformedInputAction());
            }
            if (this.charCodingConfig.getUnmappableInputAction() != null) {
                this.charsetDecoder.onUnmappableCharacter(this.charCodingConfig.getUnmappableInputAction());
            }
        }
        return this.charsetDecoder;
    }

    @Override
    public final void consume(ByteBuffer src) throws IOException {
        CharsetDecoder charsetDecoder = this.getCharsetDecoder();
        while (src.hasRemaining()) {
            if (this.byteBuffer != null && this.byteBuffer.position() > 0) {
                int n = this.byteBuffer.remaining();
                if (n < src.remaining()) {
                    int oldLimit = src.limit();
                    src.limit(src.position() + n);
                    this.byteBuffer.put(src);
                    src.limit(oldLimit);
                } else {
                    this.byteBuffer.put(src);
                }
                this.byteBuffer.flip();
                CoderResult r = charsetDecoder.decode(this.byteBuffer, this.charBuffer, false);
                this.checkResult(r);
                this.doDecode(false);
                this.byteBuffer.compact();
            }
            if (this.byteBuffer != null && this.byteBuffer.position() != 0) continue;
            CoderResult r = charsetDecoder.decode(src, this.charBuffer, false);
            this.checkResult(r);
            this.doDecode(false);
            if (!r.isUnderflow() || !src.hasRemaining()) continue;
            if (this.byteBuffer == null) {
                this.byteBuffer = ByteBuffer.allocate(Math.max(src.remaining(), 1024));
            }
            this.byteBuffer.put(src);
        }
    }

    @Override
    public final void streamEnd(List<? extends Header> trailers) throws HttpException, IOException {
        CharsetDecoder charsetDecoder = this.getCharsetDecoder();
        this.checkResult(charsetDecoder.decode(EMPTY_BIN, this.charBuffer, true));
        this.doDecode(false);
        this.checkResult(charsetDecoder.flush(this.charBuffer));
        this.doDecode(true);
        this.completed();
    }
}

