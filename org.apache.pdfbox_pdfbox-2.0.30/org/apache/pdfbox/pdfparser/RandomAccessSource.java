/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.SequentialSource;

final class RandomAccessSource
implements SequentialSource {
    private final RandomAccessRead reader;
    private boolean isOpen = true;

    RandomAccessSource(RandomAccessRead reader) {
        this.reader = reader;
    }

    @Override
    public int read() throws IOException {
        return this.reader.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.reader.read(b);
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException {
        return this.reader.read(b, offset, length);
    }

    @Override
    public long getPosition() throws IOException {
        return this.reader.getPosition();
    }

    @Override
    public int peek() throws IOException {
        return this.reader.peek();
    }

    @Override
    public void unread(int b) throws IOException {
        this.reader.rewind(1);
    }

    @Override
    public void unread(byte[] bytes) throws IOException {
        this.reader.rewind(bytes.length);
    }

    @Override
    public void unread(byte[] bytes, int start, int len) throws IOException {
        this.reader.rewind(len);
    }

    @Override
    public byte[] readFully(int length) throws IOException {
        return this.reader.readFully(length);
    }

    @Override
    public boolean isEOF() throws IOException {
        return this.reader.isEOF();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
        this.isOpen = false;
    }

    @Override
    public boolean isClosed() throws IOException {
        return !this.isOpen;
    }
}

