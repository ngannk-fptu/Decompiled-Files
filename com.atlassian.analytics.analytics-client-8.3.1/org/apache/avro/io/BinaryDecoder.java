/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.InvalidNumberEncodingException;
import org.apache.avro.SystemLimitException;
import org.apache.avro.io.Decoder;
import org.apache.avro.util.Utf8;

public class BinaryDecoder
extends Decoder {
    private long collectionCount = 0L;
    private ByteSource source = null;
    private byte[] buf = null;
    private int minPos = 0;
    private int pos = 0;
    private int limit = 0;
    private final Utf8 scratchUtf8 = new Utf8();

    byte[] getBuf() {
        return this.buf;
    }

    int getPos() {
        return this.pos;
    }

    int getLimit() {
        return this.limit;
    }

    void setBuf(byte[] buf, int pos, int len) {
        this.buf = buf;
        this.pos = pos;
        this.limit = pos + len;
    }

    void clearBuf() {
        this.buf = null;
    }

    protected BinaryDecoder() {
    }

    BinaryDecoder(InputStream in, int bufferSize) {
        this();
        this.configure(in, bufferSize);
    }

    BinaryDecoder(byte[] data, int offset, int length) {
        this();
        this.configure(data, offset, length);
    }

    BinaryDecoder configure(InputStream in, int bufferSize) {
        this.configureSource(bufferSize, new InputStreamByteSource(in));
        return this;
    }

    BinaryDecoder configure(byte[] data, int offset, int length) {
        this.configureSource(8192, new ByteArrayByteSource(data, offset, length));
        return this;
    }

    private void configureSource(int bufferSize, ByteSource source) {
        if (null != this.source) {
            this.source.detach();
        }
        source.attach(bufferSize, this);
        this.source = source;
    }

    @Override
    public void readNull() throws IOException {
    }

    @Override
    public boolean readBoolean() throws IOException {
        int n;
        if (this.limit == this.pos) {
            this.limit = this.source.tryReadRaw(this.buf, 0, this.buf.length);
            this.pos = 0;
            if (this.limit == 0) {
                throw new EOFException();
            }
        }
        return (n = this.buf[this.pos++] & 0xFF) == 1;
    }

    @Override
    public int readInt() throws IOException {
        this.ensureBounds(5);
        int len = 1;
        int b = this.buf[this.pos] & 0xFF;
        int n = b & 0x7F;
        if (b > 127) {
            b = this.buf[this.pos + len++] & 0xFF;
            n ^= (b & 0x7F) << 7;
            if (b > 127) {
                b = this.buf[this.pos + len++] & 0xFF;
                n ^= (b & 0x7F) << 14;
                if (b > 127) {
                    b = this.buf[this.pos + len++] & 0xFF;
                    n ^= (b & 0x7F) << 21;
                    if (b > 127) {
                        b = this.buf[this.pos + len++] & 0xFF;
                        n ^= (b & 0x7F) << 28;
                        if (b > 127) {
                            throw new InvalidNumberEncodingException("Invalid int encoding");
                        }
                    }
                }
            }
        }
        this.pos += len;
        if (this.pos > this.limit) {
            throw new EOFException();
        }
        return n >>> 1 ^ -(n & 1);
    }

    @Override
    public long readLong() throws IOException {
        long l;
        this.ensureBounds(10);
        int b = this.buf[this.pos++] & 0xFF;
        int n = b & 0x7F;
        if (b > 127) {
            b = this.buf[this.pos++] & 0xFF;
            n ^= (b & 0x7F) << 7;
            if (b > 127) {
                b = this.buf[this.pos++] & 0xFF;
                n ^= (b & 0x7F) << 14;
                if (b > 127) {
                    b = this.buf[this.pos++] & 0xFF;
                    l = b > 127 ? this.innerLongDecode(n) : (long)(n ^= (b & 0x7F) << 21);
                } else {
                    l = n;
                }
            } else {
                l = n;
            }
        } else {
            l = n;
        }
        if (this.pos > this.limit) {
            throw new EOFException();
        }
        return l >>> 1 ^ -(l & 1L);
    }

    private long innerLongDecode(long l) throws IOException {
        int len = 1;
        int b = this.buf[this.pos] & 0xFF;
        l ^= ((long)b & 0x7FL) << 28;
        if (b > 127) {
            b = this.buf[this.pos + len++] & 0xFF;
            l ^= ((long)b & 0x7FL) << 35;
            if (b > 127) {
                b = this.buf[this.pos + len++] & 0xFF;
                l ^= ((long)b & 0x7FL) << 42;
                if (b > 127) {
                    b = this.buf[this.pos + len++] & 0xFF;
                    l ^= ((long)b & 0x7FL) << 49;
                    if (b > 127) {
                        b = this.buf[this.pos + len++] & 0xFF;
                        l ^= ((long)b & 0x7FL) << 56;
                        if (b > 127) {
                            b = this.buf[this.pos + len++] & 0xFF;
                            l ^= ((long)b & 0x7FL) << 63;
                            if (b > 127) {
                                throw new InvalidNumberEncodingException("Invalid long encoding");
                            }
                        }
                    }
                }
            }
        }
        this.pos += len;
        return l;
    }

    @Override
    public float readFloat() throws IOException {
        this.ensureBounds(4);
        int len = 1;
        int n = this.buf[this.pos] & 0xFF | (this.buf[this.pos + len++] & 0xFF) << 8 | (this.buf[this.pos + len++] & 0xFF) << 16 | (this.buf[this.pos + len++] & 0xFF) << 24;
        if (this.pos + 4 > this.limit) {
            throw new EOFException();
        }
        this.pos += 4;
        return Float.intBitsToFloat(n);
    }

    @Override
    public double readDouble() throws IOException {
        this.ensureBounds(8);
        int len = 1;
        int n1 = this.buf[this.pos] & 0xFF | (this.buf[this.pos + len++] & 0xFF) << 8 | (this.buf[this.pos + len++] & 0xFF) << 16 | (this.buf[this.pos + len++] & 0xFF) << 24;
        int n2 = this.buf[this.pos + len++] & 0xFF | (this.buf[this.pos + len++] & 0xFF) << 8 | (this.buf[this.pos + len++] & 0xFF) << 16 | (this.buf[this.pos + len++] & 0xFF) << 24;
        if (this.pos + 8 > this.limit) {
            throw new EOFException();
        }
        this.pos += 8;
        return Double.longBitsToDouble((long)n1 & 0xFFFFFFFFL | (long)n2 << 32);
    }

    @Override
    public Utf8 readString(Utf8 old) throws IOException {
        int length = SystemLimitException.checkMaxStringLength(this.readLong());
        Utf8 result = old != null ? old : new Utf8();
        result.setByteLength(length);
        if (0 != length) {
            this.doReadBytes(result.getBytes(), 0, length);
        }
        return result;
    }

    @Override
    public String readString() throws IOException {
        return this.readString(this.scratchUtf8).toString();
    }

    @Override
    public void skipString() throws IOException {
        this.doSkipBytes(this.readLong());
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer old) throws IOException {
        ByteBuffer result;
        int length = SystemLimitException.checkMaxBytesLength(this.readLong());
        if (old != null && length <= old.capacity()) {
            result = old;
            ((Buffer)result).clear();
        } else {
            result = ByteBuffer.allocate(length);
        }
        this.doReadBytes(result.array(), result.position(), length);
        ((Buffer)result).limit(length);
        return result;
    }

    @Override
    public void skipBytes() throws IOException {
        this.doSkipBytes(this.readLong());
    }

    @Override
    public void readFixed(byte[] bytes, int start, int length) throws IOException {
        this.doReadBytes(bytes, start, length);
    }

    @Override
    public void skipFixed(int length) throws IOException {
        this.doSkipBytes(length);
    }

    @Override
    public int readEnum() throws IOException {
        return this.readInt();
    }

    protected void doSkipBytes(long length) throws IOException {
        int remaining = this.limit - this.pos;
        if (length <= (long)remaining) {
            this.pos = (int)((long)this.pos + length);
        } else {
            this.pos = 0;
            this.limit = 0;
            this.source.skipSourceBytes(length -= (long)remaining);
        }
    }

    protected void doReadBytes(byte[] bytes, int start, int length) throws IOException {
        if (length < 0) {
            throw new AvroRuntimeException("Malformed data. Length is negative: " + length);
        }
        int remaining = this.limit - this.pos;
        if (length <= remaining) {
            System.arraycopy(this.buf, this.pos, bytes, start, length);
            this.pos += length;
        } else {
            System.arraycopy(this.buf, this.pos, bytes, start, remaining);
            this.pos = this.limit;
            this.source.readRaw(bytes, start += remaining, length -= remaining);
        }
    }

    protected long doReadItemCount() throws IOException {
        long result = this.readLong();
        if (result < 0L) {
            this.readLong();
            result = -result;
        }
        return result;
    }

    private long doSkipItems() throws IOException {
        long result = this.readLong();
        while (result < 0L) {
            long bytecount = this.readLong();
            this.doSkipBytes(bytecount);
            result = this.readLong();
        }
        return result;
    }

    @Override
    public long readArrayStart() throws IOException {
        this.collectionCount = SystemLimitException.checkMaxCollectionLength(0L, this.doReadItemCount());
        return this.collectionCount;
    }

    @Override
    public long arrayNext() throws IOException {
        long length = this.doReadItemCount();
        this.collectionCount = SystemLimitException.checkMaxCollectionLength(this.collectionCount, length);
        return length;
    }

    @Override
    public long skipArray() throws IOException {
        return this.doSkipItems();
    }

    @Override
    public long readMapStart() throws IOException {
        this.collectionCount = SystemLimitException.checkMaxCollectionLength(0L, this.doReadItemCount());
        return this.collectionCount;
    }

    @Override
    public long mapNext() throws IOException {
        long length = this.doReadItemCount();
        this.collectionCount = SystemLimitException.checkMaxCollectionLength(this.collectionCount, length);
        return length;
    }

    @Override
    public long skipMap() throws IOException {
        return this.doSkipItems();
    }

    @Override
    public int readIndex() throws IOException {
        return this.readInt();
    }

    public boolean isEnd() throws IOException {
        if (this.pos < this.limit) {
            return false;
        }
        if (this.source.isEof()) {
            return true;
        }
        int read = this.source.tryReadRaw(this.buf, 0, this.buf.length);
        this.pos = 0;
        this.limit = read;
        return 0 == read;
    }

    private void ensureBounds(int num) throws IOException {
        int remaining = this.limit - this.pos;
        if (remaining < num) {
            this.source.compactAndFill(this.buf, this.pos, this.minPos, remaining);
            if (this.pos >= this.limit) {
                throw new EOFException();
            }
        }
    }

    public InputStream inputStream() {
        return this.source;
    }

    static /* synthetic */ byte[] access$202(BinaryDecoder x0, byte[] x1) {
        x0.buf = x1;
        return x1;
    }

    private static class ByteArrayByteSource
    extends ByteSource {
        private static final int MIN_SIZE = 16;
        private byte[] data;
        private int position;
        private int max;
        private boolean compacted = false;

        private ByteArrayByteSource(byte[] data, int start, int len) {
            if (len < 16) {
                this.data = Arrays.copyOfRange(data, start, start + 16);
                this.position = 0;
                this.max = len;
            } else {
                this.data = data;
                this.position = start;
                this.max = start + len;
            }
        }

        @Override
        protected void attach(int bufferSize, BinaryDecoder decoder) {
            BinaryDecoder.access$202(decoder, this.data);
            decoder.pos = this.position;
            decoder.minPos = this.position;
            decoder.limit = this.max;
            this.ba = new BufferAccessor(decoder);
        }

        @Override
        protected void skipSourceBytes(long length) throws IOException {
            long skipped = this.trySkipBytes(length);
            if (skipped < length) {
                throw new EOFException();
            }
        }

        @Override
        protected long trySkipBytes(long length) throws IOException {
            this.max = this.ba.getLim();
            this.position = this.ba.getPos();
            long remaining = (long)this.max - (long)this.position;
            if (remaining >= length) {
                this.position = (int)((long)this.position + length);
                this.ba.setPos(this.position);
                return length;
            }
            this.position = (int)((long)this.position + remaining);
            this.ba.setPos(this.position);
            return remaining;
        }

        @Override
        protected void readRaw(byte[] data, int off, int len) throws IOException {
            int read = this.tryReadRaw(data, off, len);
            if (read < len) {
                throw new EOFException();
            }
        }

        @Override
        protected int tryReadRaw(byte[] data, int off, int len) throws IOException {
            return 0;
        }

        @Override
        protected void compactAndFill(byte[] buf, int pos, int minPos, int remaining) throws IOException {
            if (!this.compacted) {
                byte[] tinybuf = new byte[remaining + 16];
                System.arraycopy(buf, pos, tinybuf, 0, remaining);
                this.ba.setBuf(tinybuf, 0, remaining);
                this.compacted = true;
            }
        }

        @Override
        public int read() throws IOException {
            this.max = this.ba.getLim();
            this.position = this.ba.getPos();
            if (this.position >= this.max) {
                return -1;
            }
            int result = this.ba.getBuf()[this.position++] & 0xFF;
            this.ba.setPos(this.position);
            return result;
        }

        @Override
        public void close() throws IOException {
            this.ba.setPos(this.ba.getLim());
        }

        @Override
        public boolean isEof() {
            int remaining = this.ba.getLim() - this.ba.getPos();
            return remaining == 0;
        }
    }

    private static class InputStreamByteSource
    extends ByteSource {
        private InputStream in;
        protected boolean isEof = false;

        private InputStreamByteSource(InputStream in) {
            this.in = in;
        }

        @Override
        protected void skipSourceBytes(long length) throws IOException {
            boolean readZero = false;
            while (length > 0L) {
                long n = this.in.skip(length);
                if (n > 0L) {
                    length -= n;
                    continue;
                }
                if (n == 0L) {
                    if (readZero) {
                        this.isEof = true;
                        throw new EOFException();
                    }
                    readZero = true;
                    continue;
                }
                this.isEof = true;
                throw new EOFException();
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        protected long trySkipBytes(long length) throws IOException {
            long leftToSkip = length;
            try {
                boolean readZero = false;
                while (leftToSkip > 0L) {
                    long n = this.in.skip(length);
                    if (n > 0L) {
                        leftToSkip -= n;
                        continue;
                    }
                    if (n != 0L) {
                        this.isEof = true;
                        return length - leftToSkip;
                    }
                    if (readZero) {
                        this.isEof = true;
                        return length - leftToSkip;
                    }
                    readZero = true;
                }
                return length - leftToSkip;
            }
            catch (EOFException eof) {
                this.isEof = true;
            }
            return length - leftToSkip;
        }

        @Override
        protected void readRaw(byte[] data, int off, int len) throws IOException {
            while (len > 0) {
                int read = this.in.read(data, off, len);
                if (read < 0) {
                    this.isEof = true;
                    throw new EOFException();
                }
                len -= read;
                off += read;
            }
        }

        @Override
        protected int tryReadRaw(byte[] data, int off, int len) throws IOException {
            int leftToCopy = len;
            try {
                while (leftToCopy > 0) {
                    int read = this.in.read(data, off, leftToCopy);
                    if (read < 0) {
                        this.isEof = true;
                        break;
                    }
                    leftToCopy -= read;
                    off += read;
                }
            }
            catch (EOFException eof) {
                this.isEof = true;
            }
            return len - leftToCopy;
        }

        @Override
        public int read() throws IOException {
            if (this.ba.getLim() - this.ba.getPos() == 0) {
                return this.in.read();
            }
            int position = this.ba.getPos();
            int result = this.ba.getBuf()[position] & 0xFF;
            this.ba.setPos(position + 1);
            return result;
        }

        @Override
        public boolean isEof() {
            return this.isEof;
        }

        @Override
        public void close() throws IOException {
            this.in.close();
        }
    }

    static abstract class ByteSource
    extends InputStream {
        protected BufferAccessor ba;

        protected ByteSource() {
        }

        abstract boolean isEof();

        protected void attach(int bufferSize, BinaryDecoder decoder) {
            BinaryDecoder.access$202(decoder, new byte[bufferSize]);
            decoder.pos = 0;
            decoder.minPos = 0;
            decoder.limit = 0;
            this.ba = new BufferAccessor(decoder);
        }

        protected void detach() {
            this.ba.detach();
        }

        protected abstract void skipSourceBytes(long var1) throws IOException;

        protected abstract long trySkipBytes(long var1) throws IOException;

        protected abstract void readRaw(byte[] var1, int var2, int var3) throws IOException;

        protected abstract int tryReadRaw(byte[] var1, int var2, int var3) throws IOException;

        protected void compactAndFill(byte[] buf, int pos, int minPos, int remaining) throws IOException {
            System.arraycopy(buf, pos, buf, minPos, remaining);
            this.ba.setPos(minPos);
            int newLimit = remaining + this.tryReadRaw(buf, minPos + remaining, buf.length - remaining);
            this.ba.setLimit(newLimit);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int lim = this.ba.getLim();
            int pos = this.ba.getPos();
            byte[] buf = this.ba.getBuf();
            int remaining = lim - pos;
            if (remaining >= len) {
                System.arraycopy(buf, pos, b, off, len);
                this.ba.setPos(pos += len);
                return len;
            }
            System.arraycopy(buf, pos, b, off, remaining);
            this.ba.setPos(pos += remaining);
            int inputRead = remaining + this.tryReadRaw(b, off + remaining, len - remaining);
            if (inputRead == 0) {
                return -1;
            }
            return inputRead;
        }

        @Override
        public long skip(long n) throws IOException {
            int pos;
            int lim = this.ba.getLim();
            int remaining = lim - (pos = this.ba.getPos());
            if ((long)remaining > n) {
                pos = (int)((long)pos + n);
                this.ba.setPos(pos);
                return n;
            }
            pos = lim;
            this.ba.setPos(pos);
            long isSkipCount = this.trySkipBytes(n - (long)remaining);
            return isSkipCount + (long)remaining;
        }

        @Override
        public int available() throws IOException {
            return this.ba.getLim() - this.ba.getPos();
        }
    }

    static class BufferAccessor {
        private final BinaryDecoder decoder;
        private byte[] buf;
        private int pos;
        private int limit;
        boolean detached = false;

        private BufferAccessor(BinaryDecoder decoder) {
            this.decoder = decoder;
        }

        void detach() {
            this.buf = this.decoder.buf;
            this.pos = this.decoder.pos;
            this.limit = this.decoder.limit;
            this.detached = true;
        }

        int getPos() {
            if (this.detached) {
                return this.pos;
            }
            return this.decoder.pos;
        }

        int getLim() {
            if (this.detached) {
                return this.limit;
            }
            return this.decoder.limit;
        }

        byte[] getBuf() {
            if (this.detached) {
                return this.buf;
            }
            return this.decoder.buf;
        }

        void setPos(int pos) {
            if (this.detached) {
                this.pos = pos;
            } else {
                this.decoder.pos = pos;
            }
        }

        void setLimit(int limit) {
            if (this.detached) {
                this.limit = limit;
            } else {
                this.decoder.limit = limit;
            }
        }

        void setBuf(byte[] buf, int offset, int length) {
            if (this.detached) {
                this.buf = buf;
                this.limit = offset + length;
                this.pos = offset;
            } else {
                BinaryDecoder.access$202(this.decoder, buf);
                this.decoder.limit = offset + length;
                this.decoder.pos = offset;
                this.decoder.minPos = offset;
            }
        }
    }
}

