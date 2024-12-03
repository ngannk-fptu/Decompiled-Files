/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import com.twelvemonkeys.io.enc.Base64Decoder;
import com.twelvemonkeys.io.enc.Encoder;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Base64Encoder
implements Encoder {
    @Override
    public void encode(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException {
        block4: while (byteBuffer.hasRemaining()) {
            byte by;
            byte by2;
            byte by3;
            int n = Math.min(3, byteBuffer.remaining());
            switch (n) {
                case 1: {
                    by3 = byteBuffer.get();
                    by2 = 0;
                    outputStream.write(Base64Decoder.PEM_ARRAY[by3 >>> 2 & 0x3F]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[(by3 << 4 & 0x30) + (by2 >>> 4 & 0xF)]);
                    outputStream.write(61);
                    outputStream.write(61);
                    continue block4;
                }
                case 2: {
                    by3 = byteBuffer.get();
                    by2 = byteBuffer.get();
                    by = 0;
                    outputStream.write(Base64Decoder.PEM_ARRAY[by3 >>> 2 & 0x3F]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[(by3 << 4 & 0x30) + (by2 >>> 4 & 0xF)]);
                    outputStream.write(Base64Decoder.PEM_ARRAY[(by2 << 2 & 0x3C) + (by >>> 6 & 3)]);
                    outputStream.write(61);
                    continue block4;
                }
            }
            by3 = byteBuffer.get();
            by2 = byteBuffer.get();
            by = byteBuffer.get();
            outputStream.write(Base64Decoder.PEM_ARRAY[by3 >>> 2 & 0x3F]);
            outputStream.write(Base64Decoder.PEM_ARRAY[(by3 << 4 & 0x30) + (by2 >>> 4 & 0xF)]);
            outputStream.write(Base64Decoder.PEM_ARRAY[(by2 << 2 & 0x3C) + (by >>> 6 & 3)]);
            outputStream.write(Base64Decoder.PEM_ARRAY[by & 0x3F]);
        }
    }
}

