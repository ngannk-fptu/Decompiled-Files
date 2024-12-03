/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

public class LittleEndianDataInputStream
extends FilterInputStream
implements DataInput {
    public LittleEndianDataInputStream(InputStream inputStream) {
        super((InputStream)Validate.notNull((Object)inputStream, (String)"stream"));
    }

    @Override
    public boolean readBoolean() throws IOException {
        int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n != 0;
    }

    @Override
    public byte readByte() throws IOException {
        int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        return (byte)n;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n;
    }

    @Override
    public short readShort() throws IOException {
        int n = this.in.read();
        int n2 = this.in.read();
        if (n2 < 0) {
            throw new EOFException();
        }
        return (short)(n2 << 24 >>> 16 | n << 24 >>> 24);
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int n = this.in.read();
        int n2 = this.in.read();
        if (n2 < 0) {
            throw new EOFException();
        }
        return (n2 << 8) + n;
    }

    @Override
    public char readChar() throws IOException {
        int n = this.in.read();
        int n2 = this.in.read();
        if (n2 < 0) {
            throw new EOFException();
        }
        return (char)(n2 << 24 >>> 16 | n << 24 >>> 24);
    }

    @Override
    public int readInt() throws IOException {
        int n = this.in.read();
        int n2 = this.in.read();
        int n3 = this.in.read();
        int n4 = this.in.read();
        if (n4 < 0) {
            throw new EOFException();
        }
        return n4 << 24 | n3 << 24 >>> 8 | n2 << 24 >>> 16 | n << 24 >>> 24;
    }

    @Override
    public long readLong() throws IOException {
        long l = this.in.read();
        long l2 = this.in.read();
        long l3 = this.in.read();
        long l4 = this.in.read();
        long l5 = this.in.read();
        long l6 = this.in.read();
        long l7 = this.in.read();
        long l8 = this.in.read();
        if (l8 < 0L) {
            throw new EOFException();
        }
        return l8 << 56 | l7 << 56 >>> 8 | l6 << 56 >>> 16 | l5 << 56 >>> 24 | l4 << 56 >>> 32 | l3 << 56 >>> 40 | l2 << 56 >>> 48 | l << 56 >>> 56;
    }

    @Override
    public String readUTF() throws IOException {
        int n = this.in.read();
        int n2 = this.in.read();
        if (n2 < 0) {
            throw new EOFException();
        }
        int n3 = (n << 8) + n2;
        char[] cArray = new char[n3];
        int n4 = 0;
        int n5 = 0;
        while (n4 < n3) {
            int n6;
            int n7 = this.readUnsignedByte();
            int n8 = n7 >> 4;
            if (n8 < 8) {
                ++n4;
                cArray[n5++] = (char)n7;
                continue;
            }
            if (n8 == 12 || n8 == 13) {
                if ((n4 += 2) > n3) {
                    throw new UTFDataFormatException();
                }
                n6 = this.readUnsignedByte();
                if ((n6 & 0xC0) != 128) {
                    throw new UTFDataFormatException();
                }
                cArray[n5++] = (char)((n7 & 0x1F) << 6 | n6 & 0x3F);
                continue;
            }
            if (n8 == 14) {
                if ((n4 += 3) > n3) {
                    throw new UTFDataFormatException();
                }
                n6 = this.readUnsignedByte();
                int n9 = this.readUnsignedByte();
                if ((n6 & 0xC0) != 128 || (n9 & 0xC0) != 128) {
                    throw new UTFDataFormatException();
                }
                cArray[n5++] = (char)((n7 & 0xF) << 12 | (n6 & 0x3F) << 6 | n9 & 0x3F);
                continue;
            }
            throw new UTFDataFormatException();
        }
        return new String(cArray, 0, n5);
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        int n2;
        int n3;
        for (n2 = 0; n2 < n && (n3 = (int)this.in.skip(n - n2)) > 0; n2 += n3) {
        }
        return n2;
    }

    @Override
    public final void readFully(byte[] byArray) throws IOException {
        this.readFully(byArray, 0, byArray.length);
    }

    @Override
    public final void readFully(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        if (n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < n2; i += n3) {
            n3 = this.in.read(byArray, n + i, n2 - i);
            if (n3 >= 0) continue;
            throw new EOFException();
        }
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(this.in);
        return dataInputStream.readLine();
    }
}

