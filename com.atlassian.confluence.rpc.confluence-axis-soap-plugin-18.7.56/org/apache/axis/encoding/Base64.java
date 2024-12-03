/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.axis.utils.Messages;

public class Base64 {
    private static final char[] S_BASE64CHAR;
    private static final char S_BASE64PAD = '=';
    private static final byte[] S_DECODETABLE;

    private static int decode0(char[] ibuf, byte[] obuf, int wp) {
        int outlen = 3;
        if (ibuf[3] == '=') {
            outlen = 2;
        }
        if (ibuf[2] == '=') {
            outlen = 1;
        }
        byte b0 = S_DECODETABLE[ibuf[0]];
        byte b1 = S_DECODETABLE[ibuf[1]];
        byte b2 = S_DECODETABLE[ibuf[2]];
        byte b3 = S_DECODETABLE[ibuf[3]];
        switch (outlen) {
            case 1: {
                obuf[wp] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                return 1;
            }
            case 2: {
                obuf[wp++] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                obuf[wp] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
                return 2;
            }
            case 3: {
                obuf[wp++] = (byte)(b0 << 2 & 0xFC | b1 >> 4 & 3);
                obuf[wp++] = (byte)(b1 << 4 & 0xF0 | b2 >> 2 & 0xF);
                obuf[wp] = (byte)(b2 << 6 & 0xC0 | b3 & 0x3F);
                return 3;
            }
        }
        throw new RuntimeException(Messages.getMessage("internalError00"));
    }

