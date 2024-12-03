/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlgraphics.image.codec.util.FileCacheSeekableStream;
import org.apache.xmlgraphics.image.codec.util.ForwardSeekableStream;
import org.apache.xmlgraphics.image.codec.util.MemoryCacheSeekableStream;

public abstract class SeekableStream
extends InputStream
implements DataInput {
    protected long markPos = -1L;
    private byte[] ruileBuf = new byte[4];

    public static SeekableStream wrapInputStream(InputStream is, boolean canSeekBackwards) {
        SeekableStream stream = null;
        if (canSeekBackwards) {
            try {
                stream = new FileCacheSeekableStream(is);
            }
            catch (Exception e) {
                stream = new MemoryCacheSeekableStream(is);
            }
        } else {
            stream = new ForwardSeekableStream(is);
        }
        return stream;
    }

    @Override
    public abstract int read() throws IOException;

    @Override
    public abstract int read(byte[] var1, int var2, int var3) throws IOException;

    @Override
    public synchronized void mark(int readLimit) {
        try {
            this.markPos = this.getFilePointer();
        }
        catch (IOException e) {
            this.markPos = -1L;
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (this.markPos != -1L) {
            this.seek(this.markPos);
        }
    }

    @Override
    public boolean markSupported() {
        return this.canSeekBackwards();
    }

    public boolean canSeekBackwards() {
        return false;
    }

    public abstract long getFilePointer() throws IOException;

    public abstract void seek(long var1) throws IOException;

    @Override
    public final void readFully(byte[] b) throws IOException {
        this.readFully(b, 0, b.length);
    }

    @Override
    public final void readFully(byte[] b, int off, int len) throws IOException {
        int count;
        int n = 0;
        do {
            if ((count = this.read(b, off + n, len - n)) >= 0) continue;
            throw new EOFException();
        } while ((n += count) < len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        return (int)this.skip(n);
    }

    @Override
    public final boolean readBoolean() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }

    @Override
    public final byte readByte() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte)ch;
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    @Override
    public final short readShort() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (short)((ch1 << 8) + (ch2 << 0));
    }

    public final short readShortLE() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (short)((ch2 << 8) + (ch1 << 0));
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + (ch2 << 0);
    }

    public final int readUnsignedShortLE() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }

    @Override
    public final char readChar() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (char)((ch1 << 8) + (ch2 << 0));
    }

    public final char readCharLE() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (char)((ch2 << 8) + (ch1 << 0));
    }

    @Override
    public final int readInt() throws IOException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read()) | (ch3 = this.read()) | (ch4 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    public final int readIntLE() throws IOException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read()) | (ch3 = this.read()) | (ch4 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    public final long readUnsignedInt() throws IOException {
        long ch4;
        long ch3;
        long ch2;
        long ch1 = this.read();
        if ((ch1 | (ch2 = (long)this.read()) | (ch3 = (long)this.read()) | (ch4 = (long)this.read())) < 0L) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    public final long readUnsignedIntLE() throws IOException {
        this.readFully(this.ruileBuf);
        long ch1 = this.ruileBuf[0] & 0xFF;
        long ch2 = this.ruileBuf[1] & 0xFF;
        long ch3 = this.ruileBuf[2] & 0xFF;
        long ch4 = this.ruileBuf[3] & 0xFF;
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    @Override
    public final long readLong() throws IOException {
        return ((long)this.readInt() << 32) + ((long)this.readInt() & 0xFFFFFFFFL);
    }

    public final long readLongLE() throws IOException {
        int i1 = this.readIntLE();
        int i2 = this.readIntLE();
        return ((long)i2 << 32) + ((long)i1 & 0xFFFFFFFFL);
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    public final float readFloatLE() throws IOException {
        return Float.intBitsToFloat(this.readIntLE());
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    public final double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(this.readLongLE());
    }

    @Override
    public final String readLine() throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;
        block4: while (!eol) {
            c = this.read();
            switch (c) {
                case -1: 
                case 10: {
                    eol = true;
                    continue block4;
                }
                case 13: {
                    eol = true;
                    long cur = this.getFilePointer();
                    if (this.read() == 10) continue block4;
                    this.seek(cur);
                    continue block4;
                }
            }
            input.append((char)c);
        }
        if (c == -1 && input.length() == 0) {
            return null;
        }
        return input.toString();
    }

    @Override
    public final String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}

