/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

public class LittleEndianDataOutputStream
extends FilterOutputStream
implements DataOutput {
    protected int bytesWritten;

    public LittleEndianDataOutputStream(OutputStream outputStream) {
        super((OutputStream)Validate.notNull((Object)outputStream, (String)"stream"));
    }

    @Override
    public synchronized void write(int n) throws IOException {
        this.out.write(n);
        ++this.bytesWritten;
    }

    @Override
    public synchronized void write(byte[] byArray, int n, int n2) throws IOException {
        this.out.write(byArray, n, n2);
        this.bytesWritten += n2;
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
        this.out.write(n);
        ++this.bytesWritten;
    }

    @Override
    public void writeShort(int n) throws IOException {
        this.out.write(n & 0xFF);
        this.out.write(n >>> 8 & 0xFF);
        this.bytesWritten += 2;
    }

    @Override
    public void writeChar(int n) throws IOException {
        this.out.write(n & 0xFF);
        this.out.write(n >>> 8 & 0xFF);
        this.bytesWritten += 2;
    }

    @Override
    public void writeInt(int n) throws IOException {
        this.out.write(n & 0xFF);
        this.out.write(n >>> 8 & 0xFF);
        this.out.write(n >>> 16 & 0xFF);
        this.out.write(n >>> 24 & 0xFF);
        this.bytesWritten += 4;
    }

    @Override
    public void writeLong(long l) throws IOException {
        this.out.write((int)l & 0xFF);
        this.out.write((int)(l >>> 8) & 0xFF);
        this.out.write((int)(l >>> 16) & 0xFF);
        this.out.write((int)(l >>> 24) & 0xFF);
        this.out.write((int)(l >>> 32) & 0xFF);
        this.out.write((int)(l >>> 40) & 0xFF);
        this.out.write((int)(l >>> 48) & 0xFF);
        this.out.write((int)(l >>> 56) & 0xFF);
        this.bytesWritten += 8;
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
            this.out.write((byte)string.charAt(i));
        }
        this.bytesWritten += n;
    }

    @Override
    public void writeChars(String string) throws IOException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            this.out.write(c & 0xFF);
            this.out.write(c >>> 8 & 0xFF);
        }
        this.bytesWritten += n * 2;
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
        this.out.write(n3 >>> 8 & 0xFF);
        this.out.write(n3 & 0xFF);
        for (n = 0; n < n2; ++n) {
            c = string.charAt(n);
            if (c >= '\u0001' && c <= '\u007f') {
                this.out.write(c);
                continue;
            }
            if (c > '\u07ff') {
                this.out.write(0xE0 | c >> 12 & 0xF);
                this.out.write(0x80 | c >> 6 & 0x3F);
                this.out.write(0x80 | c & 0x3F);
                this.bytesWritten += 2;
                continue;
            }
            this.out.write(0xC0 | c >> 6 & 0x1F);
            this.out.write(0x80 | c & 0x3F);
            ++this.bytesWritten;
        }
        this.bytesWritten += n2 + 2;
    }

    public int size() {
        return this.bytesWritten;
    }
}

