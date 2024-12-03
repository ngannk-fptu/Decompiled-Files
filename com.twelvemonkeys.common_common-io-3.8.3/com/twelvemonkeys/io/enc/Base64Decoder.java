/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import com.twelvemonkeys.io.enc.Decoder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Base64Decoder
implements Decoder {
    static final byte[] PEM_ARRAY;
    static final byte[] PEM_CONVERT_ARRAY;
    private byte[] decodeBuffer = new byte[4];

    protected static int readFully(InputStream inputStream, byte[] byArray, int n, int n2) throws IOException {
        for (int i = 0; i < n2; ++i) {
            int n3 = inputStream.read();
            if (n3 == -1) {
                return i != 0 ? i : -1;
            }
            byArray[i + n] = (byte)n3;
        }
        return n2;
    }

    protected boolean decodeAtom(InputStream inputStream, ByteBuffer byteBuffer, int n) throws IOException {
        int n2;
        int n3 = -1;
        int n4 = -1;
        int n5 = -1;
        int n6 = -1;
        if (n < 2) {
            throw new IOException("BASE64Decoder: Not enough bytes for an atom.");
        }
        do {
            if ((n2 = inputStream.read()) != -1) continue;
            return false;
        } while (n2 == 10 || n2 == 13);
        this.decodeBuffer[0] = (byte)n2;
        n2 = Base64Decoder.readFully(inputStream, this.decodeBuffer, 1, n - 1);
        if (n2 == -1) {
            return false;
        }
        int n7 = n;
        if (n7 > 3 && this.decodeBuffer[3] == 61) {
            n7 = 3;
        }
        if (n7 > 2 && this.decodeBuffer[2] == 61) {
            n7 = 2;
        }
        switch (n7) {
            case 4: {
                n6 = PEM_CONVERT_ARRAY[this.decodeBuffer[3] & 0xFF];
            }
            case 3: {
                n5 = PEM_CONVERT_ARRAY[this.decodeBuffer[2] & 0xFF];
            }
            case 2: {
                n4 = PEM_CONVERT_ARRAY[this.decodeBuffer[1] & 0xFF];
                n3 = PEM_CONVERT_ARRAY[this.decodeBuffer[0] & 0xFF];
            }
        }
        switch (n7) {
            case 2: {
                byteBuffer.put((byte)(n3 << 2 & 0xFC | n4 >>> 4 & 3));
                break;
            }
            case 3: {
                byteBuffer.put((byte)(n3 << 2 & 0xFC | n4 >>> 4 & 3));
                byteBuffer.put((byte)(n4 << 4 & 0xF0 | n5 >>> 2 & 0xF));
                break;
            }
            case 4: {
                byteBuffer.put((byte)(n3 << 2 & 0xFC | n4 >>> 4 & 3));
                byteBuffer.put((byte)(n4 << 4 & 0xF0 | n5 >>> 2 & 0xF));
                byteBuffer.put((byte)(n5 << 6 & 0xC0 | n6 & 0x3F));
            }
        }
        return true;
    }

    @Override
    public int decode(InputStream inputStream, ByteBuffer byteBuffer) throws IOException {
        int n;
        int n2;
        do {
            n2 = 72;
            n = 0;
            while (n + 4 < n2 && this.decodeAtom(inputStream, byteBuffer, 4)) {
                n += 4;
            }
        } while (this.decodeAtom(inputStream, byteBuffer, n2 - n) && byteBuffer.remaining() > 54);
        return byteBuffer.position();
    }

    static {
        int n;
        PEM_ARRAY = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        PEM_CONVERT_ARRAY = new byte[256];
        for (n = 0; n < 255; ++n) {
            Base64Decoder.PEM_CONVERT_ARRAY[n] = -1;
        }
        for (n = 0; n < PEM_ARRAY.length; ++n) {
            Base64Decoder.PEM_CONVERT_ARRAY[Base64Decoder.PEM_ARRAY[n]] = (byte)n;
        }
    }
}

