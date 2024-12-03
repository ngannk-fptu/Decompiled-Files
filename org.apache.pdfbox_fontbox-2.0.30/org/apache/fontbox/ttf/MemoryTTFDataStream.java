/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.fontbox.ttf.TTFDataStream;

class MemoryTTFDataStream
extends TTFDataStream {
    private byte[] data = null;
    private int currentPosition = 0;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    MemoryTTFDataStream(InputStream is) throws IOException {
        try {
            int amountRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream(is.available());
            byte[] buffer = new byte[1024];
            while ((amountRead = is.read(buffer)) != -1) {
                output.write(buffer, 0, amountRead);
            }
            this.data = output.toByteArray();
        }
        finally {
            is.close();
        }
    }

    @Override
    public long readLong() throws IOException {
        return ((long)this.readSignedInt() << 32) + ((long)this.readSignedInt() & 0xFFFFFFFFL);
    }

    public int readSignedInt() throws IOException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read()) | (ch3 = this.read()) | (ch4 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    @Override
    public int read() throws IOException {
        if (this.currentPosition >= this.data.length) {
            return -1;
        }
        byte retval = this.data[this.currentPosition];
        ++this.currentPosition;
        return (retval + 256) % 256;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + ch2;
    }

    @Override
    public short readSignedShort() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (short)((ch1 << 8) + ch2);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void seek(long pos) throws IOException {
        if (pos < 0L || pos > Integer.MAX_VALUE) {
            throw new IOException("Illegal seek position: " + pos);
        }
        this.currentPosition = (int)pos;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.currentPosition < this.data.length) {
            int amountRead = Math.min(len, this.data.length - this.currentPosition);
            System.arraycopy(this.data, this.currentPosition, b, off, amountRead);
            this.currentPosition += amountRead;
            return amountRead;
        }
        return -1;
    }

    @Override
    public long getCurrentPosition() throws IOException {
        return this.currentPosition;
    }

    @Override
    public InputStream getOriginalData() throws IOException {
        return new ByteArrayInputStream(this.data);
    }

    @Override
    public long getOriginalDataSize() {
        return this.data.length;
    }
}

