/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.avro.InvalidNumberEncodingException;
import org.apache.avro.SystemLimitException;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.util.ByteBufferInputStream;

class DirectBinaryDecoder
extends BinaryDecoder {
    private InputStream in;
    private ByteReader byteReader;
    private final byte[] buf = new byte[8];

    DirectBinaryDecoder(InputStream in) {
        this.configure(in);
    }

    DirectBinaryDecoder configure(InputStream in) {
        this.in = in;
        this.byteReader = in instanceof ByteBufferInputStream ? new ReuseByteReader((ByteBufferInputStream)in) : new ByteReader();
        return this;
    }

    @Override
    public boolean readBoolean() throws IOException {
        int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n == 1;
    }

    @Override
    public int readInt() throws IOException {
        int n = 0;
        int shift = 0;
        do {
            int b;
            if ((b = this.in.read()) >= 0) {
                n |= (b & 0x7F) << shift;
                if ((b & 0x80) != 0) continue;
                return n >>> 1 ^ -(n & 1);
            }
            throw new EOFException();
        } while ((shift += 7) < 32);
        throw new InvalidNumberEncodingException("Invalid int encoding");
    }

    @Override
    public long readLong() throws IOException {
        long n = 0L;
        int shift = 0;
        do {
            int b;
            if ((b = this.in.read()) >= 0) {
                n |= ((long)b & 0x7FL) << shift;
                if ((b & 0x80) != 0) continue;
                return n >>> 1 ^ -(n & 1L);
            }
            throw new EOFException();
        } while ((shift += 7) < 64);
        throw new InvalidNumberEncodingException("Invalid long encoding");
    }

    @Override
    public float readFloat() throws IOException {
        this.doReadBytes(this.buf, 0, 4);
        int n = this.buf[0] & 0xFF | (this.buf[1] & 0xFF) << 8 | (this.buf[2] & 0xFF) << 16 | (this.buf[3] & 0xFF) << 24;
        return Float.intBitsToFloat(n);
    }

    @Override
    public double readDouble() throws IOException {
        this.doReadBytes(this.buf, 0, 8);
        long n = (long)this.buf[0] & 0xFFL | ((long)this.buf[1] & 0xFFL) << 8 | ((long)this.buf[2] & 0xFFL) << 16 | ((long)this.buf[3] & 0xFFL) << 24 | ((long)this.buf[4] & 0xFFL) << 32 | ((long)this.buf[5] & 0xFFL) << 40 | ((long)this.buf[6] & 0xFFL) << 48 | ((long)this.buf[7] & 0xFFL) << 56;
        return Double.longBitsToDouble(n);
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer old) throws IOException {
        long length = this.readLong();
        return this.byteReader.read(old, SystemLimitException.checkMaxBytesLength(length));
    }

    @Override
    protected void doSkipBytes(long length) throws IOException {
        while (length > 0L) {
            long n = this.in.skip(length);
            if (n <= 0L) {
                throw new EOFException();
            }
            length -= n;
        }
    }

    @Override
    protected void doReadBytes(byte[] bytes, int start, int length) throws IOException {
        int n;
        while ((n = this.in.read(bytes, start, length)) != length && length != 0) {
            if (n < 0) {
                throw new EOFException();
            }
            start += n;
            length -= n;
        }
        return;
    }

    @Override
    public InputStream inputStream() {
        return this.in;
    }

    @Override
    public boolean isEnd() throws IOException {
        throw new UnsupportedOperationException();
    }

    private class ReuseByteReader
    extends ByteReader {
        private final ByteBufferInputStream bbi;

        public ReuseByteReader(ByteBufferInputStream bbi) {
            this.bbi = bbi;
        }

        @Override
        public ByteBuffer read(ByteBuffer old, int length) throws IOException {
            if (old != null) {
                return super.read(old, length);
            }
            return this.bbi.readBuffer(length);
        }
    }

    private class ByteReader {
        private ByteReader() {
        }

        public ByteBuffer read(ByteBuffer old, int length) throws IOException {
            ByteBuffer result;
            if (old != null && length <= old.capacity()) {
                result = old;
                result.clear();
            } else {
                result = ByteBuffer.allocate(length);
            }
            DirectBinaryDecoder.this.doReadBytes(result.array(), result.position(), length);
            result.limit(length);
            return result;
        }
    }
}