    public static byte[] decode(char[] data, int off, int len) {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[len / 4 * 3 + 3];
        int obufcount = 0;
        for (int i = off; i < off + len; ++i) {
            char ch = data[i];
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127)) continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount != ibuf.length) continue;
            ibufcount = 0;
            obufcount += Base64.decode0(ibuf, obuf, obufcount);
        }
        if (obufcount == obuf.length) {
            return obuf;
        }
        byte[] ret = new byte[obufcount];
        System.arraycopy(obuf, 0, ret, 0, obufcount);
        return ret;
    }

    public static byte[] decode(String data) {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[data.length() / 4 * 3 + 3];
        int obufcount = 0;
        for (int i = 0; i < data.length(); ++i) {
            char ch = data.charAt(i);
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127)) continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount != ibuf.length) continue;
            ibufcount = 0;
            obufcount += Base64.decode0(ibuf, obuf, obufcount);
        }
        if (obufcount == obuf.length) {
            return obuf;
        }
        byte[] ret = new byte[obufcount];
        System.arraycopy(obuf, 0, ret, 0, obufcount);
        return ret;
    }

    public static void decode(char[] data, int off, int len, OutputStream ostream) throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];
        for (int i = off; i < off + len; ++i) {
            char ch = data[i];
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127)) continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount != ibuf.length) continue;
            ibufcount = 0;
            int obufcount = Base64.decode0(ibuf, obuf, 0);
            ostream.write(obuf, 0, obufcount);
        }
    }

    public static void decode(String data, OutputStream ostream) throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];
        for (int i = 0; i < data.length(); ++i) {
            char ch = data.charAt(i);
            if (ch != '=' && (ch >= S_DECODETABLE.length || S_DECODETABLE[ch] == 127)) continue;
            ibuf[ibufcount++] = ch;
            if (ibufcount != ibuf.length) continue;
            ibufcount = 0;
            int obufcount = Base64.decode0(ibuf, obuf, 0);
            ostream.write(obuf, 0, obufcount);
        }
    }

    public static String encode(byte[] data) {
        return Base64.encode(data, 0, data.length);
    }

    public static String encode(byte[] data, int off, int len) {
        int i;
        int rest;
        if (len <= 0) {
            return "";
        }
        char[] out = new char[len / 3 * 4 + 4];
        int rindex = off;
        int windex = 0;
        for (rest = len - off; rest >= 3; rest -= 3) {
            i = ((data[rindex] & 0xFF) << 16) + ((data[rindex + 1] & 0xFF) << 8) + (data[rindex + 2] & 0xFF);
            out[windex++] = S_BASE64CHAR[i >> 18];
            out[windex++] = S_BASE64CHAR[i >> 12 & 0x3F];
            out[windex++] = S_BASE64CHAR[i >> 6 & 0x3F];
            out[windex++] = S_BASE64CHAR[i & 0x3F];
            rindex += 3;
        }
        if (rest == 1) {
            i = data[rindex] & 0xFF;
            out[windex++] = S_BASE64CHAR[i >> 2];
            out[windex++] = S_BASE64CHAR[i << 4 & 0x3F];
            out[windex++] = 61;
            out[windex++] = 61;
        } else if (rest == 2) {
            i = ((data[rindex] & 0xFF) << 8) + (data[rindex + 1] & 0xFF);
            out[windex++] = S_BASE64CHAR[i >> 10];
            out[windex++] = S_BASE64CHAR[i >> 4 & 0x3F];
            out[windex++] = S_BASE64CHAR[i << 2 & 0x3F];
            out[windex++] = 61;
        }
        return new String(out, 0, windex);
    }

    public static void encode(byte[] data, int off, int len, OutputStream ostream) throws IOException {
        int i;
        int rest;
        if (len <= 0) {
            return;
        }
        byte[] out = new byte[4];
        int rindex = off;
        for (rest = len - off; rest >= 3; rest -= 3) {
            i = ((data[rindex] & 0xFF) << 16) + ((data[rindex + 1] & 0xFF) << 8) + (data[rindex + 2] & 0xFF);
            out[0] = (byte)S_BASE64CHAR[i >> 18];
            out[1] = (byte)S_BASE64CHAR[i >> 12 & 0x3F];
            out[2] = (byte)S_BASE64CHAR[i >> 6 & 0x3F];
            out[3] = (byte)S_BASE64CHAR[i & 0x3F];
            ostream.write(out, 0, 4);
            rindex += 3;
        }
        if (rest == 1) {
            i = data[rindex] & 0xFF;
            out[0] = (byte)S_BASE64CHAR[i >> 2];
            out[1] = (byte)S_BASE64CHAR[i << 4 & 0x3F];
            out[2] = 61;
            out[3] = 61;
            ostream.write(out, 0, 4);
        } else if (rest == 2) {
            i = ((data[rindex] & 0xFF) << 8) + (data[rindex + 1] & 0xFF);
            out[0] = (byte)S_BASE64CHAR[i >> 10];
            out[1] = (byte)S_BASE64CHAR[i >> 4 & 0x3F];
            out[2] = (byte)S_BASE64CHAR[i << 2 & 0x3F];
            out[3] = 61;
            ostream.write(out, 0, 4);
        }
    }

    public static void encode(byte[] data, int off, int len, Writer writer) throws IOException {
        int i;
        int rest;
        if (len <= 0) {
            return;
        }
        char[] out = new char[4];
        int rindex = off;
        int output = 0;
        for (rest = len - off; rest >= 3; rest -= 3) {
            i = ((data[rindex] & 0xFF) << 16) + ((data[rindex + 1] & 0xFF) << 8) + (data[rindex + 2] & 0xFF);
            out[0] = S_BASE64CHAR[i >> 18];
            out[1] = S_BASE64CHAR[i >> 12 & 0x3F];
            out[2] = S_BASE64CHAR[i >> 6 & 0x3F];
            out[3] = S_BASE64CHAR[i & 0x3F];
            writer.write(out, 0, 4);
            rindex += 3;
            if ((output += 4) % 76 != 0) continue;
            writer.write("\n");
        }
        if (rest == 1) {
            i = data[rindex] & 0xFF;
            out[0] = S_BASE64CHAR[i >> 2];
            out[1] = S_BASE64CHAR[i << 4 & 0x3F];
            out[2] = 61;
            out[3] = 61;
            writer.write(out, 0, 4);
        } else if (rest == 2) {
            i = ((data[rindex] & 0xFF) << 8) + (data[rindex + 1] & 0xFF);
            out[0] = S_BASE64CHAR[i >> 10];
            out[1] = S_BASE64CHAR[i >> 4 & 0x3F];
            out[2] = S_BASE64CHAR[i << 2 & 0x3F];
            out[3] = 61;
            writer.write(out, 0, 4);
        }
    }

    static {
        int i;
        S_BASE64CHAR = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
        S_DECODETABLE = new byte[128];
        for (i = 0; i < S_DECODETABLE.length; ++i) {
            Base64.S_DECODETABLE[i] = 127;
        }
        for (i = 0; i < S_BASE64CHAR.length; ++i) {
            Base64.S_DECODETABLE[Base64.S_BASE64CHAR[i]] = (byte)i;
        }
    }
}

