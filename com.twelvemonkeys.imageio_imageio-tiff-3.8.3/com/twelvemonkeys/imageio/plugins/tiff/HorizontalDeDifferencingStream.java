/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.plugins.tiff.HorizontalDifferencingStream;
import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

final class HorizontalDeDifferencingStream
extends InputStream {
    private final int columns;
    private final int samplesPerPixel;
    private final int bitsPerSample;
    private final ReadableByteChannel channel;
    private final ByteBuffer buffer;

    public HorizontalDeDifferencingStream(InputStream inputStream, int n, int n2, int n3, ByteOrder byteOrder) {
        this.columns = (Integer)Validate.isTrue((n > 0 ? 1 : 0) != 0, (Object)n, (String)"width must be greater than 0");
        this.samplesPerPixel = (Integer)Validate.isTrue((n3 >= 8 || n2 == 1 ? 1 : 0) != 0, (Object)n2, (String)"Unsupported samples per pixel for < 8 bit samples: %s");
        this.bitsPerSample = (Integer)Validate.isTrue((boolean)HorizontalDifferencingStream.isValidBPS(n3), (Object)n3, (String)"Unsupported bits per sample value: %s");
        this.channel = Channels.newChannel((InputStream)Validate.notNull((Object)inputStream, (String)"stream"));
        this.buffer = ByteBuffer.allocate((n * n2 * n3 + 7) / 8).order(byteOrder);
        this.buffer.flip();
    }

    private boolean fetch() throws IOException {
        this.buffer.clear();
        while (this.channel.read(this.buffer) > 0) {
        }
        if (this.buffer.position() > 0) {
            if (this.buffer.hasRemaining()) {
                throw new EOFException("Unexpected end of stream");
            }
            this.decodeRow();
            this.buffer.flip();
            return true;
        }
        this.buffer.position(this.buffer.capacity());
        return false;
    }

    private void decodeRow() {
        int n = 0;
        byte[] byArray = this.buffer.array();
        switch (this.bitsPerSample) {
            case 1: {
                for (int i = 0; i < (this.columns + 7) / 8; ++i) {
                    byte by = byArray[i];
                    byte by2 = (byte)((n += by >> 7 & 1) << 7 & 0x80);
                    by2 = (byte)(by2 | (byte)((n += by >> 6 & 1) << 6 & 0x40));
                    by2 = (byte)(by2 | (byte)((n += by >> 5 & 1) << 5 & 0x20));
                    by2 = (byte)(by2 | (byte)((n += by >> 4 & 1) << 4 & 0x10));
                    by2 = (byte)(by2 | (byte)((n += by >> 3 & 1) << 3 & 8));
                    by2 = (byte)(by2 | (byte)((n += by >> 2 & 1) << 2 & 4));
                    by2 = (byte)(by2 | (byte)((n += by >> 1 & 1) << 1 & 2));
                    byArray[i] = (byte)(by2 | (n += by & 1) & 1);
                }
                break;
            }
            case 2: {
                for (int i = 0; i < (this.columns + 3) / 4; ++i) {
                    byte by = byArray[i];
                    byte by3 = (byte)((n += by >> 6 & 3) << 6 & 0xC0);
                    by3 = (byte)(by3 | (byte)((n += by >> 4 & 3) << 4 & 0x30));
                    by3 = (byte)(by3 | (byte)((n += by >> 2 & 3) << 2 & 0xC));
                    byArray[i] = (byte)(by3 | (n += by & 3) & 3);
                }
                break;
            }
            case 4: {
                for (int i = 0; i < (this.columns + 1) / 2; ++i) {
                    byte by = byArray[i];
                    byte by4 = (byte)((n += by >> 4 & 0xF) << 4 & 0xF0);
                    byArray[i] = (byte)(by4 | (n += by & 0xF) & 0xF);
                }
                break;
            }
            case 8: {
                for (int i = 1; i < this.columns; ++i) {
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n2 = i * this.samplesPerPixel + j;
                        byArray[n2] = (byte)(byArray[n2 - this.samplesPerPixel] + byArray[n2]);
                    }
                }
                break;
            }
            case 16: {
                for (int i = 1; i < this.columns; ++i) {
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n3 = i * this.samplesPerPixel + j;
                        this.buffer.putShort(2 * n3, (short)(this.buffer.getShort(2 * (n3 - this.samplesPerPixel)) + this.buffer.getShort(2 * n3)));
                    }
                }
                break;
            }
            case 32: {
                for (int i = 1; i < this.columns; ++i) {
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n4 = i * this.samplesPerPixel + j;
                        this.buffer.putInt(4 * n4, this.buffer.getInt(4 * (n4 - this.samplesPerPixel)) + this.buffer.getInt(4 * n4));
                    }
                }
                break;
            }
            case 64: {
                for (int i = 1; i < this.columns; ++i) {
                    for (int j = 0; j < this.samplesPerPixel; ++j) {
                        int n5 = i * this.samplesPerPixel + j;
                        this.buffer.putLong(8 * n5, this.buffer.getLong(8 * (n5 - this.samplesPerPixel)) + this.buffer.getLong(8 * n5));
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
    public int read() throws IOException {
        if (!this.buffer.hasRemaining() && !this.fetch()) {
            return -1;
        }
        return this.buffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (!this.buffer.hasRemaining() && !this.fetch()) {
            return -1;
        }
        int n3 = Math.min(this.buffer.remaining(), n2);
        this.buffer.get(byArray, n, n3);
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        if (l < 0L) {
            return 0L;
        }
        if (!this.buffer.hasRemaining() && !this.fetch()) {
            return 0L;
        }
        int n = (int)Math.min((long)this.buffer.remaining(), l);
        this.buffer.position(this.buffer.position() + n);
        return n;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        }
        finally {
            if (this.channel.isOpen()) {
                this.channel.close();
            }
        }
    }
}

