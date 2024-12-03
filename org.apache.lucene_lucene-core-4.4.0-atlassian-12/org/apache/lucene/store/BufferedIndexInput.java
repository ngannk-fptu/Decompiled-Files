/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.EOFException;
import java.io.IOException;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

public abstract class BufferedIndexInput
extends IndexInput {
    public static final int BUFFER_SIZE = 1024;
    public static final int MERGE_BUFFER_SIZE = 4096;
    private int bufferSize = 1024;
    protected byte[] buffer;
    private long bufferStart = 0L;
    private int bufferLength = 0;
    private int bufferPosition = 0;

    @Override
    public final byte readByte() throws IOException {
        if (this.bufferPosition >= this.bufferLength) {
            this.refill();
        }
        return this.buffer[this.bufferPosition++];
    }

    public BufferedIndexInput(String resourceDesc) {
        this(resourceDesc, 1024);
    }

    public BufferedIndexInput(String resourceDesc, IOContext context) {
        this(resourceDesc, BufferedIndexInput.bufferSize(context));
    }

    public BufferedIndexInput(String resourceDesc, int bufferSize) {
        super(resourceDesc);
        this.checkBufferSize(bufferSize);
        this.bufferSize = bufferSize;
    }

    public final void setBufferSize(int newSize) {
        assert (this.buffer == null || this.bufferSize == this.buffer.length) : "buffer=" + this.buffer + " bufferSize=" + this.bufferSize + " buffer.length=" + (this.buffer != null ? this.buffer.length : 0);
        if (newSize != this.bufferSize) {
            this.checkBufferSize(newSize);
            this.bufferSize = newSize;
            if (this.buffer != null) {
                byte[] newBuffer = new byte[newSize];
                int leftInBuffer = this.bufferLength - this.bufferPosition;
                int numToCopy = leftInBuffer > newSize ? newSize : leftInBuffer;
                System.arraycopy(this.buffer, this.bufferPosition, newBuffer, 0, numToCopy);
                this.bufferStart += (long)this.bufferPosition;
                this.bufferPosition = 0;
                this.bufferLength = numToCopy;
                this.newBuffer(newBuffer);
            }
        }
    }

    protected void newBuffer(byte[] newBuffer) {
        this.buffer = newBuffer;
    }

    public final int getBufferSize() {
        return this.bufferSize;
    }

    private void checkBufferSize(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be greater than 0 (got " + bufferSize + ")");
        }
    }

    @Override
    public final void readBytes(byte[] b, int offset, int len) throws IOException {
        this.readBytes(b, offset, len, true);
    }

    @Override
    public final void readBytes(byte[] b, int offset, int len, boolean useBuffer) throws IOException {
        if (len <= this.bufferLength - this.bufferPosition) {
            if (len > 0) {
                System.arraycopy(this.buffer, this.bufferPosition, b, offset, len);
            }
            this.bufferPosition += len;
        } else {
            int available = this.bufferLength - this.bufferPosition;
            if (available > 0) {
                System.arraycopy(this.buffer, this.bufferPosition, b, offset, available);
                offset += available;
                len -= available;
                this.bufferPosition += available;
            }
            if (useBuffer && len < this.bufferSize) {
                this.refill();
                if (this.bufferLength < len) {
                    System.arraycopy(this.buffer, 0, b, offset, this.bufferLength);
                    throw new EOFException("read past EOF: " + this);
                }
                System.arraycopy(this.buffer, 0, b, offset, len);
                this.bufferPosition = len;
            } else {
                long after = this.bufferStart + (long)this.bufferPosition + (long)len;
                if (after > this.length()) {
                    throw new EOFException("read past EOF: " + this);
                }
                this.readInternal(b, offset, len);
                this.bufferStart = after;
                this.bufferPosition = 0;
                this.bufferLength = 0;
            }
        }
    }

    @Override
    public final short readShort() throws IOException {
        if (2 <= this.bufferLength - this.bufferPosition) {
            return (short)((this.buffer[this.bufferPosition++] & 0xFF) << 8 | this.buffer[this.bufferPosition++] & 0xFF);
        }
        return super.readShort();
    }

    @Override
    public final int readInt() throws IOException {
        if (4 <= this.bufferLength - this.bufferPosition) {
            return (this.buffer[this.bufferPosition++] & 0xFF) << 24 | (this.buffer[this.bufferPosition++] & 0xFF) << 16 | (this.buffer[this.bufferPosition++] & 0xFF) << 8 | this.buffer[this.bufferPosition++] & 0xFF;
        }
        return super.readInt();
    }

    @Override
    public final long readLong() throws IOException {
        if (8 <= this.bufferLength - this.bufferPosition) {
            int i1 = (this.buffer[this.bufferPosition++] & 0xFF) << 24 | (this.buffer[this.bufferPosition++] & 0xFF) << 16 | (this.buffer[this.bufferPosition++] & 0xFF) << 8 | this.buffer[this.bufferPosition++] & 0xFF;
            int i2 = (this.buffer[this.bufferPosition++] & 0xFF) << 24 | (this.buffer[this.bufferPosition++] & 0xFF) << 16 | (this.buffer[this.bufferPosition++] & 0xFF) << 8 | this.buffer[this.bufferPosition++] & 0xFF;
            return (long)i1 << 32 | (long)i2 & 0xFFFFFFFFL;
        }
        return super.readLong();
    }

    @Override
    public final int readVInt() throws IOException {
        if (5 <= this.bufferLength - this.bufferPosition) {
            byte b;
            if ((b = this.buffer[this.bufferPosition++]) >= 0) {
                return b;
            }
            int i = b & 0x7F;
            b = this.buffer[this.bufferPosition++];
            i |= (b & 0x7F) << 7;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= (b & 0x7F) << 14;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= (b & 0x7F) << 21;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= (b & 0xF) << 28;
            if ((b & 0xF0) == 0) {
                return i;
            }
            throw new IOException("Invalid vInt detected (too many bits)");
        }
        return super.readVInt();
    }

    @Override
    public final long readVLong() throws IOException {
        if (9 <= this.bufferLength - this.bufferPosition) {
            byte b;
            if ((b = this.buffer[this.bufferPosition++]) >= 0) {
                return b;
            }
            long i = (long)b & 0x7FL;
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 7;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 14;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 21;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 28;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 35;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 42;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 49;
            if (b >= 0) {
                return i;
            }
            b = this.buffer[this.bufferPosition++];
            i |= ((long)b & 0x7FL) << 56;
            if (b >= 0) {
                return i;
            }
            throw new IOException("Invalid vLong detected (negative values disallowed)");
        }
        return super.readVLong();
    }

    private void refill() throws IOException {
        int newLength;
        long start = this.bufferStart + (long)this.bufferPosition;
        long end = start + (long)this.bufferSize;
        if (end > this.length()) {
            end = this.length();
        }
        if ((newLength = (int)(end - start)) <= 0) {
            throw new EOFException("read past EOF: " + this);
        }
        if (this.buffer == null) {
            this.newBuffer(new byte[this.bufferSize]);
            this.seekInternal(this.bufferStart);
        }
        this.readInternal(this.buffer, 0, newLength);
        this.bufferLength = newLength;
        this.bufferStart = start;
        this.bufferPosition = 0;
    }

    protected abstract void readInternal(byte[] var1, int var2, int var3) throws IOException;

    @Override
    public final long getFilePointer() {
        return this.bufferStart + (long)this.bufferPosition;
    }

    @Override
    public final void seek(long pos) throws IOException {
        if (pos >= this.bufferStart && pos < this.bufferStart + (long)this.bufferLength) {
            this.bufferPosition = (int)(pos - this.bufferStart);
        } else {
            this.bufferStart = pos;
            this.bufferPosition = 0;
            this.bufferLength = 0;
            this.seekInternal(pos);
        }
    }

    protected abstract void seekInternal(long var1) throws IOException;

    @Override
    public BufferedIndexInput clone() {
        BufferedIndexInput clone = (BufferedIndexInput)super.clone();
        clone.buffer = null;
        clone.bufferLength = 0;
        clone.bufferPosition = 0;
        clone.bufferStart = this.getFilePointer();
        return clone;
    }

    protected final int flushBuffer(IndexOutput out, long numBytes) throws IOException {
        int toCopy = this.bufferLength - this.bufferPosition;
        if ((long)toCopy > numBytes) {
            toCopy = (int)numBytes;
        }
        if (toCopy > 0) {
            out.writeBytes(this.buffer, this.bufferPosition, toCopy);
            this.bufferPosition += toCopy;
        }
        return toCopy;
    }

    public static int bufferSize(IOContext context) {
        switch (context.context) {
            case MERGE: {
                return 4096;
            }
        }
        return 1024;
    }
}

