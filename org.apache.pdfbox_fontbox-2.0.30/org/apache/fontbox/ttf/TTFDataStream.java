/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.fontbox.util.Charsets;

abstract class TTFDataStream
implements Closeable {
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

    TTFDataStream() {
    }

    public float read32Fixed() throws IOException {
        float retval = this.readSignedShort();
        return retval += (float)this.readUnsignedShort() / 65536.0f;
    }

    public String readString(int length) throws IOException {
        return this.readString(length, Charsets.ISO_8859_1);
    }

    public String readString(int length, String charset) throws IOException {
        byte[] buffer = this.read(length);
        return new String(buffer, charset);
    }

    public String readString(int length, Charset charset) throws IOException {
        byte[] buffer = this.read(length);
        return new String(buffer, charset);
    }

    public abstract int read() throws IOException;

    public abstract long readLong() throws IOException;

    public int readSignedByte() throws IOException {
        int signedByte = this.read();
        return signedByte <= 127 ? signedByte : signedByte - 256;
    }

    public int readUnsignedByte() throws IOException {
        int unsignedByte = this.read();
        if (unsignedByte == -1) {
            throw new EOFException("premature EOF");
        }
        return unsignedByte;
    }

    public long readUnsignedInt() throws IOException {
        long byte1 = this.read();
        long byte2 = this.read();
        long byte3 = this.read();
        long byte4 = this.read();
        if (byte4 < 0L) {
            throw new EOFException();
        }
        return (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + byte4;
    }

    public abstract int readUnsignedShort() throws IOException;

    public int[] readUnsignedByteArray(int length) throws IOException {
        int[] array = new int[length];
        for (int i = 0; i < length; ++i) {
            array[i] = this.read();
        }
        return array;
    }

    public int[] readUnsignedShortArray(int length) throws IOException {
        int[] array = new int[length];
        for (int i = 0; i < length; ++i) {
            array[i] = this.readUnsignedShort();
        }
        return array;
    }

    public abstract short readSignedShort() throws IOException;

    public Calendar readInternationalDate() throws IOException {
        long secondsSince1904 = this.readLong();
        Calendar cal = Calendar.getInstance((TimeZone)TIMEZONE_UTC.clone());
        cal.set(1904, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        long millisFor1904 = cal.getTimeInMillis();
        cal.setTimeInMillis(millisFor1904 += secondsSince1904 * 1000L);
        return cal;
    }

    public String readTag() throws IOException {
        return new String(this.read(4), Charsets.US_ASCII);
    }

    public abstract void seek(long var1) throws IOException;

    public byte[] read(int numberOfBytes) throws IOException {
        int totalAmountRead;
        byte[] data = new byte[numberOfBytes];
        int amountRead = 0;
        for (totalAmountRead = 0; totalAmountRead < numberOfBytes && (amountRead = this.read(data, totalAmountRead, numberOfBytes - totalAmountRead)) != -1; totalAmountRead += amountRead) {
        }
        if (totalAmountRead == numberOfBytes) {
            return data;
        }
        throw new IOException("Unexpected end of TTF stream reached");
    }

    public abstract int read(byte[] var1, int var2, int var3) throws IOException;

    public abstract long getCurrentPosition() throws IOException;

    public abstract InputStream getOriginalData() throws IOException;

    public abstract long getOriginalDataSize();
}

