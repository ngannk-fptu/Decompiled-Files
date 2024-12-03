/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.icns;

import java.io.DataInputStream;
import java.io.IOException;

final class ICNSUtil {
    private ICNSUtil() {
    }

    static String intToStr(int n) {
        return new String(new byte[]{(byte)((n & 0xFF000000) >> 24), (byte)((n & 0xFF0000) >> 16), (byte)((n & 0xFF00) >> 8), (byte)(n & 0xFF)});
    }

    static void decompress(DataInputStream dataInputStream, byte[] byArray, int n, int n2) throws IOException {
        int n3;
        int n4 = n;
        for (int i = n2; i > 0; i -= n3) {
            byte by = dataInputStream.readByte();
            if ((by & 0x80) != 0) {
                n3 = by + 131;
                byte by2 = dataInputStream.readByte();
                for (int j = 0; j < n3; ++j) {
                    byArray[n4++] = by2;
                }
                continue;
            }
            n3 = by + 1;
            dataInputStream.readFully(byArray, n4, n3);
            n4 += n3;
        }
    }
}

