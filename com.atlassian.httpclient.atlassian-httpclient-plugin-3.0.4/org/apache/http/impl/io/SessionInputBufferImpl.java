/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import org.apache.http.MessageConstraintException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;

public class SessionInputBufferImpl
implements SessionInputBuffer,
BufferInfo {
    private final HttpTransportMetricsImpl metrics;
    private final byte[] buffer;
    private final ByteArrayBuffer lineBuffer;
    private final int minChunkLimit;
    private final MessageConstraints constraints;
    private final CharsetDecoder decoder;
    private InputStream inStream;
    private int bufferPos;
    private int bufferLen;
    private CharBuffer cbuf;

    public SessionInputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize, int minChunkLimit, MessageConstraints constraints, CharsetDecoder charDecoder) {
        Args.notNull(metrics, "HTTP transport metrcis");
        Args.positive(bufferSize, "Buffer size");
        this.metrics = metrics;
        this.buffer = new byte[bufferSize];
        this.bufferPos = 0;
        this.bufferLen = 0;
        this.minChunkLimit = minChunkLimit >= 0 ? minChunkLimit : 512;
        this.constraints = constraints != null ? constraints : MessageConstraints.DEFAULT;
        this.lineBuffer = new ByteArrayBuffer(bufferSize);
        this.decoder = charDecoder;
    }

    public SessionInputBufferImpl(HttpTransportMetricsImpl metrics, int bufferSize) {
        this(metrics, bufferSize, bufferSize, null, null);
    }

    public void bind(InputStream inputStream) {
        this.inStream = inputStream;
    }

    public boolean isBound() {
        return this.inStream != null;
    }

    @Override
    public int capacity() {
        return this.buffer.length;
    }

    @Override
    public int length() {
        return this.bufferLen - this.bufferPos;
    }

    @Override
    public int available() {
        return this.capacity() - this.length();
    }

    private int streamRead(byte[] b, int off, int len) throws IOException {
        Asserts.notNull(this.inStream, "Input stream");
        return this.inStream.read(b, off, len);
    }

    public int fillBuffer() throws IOException {
        int len;
        int off;
        int readLen;
        if (this.bufferPos > 0) {
            int len2 = this.bufferLen - this.bufferPos;
            if (len2 > 0) {
                System.arraycopy(this.buffer, this.bufferPos, this.buffer, 0, len2);
            }
            this.bufferPos = 0;
            this.bufferLen = len2;
        }
        if ((readLen = this.streamRead(this.buffer, off = this.bufferLen, len = this.buffer.length - off)) == -1) {
            return -1;
        }
        this.bufferLen = off + readLen;
        this.metrics.incrementBytesTransferred(readLen);
        return readLen;
    }

    public boolean hasBufferedData() {
        return this.bufferPos < this.bufferLen;
    }

    public void clear() {
        this.bufferPos = 0;
        this.bufferLen = 0;
    }

    @Override
    public int read() throws IOException {
        while (!this.hasBufferedData()) {
            int noRead = this.fillBuffer();
            if (noRead != -1) continue;
            return -1;
        }
        return this.buffer[this.bufferPos++] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            return 0;
        }
        if (this.hasBufferedData()) {
            int chunk = Math.min(len, this.bufferLen - this.bufferPos);
            System.arraycopy(this.buffer, this.bufferPos, b, off, chunk);
            this.bufferPos += chunk;
            return chunk;
        }
        if (len > this.minChunkLimit) {
            int readLen = this.streamRead(b, off, len);
            if (readLen > 0) {
                this.metrics.incrementBytesTransferred(readLen);
            }
            return readLen;
        }
        while (!this.hasBufferedData()) {
            int noRead = this.fillBuffer();
            if (noRead != -1) continue;
            return -1;
        }
        int chunk = Math.min(len, this.bufferLen - this.bufferPos);
        System.arraycopy(this.buffer, this.bufferPos, b, off, chunk);
        this.bufferPos += chunk;
        return chunk;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (b == null) {
            return 0;
        }
        return this.read(b, 0, b.length);
    }

    @Override
    public int readLine(CharArrayBuffer charbuffer) throws IOException {
        Args.notNull(charbuffer, "Char array buffer");
        int maxLineLen = this.constraints.getMaxLineLength();
        int noRead = 0;
        boolean retry = true;
        while (retry) {
            int len;
            int currentLen;
            int pos = -1;
            for (int i = this.bufferPos; i < this.bufferLen; ++i) {
                if (this.buffer[i] != 10) continue;
                pos = i;
                break;
            }
            if (maxLineLen > 0 && (currentLen = this.lineBuffer.length() + (pos >= 0 ? pos : this.bufferLen) - this.bufferPos) >= maxLineLen) {
                throw new MessageConstraintException("Maximum line length limit exceeded");
            }
            if (pos != -1) {
                if (this.lineBuffer.isEmpty()) {
                    return this.lineFromReadBuffer(charbuffer, pos);
                }
                retry = false;
                len = pos + 1 - this.bufferPos;
                this.lineBuffer.append(this.buffer, this.bufferPos, len);
                this.bufferPos = pos + 1;
                continue;
            }
            if (this.hasBufferedData()) {
                len = this.bufferLen - this.bufferPos;
                this.lineBuffer.append(this.buffer, this.bufferPos, len);
                this.bufferPos = this.bufferLen;
            }
            if ((noRead = this.fillBuffer()) != -1) continue;
            retry = false;
        }
        if (noRead == -1 && this.lineBuffer.isEmpty()) {
            return -1;
        }
        return this.lineFromLineBuffer(charbuffer);
    }

    private int lineFromLineBuffer(CharArrayBuffer charbuffer) throws IOException {
        int len = this.lineBuffer.length();
        if (len > 0) {
            if (this.lineBuffer.byteAt(len - 1) == 10) {
                --len;
            }
            if (len > 0 && this.lineBuffer.byteAt(len - 1) == 13) {
                --len;
            }
        }
        if (this.decoder == null) {
            charbuffer.append(this.lineBuffer, 0, len);
        } else {
            ByteBuffer bbuf = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, len);
            len = this.appendDecoded(charbuffer, bbuf);
        }
        this.lineBuffer.clear();
        return len;
    }

    private int lineFromReadBuffer(CharArrayBuffer charbuffer, int position) throws IOException {
        int pos = position;
        int off = this.bufferPos;
        this.bufferPos = pos + 1;
        if (pos > off && this.buffer[pos - 1] == 13) {
            --pos;
        }
        int len = pos - off;
        if (this.decoder == null) {
            charbuffer.append(this.buffer, off, len);
        } else {
            ByteBuffer bbuf = ByteBuffer.wrap(this.buffer, off, len);
            len = this.appendDecoded(charbuffer, bbuf);
        }
        return len;
    }

    private int appendDecoded(CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
        CoderResult result;
        if (!bbuf.hasRemaining()) {
            return 0;
        }
        if (this.cbuf == null) {
            this.cbuf = CharBuffer.allocate(1024);
        }
        this.decoder.reset();
        int len = 0;
        while (bbuf.hasRemaining()) {
            result = this.decoder.decode(bbuf, this.cbuf, true);
            len += this.handleDecodingResult(result, charbuffer, bbuf);
        }
        result = this.decoder.flush(this.cbuf);
        this.cbuf.clear();
        return len += this.handleDecodingResult(result, charbuffer, bbuf);
    }

    private int handleDecodingResult(CoderResult result, CharArrayBuffer charbuffer, ByteBuffer bbuf) throws IOException {
        if (result.isError()) {
            result.throwException();
        }
        this.cbuf.flip();
        int len = this.cbuf.remaining();
        while (this.cbuf.hasRemaining()) {
            charbuffer.append(this.cbuf.get());
        }
        this.cbuf.compact();
        return len;
    }

    @Override
    public String readLine() throws IOException {
        CharArrayBuffer charbuffer = new CharArrayBuffer(64);
        int readLen = this.readLine(charbuffer);
        return readLen != -1 ? charbuffer.toString() : null;
    }

    @Override
    public boolean isDataAvailable(int timeout) throws IOException {
        return this.hasBufferedData();
    }

    @Override
    public HttpTransportMetrics getMetrics() {
        return this.metrics;
    }
}

