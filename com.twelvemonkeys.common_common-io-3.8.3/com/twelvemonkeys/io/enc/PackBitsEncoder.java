/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import com.twelvemonkeys.io.enc.Encoder;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class PackBitsEncoder
implements Encoder {
    private final byte[] buffer = new byte[128];

    @Override
    public void encode(OutputStream outputStream, ByteBuffer byteBuffer) throws IOException {
        this.encode(outputStream, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        byteBuffer.position(byteBuffer.remaining());
    }

    private void encode(OutputStream outputStream, byte[] byArray, int n, int n2) throws IOException {
        int n3 = n;
        int n4 = n + n2 - 1;
        int n5 = n4 - 1;
        while (n3 <= n4) {
            int n6;
            byte by = byArray[n3];
            for (n6 = 1; n6 < 127 && n3 < n4 && byArray[n3] == byArray[n3 + 1]; ++n3, ++n6) {
            }
            if (n6 > 1) {
                ++n3;
                outputStream.write(-(n6 - 1));
                outputStream.write(by);
            }
            n6 = 0;
            while (n6 < 128 && (n3 < n4 && byArray[n3] != byArray[n3 + 1] || n3 < n5 && byArray[n3] != byArray[n3 + 2])) {
                this.buffer[n6++] = byArray[n3++];
            }
            if (n3 == n4 && n6 > 0 && n6 < 128) {
                this.buffer[n6++] = byArray[n3++];
            }
            if (n6 > 0) {
                outputStream.write(n6 - 1);
                outputStream.write(this.buffer, 0, n6);
            }
            if (n3 != n4 || n6 > 0 && n6 < 128) continue;
            outputStream.write(0);
            outputStream.write(byArray[n3++]);
        }
    }
}

