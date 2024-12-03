/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import zipkin2.internal.HexCodec;
import zipkin2.internal.JsonCodec;
import zipkin2.internal.RecyclableBuffers;

public abstract class ReadBuffer
extends InputStream {
    public static ReadBuffer wrapUnsafe(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            int offset = buffer.arrayOffset() + buffer.position();
            return ReadBuffer.wrap(buffer.array(), offset, buffer.remaining());
        }
        return buffer.order() == ByteOrder.BIG_ENDIAN ? new BigEndianByteBuffer(buffer) : new LittleEndianByteBuffer(buffer);
    }

    public static ReadBuffer wrap(byte[] bytes) {
        return ReadBuffer.wrap(bytes, 0, bytes.length);
    }

    public static ReadBuffer wrap(byte[] bytes, int pos, int length) {
        return new Array(bytes, pos, length);
    }

    @Override
    public abstract int read(byte[] var1, int var2, int var3);

    @Override
    public abstract long skip(long var1);

    @Override
    public abstract int available();

    @Override
    public void close() {
    }

    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    abstract byte readByteUnsafe();

    final byte readByte() {
        this.require(1);
        return this.readByteUnsafe();
    }

    abstract byte[] readBytes(int var1);

    final String readUtf8(int length) {
        if (length == 0) {
            return "";
        }
        this.require(length);
        if (length > 256) {
            return this.doReadUtf8(length);
        }
        char[] buffer = RecyclableBuffers.shortStringBuffer();
        if (this.tryReadAscii(buffer, length)) {
            return new String(buffer, 0, length);
        }
        return this.doReadUtf8(length);
    }

    abstract boolean tryReadAscii(char[] var1, int var2);

    abstract String doReadUtf8(int var1);

    abstract int pos();

    abstract short readShort();

    abstract int readInt();

    abstract long readLong();

    abstract long readLongLe();

    @Override
    public final int read() {
        return this.available() > 0 ? (int)this.readByteUnsafe() : -1;
    }

    final String readBytesAsHex(int length) {
        if (length > 32) {
            throw new IllegalArgumentException("hex field greater than 32 chars long: " + length);
        }
        this.require(length);
        char[] result = RecyclableBuffers.shortStringBuffer();
        int hexLength = length * 2;
        for (int i = 0; i < hexLength; i += 2) {
            byte b = this.readByteUnsafe();
            result[i + 0] = HexCodec.HEX_DIGITS[b >> 4 & 0xF];
            result[i + 1] = HexCodec.HEX_DIGITS[b & 0xF];
        }
        return new String(result, 0, hexLength);
    }

    final int readVarint32() {
        byte b = this.readByte();
        if (b >= 0) {
            return b;
        }
        int result = b & 0x7F;
        b = this.readByte();
        if (b >= 0) {
            return result | b << 7;
        }
        result |= (b & 0x7F) << 7;
        b = this.readByte();
        if (b >= 0) {
            return result | b << 14;
        }
        result |= (b & 0x7F) << 14;
        b = this.readByte();
        if (b >= 0) {
            return result | b << 21;
        }
        result |= (b & 0x7F) << 21;
        b = this.readByte();
        if ((b & 0xF0) != 0) {
            throw new IllegalArgumentException("Greater than 32-bit varint at position " + (this.pos() - 1));
        }
        return result | b << 28;
    }

    final long readVarint64() {
        byte b = this.readByte();
        if (b >= 0) {
            return b;
        }
        long result = b & 0x7F;
        for (int i = 1; b < 0 && i < 10; ++i) {
            b = this.readByte();
            if (i == 9 && (b & 0xF0) != 0) {
                throw new IllegalArgumentException("Greater than 64-bit varint at position " + (this.pos() - 1));
            }
            result |= (long)(b & 0x7F) << i * 7;
        }
        return result;
    }

    final void require(int byteCount) {
        if (this.available() < byteCount) {
            throw new IllegalArgumentException("Truncated: length " + byteCount + " > bytes available " + this.available());
        }
    }

    int checkReadArguments(byte[] dst, int offset, int length) {
        if (dst == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || length < 0 || length > dst.length - offset) {
            throw new IndexOutOfBoundsException();
        }
        return Math.min(this.available(), length);
    }

    static final class Array
    extends ReadBuffer {
        final byte[] buf;
        int arrayOffset;
        int offset;
        int length;

        Array(byte[] buf, int offset, int length) {
            this.buf = buf;
            this.arrayOffset = this.offset = offset;
            this.length = length;
        }

        @Override
        final byte readByteUnsafe() {
            return this.buf[this.offset++];
        }

        @Override
        final byte[] readBytes(int length) {
            this.require(length);
            byte[] result = new byte[length];
            System.arraycopy(this.buf, this.offset, result, 0, length);
            this.offset += length;
            return result;
        }

        @Override
        public int read(byte[] dst, int offset, int length) {
            if (this.available() == 0) {
                return -1;
            }
            int toRead = this.checkReadArguments(dst, offset, length);
            if (toRead == 0) {
                return 0;
            }
            System.arraycopy(this.buf, this.offset, dst, 0, toRead);
            this.offset += toRead;
            return toRead;
        }

        @Override
        boolean tryReadAscii(char[] destination, int length) {
            for (int i = 0; i < length; ++i) {
                byte b = this.buf[this.offset + i];
                if ((b & 0x80) != 0) {
                    return false;
                }
                destination[i] = (char)b;
            }
            this.offset += length;
            return true;
        }

        @Override
        final String doReadUtf8(int length) {
            String result = new String(this.buf, this.offset, length, JsonCodec.UTF_8);
            this.offset += length;
            return result;
        }

        @Override
        short readShort() {
            this.require(2);
            return (short)((this.buf[this.offset++] & 0xFF) << 8 | this.buf[this.offset++] & 0xFF);
        }

        @Override
        int readInt() {
            this.require(4);
            int pos = this.offset;
            this.offset = pos + 4;
            return (this.buf[pos] & 0xFF) << 24 | (this.buf[pos + 1] & 0xFF) << 16 | (this.buf[pos + 2] & 0xFF) << 8 | this.buf[pos + 3] & 0xFF;
        }

        @Override
        long readLong() {
            return Long.reverseBytes(this.readLongLe());
        }

        @Override
        long readLongLe() {
            this.require(8);
            int pos = this.offset;
            this.offset = pos + 8;
            return (long)this.buf[pos] & 0xFFL | ((long)this.buf[pos + 1] & 0xFFL) << 8 | ((long)this.buf[pos + 2] & 0xFFL) << 16 | ((long)this.buf[pos + 3] & 0xFFL) << 24 | ((long)this.buf[pos + 4] & 0xFFL) << 32 | ((long)this.buf[pos + 5] & 0xFFL) << 40 | ((long)this.buf[pos + 6] & 0xFFL) << 48 | ((long)this.buf[pos + 7] & 0xFFL) << 56;
        }

        @Override
        public int pos() {
            return this.offset - this.arrayOffset;
        }

        @Override
        public long skip(long maxCount) {
            int toSkip = Math.min(this.available(), (int)maxCount);
            this.offset += toSkip;
            return toSkip;
        }

        @Override
        public int available() {
            return this.length - (this.offset - this.arrayOffset);
        }
    }

    static abstract class Buff
    extends ReadBuffer {
        final ByteBuffer buf;

        Buff(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        final byte readByteUnsafe() {
            return this.buf.get();
        }

        @Override
        final byte[] readBytes(int length) {
            this.require(length);
            byte[] copy = new byte[length];
            this.buf.get(copy);
            return copy;
        }

        @Override
        boolean tryReadAscii(char[] destination, int length) {
            this.buf.mark();
            for (int i = 0; i < length; ++i) {
                byte b = this.buf.get();
                if ((b & 0x80) != 0) {
                    this.buf.reset();
                    return false;
                }
                destination[i] = (char)b;
            }
            return true;
        }

        @Override
        final String doReadUtf8(int length) {
            return new String(this.readBytes(length), JsonCodec.UTF_8);
        }

        @Override
        public int pos() {
            return this.buf.position();
        }

        @Override
        public int read(byte[] dst, int offset, int length) {
            if (this.available() == 0) {
                return -1;
            }
            int toRead = this.checkReadArguments(dst, offset, length);
            if (toRead == 0) {
                return 0;
            }
            this.buf.get(dst, offset, toRead);
            return toRead;
        }

        @Override
        public long skip(long maxCount) {
            int skipped = Math.max(this.available(), (int)maxCount);
            this.buf.position(this.buf.position() + skipped);
            return skipped;
        }

        @Override
        public int available() {
            return this.buf.remaining();
        }
    }

    static final class LittleEndianByteBuffer
    extends Buff {
        LittleEndianByteBuffer(ByteBuffer buf) {
            super(buf);
        }

        @Override
        short readShort() {
            this.require(2);
            return Short.reverseBytes(this.buf.getShort());
        }

        @Override
        int readInt() {
            this.require(4);
            return Integer.reverseBytes(this.buf.getInt());
        }

        @Override
        long readLong() {
            return Long.reverseBytes(this.readLongLe());
        }

        @Override
        long readLongLe() {
            this.require(8);
            return this.buf.getLong();
        }
    }

    static final class BigEndianByteBuffer
    extends Buff {
        BigEndianByteBuffer(ByteBuffer buf) {
            super(buf);
        }

        @Override
        short readShort() {
            this.require(2);
            return this.buf.getShort();
        }

        @Override
        int readInt() {
            this.require(4);
            return this.buf.getInt();
        }

        @Override
        long readLong() {
            this.require(8);
            return this.buf.getLong();
        }

        @Override
        long readLongLe() {
            return Long.reverseBytes(this.readLong());
        }
    }
}

