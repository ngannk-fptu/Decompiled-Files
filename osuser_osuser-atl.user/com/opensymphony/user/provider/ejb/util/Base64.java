/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.ejb.util;

public final class Base64 {
    private static final char[] enc_table = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final byte[] dec_table = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    public static byte[] decode(byte[] data) {
        int i;
        int padCount = 0;
        int len = data.length;
        int real_len = 0;
        for (i = len - 1; i >= 0; --i) {
            if (data[i] > 32) {
                ++real_len;
            }
            if (data[i] != 61) continue;
            ++padCount;
        }
        if (real_len % 4 != 0) {
            throw new IllegalArgumentException("Length not a multiple of 4");
        }
        int ret_len = real_len / 4 * 3 - padCount;
        byte[] ret = new byte[ret_len];
        i = 0;
        byte[] t = new byte[4];
        int output_index = 0;
        int j = 0;
        t[3] = 61;
        t[2] = 61;
        t[1] = 61;
        t[0] = 61;
        while (i < len) {
            byte c;
            if ((c = data[i++]) > 32) {
                t[j++] = c;
            }
            if (j != 4) continue;
            output_index += Base64.decode(ret, output_index, t[0], t[1], t[2], t[3]);
            j = 0;
            t[3] = 61;
            t[2] = 61;
            t[1] = 61;
            t[0] = 61;
        }
        if (j > 0) {
            Base64.decode(ret, output_index, t[0], t[1], t[2], t[3]);
        }
        return ret;
    }

    public static int decode(byte[] ret, int ret_off, byte a, byte b, byte c, byte d) {
        byte da = dec_table[a];
        byte db = dec_table[b];
        byte dc = dec_table[c];
        byte dd = dec_table[d];
        if (da == -1 || db == -1 || dc == -1 && c != 61 || dd == -1 && d != 61) {
            throw new IllegalArgumentException("Invalid character [" + (a & 0xFF) + ", " + (b & 0xFF) + ", " + (c & 0xFF) + ", " + (d & 0xFF) + "]");
        }
        ret[ret_off++] = (byte)(da << 2 | db >>> 4);
        if (c == 61) {
            return 1;
        }
        ret[ret_off++] = (byte)(db << 4 | dc >>> 2);
        if (d == 61) {
            return 2;
        }
        ret[ret_off++] = (byte)(dc << 6 | dd);
        return 3;
    }

    public static byte[] encode(byte[] data) {
        byte b;
        byte a;
        int i = 0;
        int j = 0;
        int len = data.length;
        int delta = len % 3;
        int outlen = (len + 2) / 3 * 4 + (len == 0 ? 2 : 0);
        byte[] output = new byte[outlen];
        for (int count = len / 3; count > 0; --count) {
            a = data[i++];
            b = data[i++];
            byte c = data[i++];
            output[j++] = (byte)enc_table[a >>> 2 & 0x3F];
            output[j++] = (byte)enc_table[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            output[j++] = (byte)enc_table[(b << 2 & 0x3C) + (c >>> 6 & 3)];
            output[j++] = (byte)enc_table[c & 0x3F];
        }
        if (delta == 1) {
            a = data[i++];
            output[j++] = (byte)enc_table[a >>> 2 & 0x3F];
            output[j++] = (byte)enc_table[a << 4 & 0x30];
            output[j++] = 61;
            output[j++] = 61;
        } else if (delta == 2) {
            a = data[i++];
            b = data[i++];
            output[j++] = (byte)enc_table[a >>> 2 & 0x3F];
            output[j++] = (byte)enc_table[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            output[j++] = (byte)enc_table[b << 2 & 0x3C];
            output[j++] = 61;
        }
        if (j != outlen) {
            throw new IllegalArgumentException("output length was not the expected");
        }
        return output;
    }
}

