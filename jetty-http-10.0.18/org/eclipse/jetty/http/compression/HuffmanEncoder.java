/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.http.compression;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpTokens;
import org.eclipse.jetty.http.compression.Huffman;

public class HuffmanEncoder {
    private HuffmanEncoder() {
    }

    public static int octetsNeeded(String s) {
        return HuffmanEncoder.octetsNeeded(Huffman.CODES, s);
    }

    public static int octetsNeeded(byte[] b) {
        int needed = 0;
        for (byte value : b) {
            int c = 0xFF & value;
            needed += Huffman.CODES[c][1];
        }
        return (needed + 7) / 8;
    }

    public static void encode(ByteBuffer buffer, String s) {
        HuffmanEncoder.encode(Huffman.CODES, buffer, s);
    }

    public static int octetsNeededLowerCase(String s) {
        return HuffmanEncoder.octetsNeeded(Huffman.LCCODES, s);
    }

    public static void encodeLowerCase(ByteBuffer buffer, String s) {
        HuffmanEncoder.encode(Huffman.LCCODES, buffer, s);
    }

    private static int octetsNeeded(int[][] table, String s) {
        int needed = 0;
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (HttpTokens.isIllegalFieldVchar(c)) {
                return -1;
            }
            needed += table[c][1];
        }
        return (needed + 7) / 8;
    }

    private static void encode(int[][] table, ByteBuffer buffer, String s) {
        long current = 0L;
        int n = 0;
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (HttpTokens.isIllegalFieldVchar(c)) {
                throw new IllegalArgumentException();
            }
            int code = table[c][0];
            int bits = table[c][1];
            current <<= bits;
            current |= (long)code;
            n += bits;
            while (n >= 8) {
                buffer.put((byte)(current >> (n -= 8)));
            }
        }
        if (n > 0) {
            current <<= 8 - n;
            buffer.put((byte)(current |= (long)(255 >>> n)));
        }
    }
}

