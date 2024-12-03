/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.lang.Validate;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class BitPaddingStream
extends FilterInputStream {
    private static final int[] MASK = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, Short.MAX_VALUE, 65535, 131071, 262143, 524287, 1048575, 0x1FFFFF, 0x3FFFFF, 0x7FFFFF, 0xFFFFFF, 0x1FFFFFF, 0x3FFFFFF, 0x7FFFFFF, 0xFFFFFFF, 0x1FFFFFFF, 0x3FFFFFFF, Integer.MAX_VALUE, -1};
    private final int bitsPerSample;
    private final byte[] inputBuffer;
    private final ByteBuffer buffer;
    private final int componentSize;

    BitPaddingStream(InputStream inputStream, int n, int n2, int n3, ByteOrder byteOrder) {
        super((InputStream)Validate.notNull((Object)inputStream, (String)"stream"));
        this.bitsPerSample = n2;
        Validate.notNull((Object)byteOrder, (String)"byteOrder");
        switch (n2) {
            case 2: 
            case 4: 
            case 6: {
                this.componentSize = 1;
                break;
            }
            case 10: 
            case 12: 
            case 14: {
                this.componentSize = 2;
                break;
            }
            case 18: 
            case 20: 
            case 22: 
            case 24: 
            case 26: 
            case 28: 
            case 30: {
                this.componentSize = 4;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported BitsPerSample value: " + n2);
            }
        }
        int n4 = (n * n2 * n3 + 7) / 8;
        this.inputBuffer = new byte[n4];
        int n5 = n * n3 * this.componentSize;
        this.buffer = ByteBuffer.allocate(n5);
        this.buffer.order(byteOrder);
        this.buffer.position(this.buffer.limit());
    }

    @Override
    public int read() throws IOException {
        if (!this.buffer.hasRemaining() && !this.fillBuffer()) {
            return -1;
        }
        return this.buffer.get() & 0xFF;
    }

    private boolean readFully(byte[] byArray) throws IOException {
        int n;
        for (int i = byArray.length; i > 0; i -= n) {
            n = this.in.read(byArray, byArray.length - i, i);
            if (n != -1) continue;
            return false;
        }
        return true;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (!this.buffer.hasRemaining() && !this.fillBuffer()) {
            return -1;
        }
        int n3 = Math.min(n2, this.buffer.remaining());
        this.buffer.get(byArray, n, n3);
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        if (l <= 0L) {
            return 0L;
        }
        if (!this.buffer.hasRemaining() && !this.fillBuffer()) {
            return 0L;
        }
        int n = (int)Math.min(l, (long)this.buffer.remaining());
        this.buffer.position(this.buffer.position() + n);
        return n;
    }

    private boolean fillBuffer() throws IOException {
        if (!this.readFully(this.inputBuffer)) {
            return false;
        }
        this.buffer.clear();
        this.padBits(this.buffer, this.componentSize, this.bitsPerSample, this.inputBuffer);
        this.buffer.rewind();
        return true;
    }

    private void padBits(ByteBuffer byteBuffer, int n, int n2, byte[] byArray) {
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        block5: while (true) {
            int n6 = n5 & MASK[n4];
            while (n4 < n2) {
                if (n3 >= byArray.length) {
                    return;
                }
                n5 = byArray[n3++] & 0xFF;
                n6 = n6 << 8 | n5;
                n4 += 8;
            }
            n6 = n6 >> (n4 -= n2) & MASK[n2];
            switch (n) {
                case 1: {
                    byteBuffer.put((byte)n6);
                    continue block5;
                }
                case 2: {
                    byteBuffer.putShort((short)n6);
                    continue block5;
                }
                case 4: {
                    byteBuffer.putInt(n6);
                    continue block5;
                }
            }
            break;
        }
        throw new AssertionError();
    }
}

