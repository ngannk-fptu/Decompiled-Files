/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfparser;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import org.apache.pdfbox.pdfparser.SequentialSource;

final class InputStreamSource
implements SequentialSource {
    private final PushbackInputStream input;
    private int position;
    private boolean isOpen = true;

    InputStreamSource(InputStream input) {
        this.input = new PushbackInputStream(input, Short.MAX_VALUE);
        this.position = 0;
    }

    @Override
    public int read() throws IOException {
        int b = this.input.read();
        ++this.position;
        return b;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int n = this.input.read(b);
        if (n > 0) {
            this.position += n;
            return n;
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        int n = this.input.read(b, offset, length);
        if (n > 0) {
            this.position += n;
            return n;
        }
        return -1;
    }

    @Override
    public long getPosition() throws IOException {
        return this.position;
    }

    @Override
    public int peek() throws IOException {
        int b = this.input.read();
        if (b != -1) {
            this.input.unread(b);
        }
        return b;
    }

    @Override
    public void unread(int b) throws IOException {
        this.input.unread(b);
        --this.position;
    }

    @Override
    public void unread(byte[] bytes) throws IOException {
        this.input.unread(bytes);
        this.position -= bytes.length;
    }

    @Override
    public void unread(byte[] bytes, int start, int len) throws IOException {
        this.input.unread(bytes, start, len);
        this.position -= len;
    }

    @Override
    public byte[] readFully(int length) throws IOException {
        int count;
        byte[] bytes = new byte[length];
        int bytesRead = 0;
        do {
            if ((count = this.read(bytes, bytesRead, length - bytesRead)) >= 0) continue;
            throw new EOFException();
        } while ((bytesRead += count) < length);
        return bytes;
    }

    @Override
    public boolean isEOF() throws IOException {
        return this.peek() == -1;
    }

    @Override
    public void close() throws IOException {
        this.input.close();
        this.isOpen = false;
    }

    @Override
    public boolean isClosed() throws IOException {
        return !this.isOpen;
    }
}

