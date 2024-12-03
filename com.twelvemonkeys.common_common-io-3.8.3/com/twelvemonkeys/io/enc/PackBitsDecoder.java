/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import com.twelvemonkeys.io.enc.DecodeException;
import com.twelvemonkeys.io.enc.Decoder;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class PackBitsDecoder
implements Decoder {
    private final boolean disableNoOp;
    private final byte[] sample;
    private boolean reachedEOF;

    public PackBitsDecoder() {
        this(1, false);
    }

    public PackBitsDecoder(boolean bl) {
        this(1, bl);
    }

    public PackBitsDecoder(int n, boolean bl) {
        this.sample = new byte[n];
        this.disableNoOp = bl;
    }

    @Override
    public int decode(InputStream inputStream, ByteBuffer byteBuffer) throws IOException {
        if (this.reachedEOF) {
            return -1;
        }
        int n = inputStream.read();
        if (n < 0) {
            this.reachedEOF = true;
            return 0;
        }
        byte by = (byte)n;
        try {
            if (by >= 0) {
                PackBitsDecoder.readFully(inputStream, byteBuffer, this.sample.length * (by + 1));
            } else if (this.disableNoOp || by != -128) {
                int n2;
                for (n2 = 0; n2 < this.sample.length; ++n2) {
                    this.sample[n2] = PackBitsDecoder.readByte(inputStream);
                }
                for (n2 = -by + 1; n2 > 0; --n2) {
                    byteBuffer.put(this.sample);
                }
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            throw new DecodeException("Error in PackBits decompression, data seems corrupt", indexOutOfBoundsException);
        }
        return byteBuffer.position();
    }

    static byte readByte(InputStream inputStream) throws IOException {
        int n = inputStream.read();
        if (n < 0) {
            throw new EOFException("Unexpected end of PackBits stream");
        }
        return (byte)n;
    }

    static void readFully(InputStream inputStream, ByteBuffer byteBuffer, int n) throws IOException {
        int n2;
        int n3;
        if (n < 0) {
            throw new IndexOutOfBoundsException(String.format("Negative length: %d", n));
        }
        for (n2 = 0; n2 < n; n2 += n3) {
            n3 = inputStream.read(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position() + n2, n - n2);
            if (n3 >= 0) continue;
            throw new EOFException("Unexpected end of PackBits stream");
        }
        byteBuffer.position(byteBuffer.position() + n2);
    }
}

