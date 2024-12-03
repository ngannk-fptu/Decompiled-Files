/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.FileUtil;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;
import java.nio.channels.FileChannel;

public class LittleEndianRandomAccessFile
implements DataInput,
DataOutput {
    private RandomAccessFile file;

    public LittleEndianRandomAccessFile(String string, String string2) throws FileNotFoundException {
        this(FileUtil.resolve(string), string2);
    }

    public LittleEndianRandomAccessFile(File file, String string) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, string);
    }

    public void close() throws IOException {
        this.file.close();
    }

    public FileChannel getChannel() {
        return this.file.getChannel();
    }

    public FileDescriptor getFD() throws IOException {
        return this.file.getFD();
    }

    public long getFilePointer() throws IOException {
        return this.file.getFilePointer();
    }

    public long length() throws IOException {
        return this.file.length();
    }

    public int read() throws IOException {
        return this.file.read();
    }

    public int read(byte[] byArray) throws IOException {
        return this.file.read(byArray);
    }

    public int read(byte[] byArray, int n, int n2) throws IOException {
        return this.file.read(byArray, n, n2);
    }

    @Override
    public void readFully(byte[] byArray) throws IOException {
        this.file.readFully(byArray);
    }

    @Override
    public void readFully(byte[] byArray, int n, int n2) throws IOException {
        this.file.readFully(byArray, n, n2);
    }

    @Override
    public String readLine() throws IOException {
        return this.file.readLine();
    }

    @Override
    public boolean readBoolean() throws IOException {
        int n = this.file.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n != 0;
    }

    @Override
    public byte readByte() throws IOException {
        int n = this.file.read();
        if (n < 0) {
            throw new EOFException();
        }
        return (byte)n;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int n = this.file.read();
        if (n < 0) {
            throw new EOFException();
        }
        return n;
    }

    @Override
    public short readShort() throws IOException {
        int n = this.file.read();
        int n2 = this.file.read();
        if (n2 < 0) {
            throw new EOFException();
        }
        return (short)((n2 << 24 >>> 16) + (n << 24) >>> 24);
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int n = this.file.read();
        int n2 = this.file.read();
        if (n2 < 0) {
            throw new EOFException();
        }
        return (n2 << 8) + n;
    }

    @Override
    public char readChar() throws IOException {
        int n = this.file.read();
        int n2 = this.file.read();
        if (n2 < 0) {
            throw new EOFException();
        }
        return (char)((n2 << 24 >>> 16) + (n << 24 >>> 24));
    }

    @Override
    public int readInt() throws IOException {
        int n = this.file.read();
        int n2 = this.file.read();
        int n3 = this.file.read();
        int n4 = this.file.read();
        if (n4 < 0) {
            throw new EOFException();
        }
        return (n4 << 24) + (n3 << 24 >>> 8) + (n2 << 24 >>> 16) + (n << 24 >>> 24);
    }

    @Override
    public long readLong() throws IOException {
        long l = this.file.read();
        long l2 = this.file.read();
        long l3 = this.file.read();
        long l4 = this.file.read();
        long l5 = this.file.read();
        long l6 = this.file.read();
        long l7 = this.file.read();
        long l8 = this.file.read();
        if (l8 < 0L) {
            throw new EOFException();
        }
        return (l8 << 56) + (l7 << 56 >>> 8) + (l6 << 56 >>> 16) + (l5 << 56 >>> 24) + (l4 << 56 >>> 32) + (l3 << 56 >>> 40) + (l2 << 56 >>> 48) + (l << 56 >>> 56);
    }

    @Override
    public String readUTF() throws IOException {
        int n = this.file.read();
        int n2 = this.file.read();
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

    public void seek(long l) throws IOException {
        this.file.seek(l);
    }

    public void setLength(long l) throws IOException {
        this.file.setLength(l);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.file.skipBytes(n);
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        this.file.write(byArray);
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.file.write(byArray, n, n2);
    }

    @Override
    public void write(int n) throws IOException {
        this.file.write(n);
    }

    @Override
    public void writeBoolean(boolean bl) throws IOException {
        if (bl) {
            this.write(1);
        } else {
            this.write(0);
        }
    }

    @Override
    public void writeByte(int n) throws IOException {
        this.file.write(n);
    }

    @Override
    public void writeShort(int n) throws IOException {
        this.file.write(n & 0xFF);
        this.file.write(n >>> 8 & 0xFF);
    }

    @Override
    public void writeChar(int n) throws IOException {
        this.file.write(n & 0xFF);
        this.file.write(n >>> 8 & 0xFF);
    }

    @Override
    public void writeInt(int n) throws IOException {
        this.file.write(n & 0xFF);
        this.file.write(n >>> 8 & 0xFF);
        this.file.write(n >>> 16 & 0xFF);
        this.file.write(n >>> 24 & 0xFF);
    }

    @Override
    public void writeLong(long l) throws IOException {
        this.file.write((int)l & 0xFF);
        this.file.write((int)(l >>> 8) & 0xFF);
        this.file.write((int)(l >>> 16) & 0xFF);
        this.file.write((int)(l >>> 24) & 0xFF);
        this.file.write((int)(l >>> 32) & 0xFF);
        this.file.write((int)(l >>> 40) & 0xFF);
        this.file.write((int)(l >>> 48) & 0xFF);
        this.file.write((int)(l >>> 56) & 0xFF);
    }

    @Override
    public final void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    @Override
    public final void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    @Override
    public void writeBytes(String string) throws IOException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            this.file.write((byte)string.charAt(i));
        }
    }

    @Override
    public void writeChars(String string) throws IOException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            this.file.write(c & 0xFF);
            this.file.write(c >>> 8 & 0xFF);
        }
    }

    @Override
    public void writeUTF(String string) throws IOException {
        char c;
        int n;
        int n2 = string.length();
        int n3 = 0;
        for (n = 0; n < n2; ++n) {
            c = string.charAt(n);
            if (c >= '\u0001' && c <= '\u007f') {
                ++n3;
                continue;
            }
            if (c > '\u07ff') {
                n3 += 3;
                continue;
            }
            n3 += 2;
        }
        if (n3 > 65535) {
            throw new UTFDataFormatException();
        }
        this.file.write(n3 >>> 8 & 0xFF);
        this.file.write(n3 & 0xFF);
        for (n = 0; n < n2; ++n) {
            c = string.charAt(n);
            if (c >= '\u0001' && c <= '\u007f') {
                this.file.write(c);
                continue;
            }
            if (c > '\u07ff') {
                this.file.write(0xE0 | c >> 12 & 0xF);
                this.file.write(0x80 | c >> 6 & 0x3F);
                this.file.write(0x80 | c & 0x3F);
                continue;
            }
            this.file.write(0xC0 | c >> 6 & 0x1F);
            this.file.write(0x80 | c & 0x3F);
        }
    }
}

