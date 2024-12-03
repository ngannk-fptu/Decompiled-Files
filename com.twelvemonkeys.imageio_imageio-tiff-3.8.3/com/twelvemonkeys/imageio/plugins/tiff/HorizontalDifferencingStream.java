/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

final class HorizontalDifferencingStream
extends OutputStream {
    private final int columns;
    private final int samplesPerPixel;
    private final int bitsPerSample;
    private final WritableByteChannel channel;
    private final ByteBuffer buffer;

    public HorizontalDifferencingStream(OutputStream outputStream, int n, int n2, int n3, ByteOrder byteOrder) {
        this.columns = (Integer)Validate.isTrue((n > 0 ? 1 : 0) != 0, (Object)n, (String)"width must be greater than 0");
        this.samplesPerPixel = (Integer)Validate.isTrue((n3 >= 8 || n2 == 1 ? 1 : 0) != 0, (Object)n2, (String)"Unsupported samples per pixel for < 8 bit samples: %s");
        this.bitsPerSample = (Integer)Validate.isTrue((boolean)HorizontalDifferencingStream.isValidBPS(n3), (Object)n3, (String)"Unsupported bits per sample value: %s");
        this.channel = Channels.newChannel((OutputStream)Validate.notNull((Object)outputStream, (String)"stream"));
        this.buffer = ByteBuffer.allocate((n * n2 * n3 + 7) / 8).order(byteOrder);
    }

    static boolean isValidBPS(int n) {
        switch (n) {
            case 1: 
            case 2: 
            case 4: 
            case 8: 
            case 16: 
            case 32: 
            case 64: {
                return true;
            }
        }
        return false;
    }

    private boolean flushBuffer() throws IOException {
        if (this.buffer.position() == 0) {
            return false;
        }
        this.encodeRow();
        this.buffer.flip();
        this.channel.write(this.buffer);
        this.buffer.clear();
        return true;
    }

    private void encodeRow() throws EOFException {
        int n = 0;
        byte[] byArray = this.buffer.array();
        switch (this.bitsPerSample) {
            case 1: {
                byte by;
                byte by2;
                for (int i = (this.columns + 7) / 8 - 1; i > 0; --i) {
                    by2 = byArray[i];
                    int n2 = byArray[i - 1] & 1;
                    by = (byte)(((by2 & 0x80) >> 7) - n2 << 7);
                    n = ((by2 & 0x40) >> 6) - ((by2 & 0x80) >> 7);
                    by = (byte)(by | n << 6 & 0x40);
                    n = ((by2 & 0x20) >> 5) - ((by2 & 0x40) >> 6);
                    by = (byte)(by | n << 5 & 0x20);
                    n = ((by2 & 0x10) >> 4) - ((by2 & 0x20) >> 5);
                    by = (byte)(by | n << 4 & 0x10);
                    n = ((by2 & 8) >> 3) - ((by2 & 0x10) >> 4);
                    by = (byte)(by | n << 3 & 8);
                    n = ((by2 & 4) >> 2) - ((by2 & 8) >> 3);
                    by = (byte)(by | n << 2 & 4);
                    n = ((by2 & 2) >> 1) - ((by2 & 4) >> 2);
                    by = (byte)(by | n << 1 & 2);
                    n = (by2 & 1) - ((by2 & 2) >> 1);
                    byArray[i] = (byte)(by & 0xFE | n & 1);
                }
                by2 = byArray[0];
                by = (byte)(by2 & 0x80);
                n = ((by2 & 0x40) >> 6) - ((by2 & 0x80) >> 7);
                by = (byte)(by | n << 6 & 0x40);
                n = ((by2 & 0x20) >> 5) - ((by2 & 0x40) >> 6);
                by = (byte)(by | n << 5 & 0x20);
                n = ((by2 & 0x10) >> 4) - ((by2 & 0x20) >> 5);
                by = (byte)(by | n << 4 & 0x10);
                n = ((by2 & 8) >> 3) - ((by2 & 0x10) >> 4);
                by = (byte)(by | n << 3 & 8);
                n = ((by2 & 4) >> 2) - ((by2 & 8) >> 3);
                by = (byte)(by | n << 2 & 4);
                n = ((by2 & 2) >> 1) - ((by2 & 4) >> 2);
                by = (byte)(by | n << 1 & 2);
                n = (by2 & 1) - ((by2 & 2) >> 1);
                byArray[0] = (byte)(by & 0xFE | n & 1);
                break;
            }
            case 2: {
                byte by;
                byte by3;
                for (int i = (this.columns + 3) / 4 - 1; i > 0; --i) {
                    by3 = byArray[i];
                    int n3 = byArray[i - 1] & 3;
                    by = (byte)(((by3 & 0xC0) >> 6) - n3 << 6);
                    n = ((by3 & 0x30) >> 4) - ((by3 & 0xC0) >> 6);
                    by = (byte)(by | n << 4 & 0x30);
                    n = ((by3 & 0xC) >> 2) - ((by3 & 0x30) >> 4);
                    by = (byte)(by | n << 2 & 0xC);
                    n = (by3 & 3) - ((by3 & 0xC) >> 2);
                    byArray[i] = (byte)(by & 0xFC | n & 3);
                }
                by3 = byArray[0];
                by = (byte)(by3 & 0xC0);
                n = ((by3 & 0x30) >> 4) - ((by3 & 0xC0) >> 6);
                by = (byte)(by | n << 4 & 0x30);
                n = ((by3 & 0xC) >> 2) - ((by3 & 0x30) >> 4);
                by = (byte)(by | n << 2 & 0xC);
                n = (by3 & 3) - ((by3 & 0xC) >> 2);
                byArray[0] = (byte)(by & 0xFC | n & 3);
                break;
            }
            case 4: {
                byte by;
                for (int i = (this.columns + 1) / 2 - 1; i > 0; --i) {
                    by = byArray[i];
                    int n4 = byArray[i - 1] & 0xF;
                    byte by4 = (byte)(((by & 0xF0) >> 4) - n4 << 4);
                    n = (by & 0xF) - ((by & 0xF0) >> 4);
                    byArray[i] = (byte)(by4 & 0xF0 | n & 0xF);
                }
                by = byArray[0];
                n = (by & 0xF) - ((by & 0xF0) >> 4);
                byArray[0] = (byte)(by & 0xF0 | n & 0xF);
                break;
            }
            case 8: {
                for (int i = this.columns - 1; i > 0; --i) {
                    int n5 = i * this.samplesPerPixel;
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n6 = n5 + j;
                        byArray[n6] = (byte)(byArray[n6] - byArray[n6 - this.samplesPerPixel]);
                    }
                }
                break;
            }
            case 16: {
                for (int i = this.columns - 1; i > 0; --i) {
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n7 = i * this.samplesPerPixel + j;
                        this.buffer.putShort(2 * n7, (short)(this.buffer.getShort(2 * n7) - this.buffer.getShort(2 * (n7 - this.samplesPerPixel))));
                    }
                }
                break;
            }
            case 32: {
                for (int i = this.columns - 1; i > 0; --i) {
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n8 = i * this.samplesPerPixel + j;
                        this.buffer.putInt(4 * n8, this.buffer.getInt(4 * n8) - this.buffer.getInt(4 * (n8 - this.samplesPerPixel)));
                    }
                }
                break;
            }
            case 64: {
                for (int i = this.columns - 1; i > 0; --i) {
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n9 = i * this.samplesPerPixel + j;
                        this.buffer.putLong(8 * n9, this.buffer.getLong(8 * n9) - this.buffer.getLong(8 * (n9 - this.samplesPerPixel)));
                    }
                }
                break;
            }
            default: {
                throw new AssertionError((Object)String.format("Unsupported bits per sample value: %d", this.bitsPerSample));
            }
        }
    }

    @Override
    public void write(int n) throws IOException {
        this.buffer.put((byte)n);
        if (!this.buffer.hasRemaining()) {
            this.flushBuffer();
        }
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        while (n2 > 0) {
            int n3 = Math.min(n2, this.buffer.remaining());
            this.buffer.put(byArray, n, n3);
            n += n3;
            n2 -= n3;
            if (this.buffer.hasRemaining()) continue;
            this.flushBuffer();
        }
    }

    @Override
    public void flush() throws IOException {
        this.flushBuffer();
    }

    @Override
    public void close() throws IOException {
        try {
            this.flushBuffer();
            super.close();
        }
        finally {
            if (this.channel.isOpen()) {
                this.channel.close();
            }
        }
    }
}

