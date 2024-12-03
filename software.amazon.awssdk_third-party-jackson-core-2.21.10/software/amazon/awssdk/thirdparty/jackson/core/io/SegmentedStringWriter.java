/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.io;

import java.io.IOException;
import java.io.Writer;
import software.amazon.awssdk.thirdparty.jackson.core.util.BufferRecycler;
import software.amazon.awssdk.thirdparty.jackson.core.util.TextBuffer;

public final class SegmentedStringWriter
extends Writer {
    private final TextBuffer _buffer;

    public SegmentedStringWriter(BufferRecycler br) {
        this._buffer = new TextBuffer(br);
    }

    @Override
    public Writer append(char c) throws IOException {
        this.write(c);
        return this;
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        String str = csq.toString();
        this._buffer.append(str, 0, str.length());
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        String str = csq.subSequence(start, end).toString();
        this._buffer.append(str, 0, str.length());
        return this;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        this._buffer.append(cbuf, 0, cbuf.length);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        this._buffer.append(cbuf, off, len);
    }

    @Override
    public void write(int c) throws IOException {
        this._buffer.append((char)c);
    }

    @Override
    public void write(String str) throws IOException {
        this._buffer.append(str, 0, str.length());
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        this._buffer.append(str, off, len);
    }

    public String getAndClear() throws IOException {
        String result = this._buffer.contentsAsString();
        this._buffer.releaseBuffers();
        return result;
    }
}

