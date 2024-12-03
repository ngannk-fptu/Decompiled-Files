/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncodeUtil {
    private static final Logger log;
    public static final char[] hexTable;
    private static BitSet URISave;
    private static BitSet URISaveEx;

    public static String escape(String string) {
        return EncodeUtil.escape(string, '%', false);
    }

    public static String escapePath(String path) {
        return EncodeUtil.escape(path, '%', true);
    }

    private static String escape(String string, char escape, boolean isPath) {
        BitSet validChars = isPath ? URISaveEx : URISave;
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        StringBuffer out = new StringBuffer(bytes.length);
        for (byte aByte : bytes) {
            int c = aByte & 0xFF;
            if (validChars.get(c) && c != escape) {
                out.append((char)c);
                continue;
            }
            out.append(escape);
            out.append(hexTable[c >> 4 & 0xF]);
            out.append(hexTable[c & 0xF]);
        }
        return out.toString();
    }

    public static String unescape(String string) {
        return EncodeUtil.unescape(string, '%');
    }

    private static String unescape(String string, char escape) {
        byte[] utf8 = string.getBytes(StandardCharsets.UTF_8);
        if (utf8.length >= 1 && utf8[utf8.length - 1] == escape || utf8.length >= 2 && utf8[utf8.length - 2] == escape) {
            throw new IllegalArgumentException("Premature end of escape sequence at end of input");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(utf8.length);
        for (int k = 0; k < utf8.length; ++k) {
            byte b = utf8[k];
            if (b == escape) {
                out.write((EncodeUtil.decodeDigit(utf8[++k]) << 4) + EncodeUtil.decodeDigit(utf8[++k]));
                continue;
            }
            out.write(b);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private static byte decodeDigit(byte b) {
        if (b >= 48 && b <= 57) {
            return (byte)(b - 48);
        }
        if (b >= 65 && b <= 70) {
            return (byte)(b - 55);
        }
        if (b >= 97 && b <= 102) {
            return (byte)(b - 87);
        }
        throw new IllegalArgumentException("Escape sequence is not hexadecimal: " + (char)b);
    }

    private EncodeUtil() {
    }

    static {
        int i;
        log = LoggerFactory.getLogger(EncodeUtil.class);
        hexTable = "0123456789abcdef".toCharArray();
        URISave = new BitSet(256);
        for (i = 97; i <= 122; ++i) {
            URISave.set(i);
        }
        for (i = 65; i <= 90; ++i) {
            URISave.set(i);
        }
        for (i = 48; i <= 57; ++i) {
            URISave.set(i);
        }
        URISave.set(45);
        URISave.set(95);
        URISave.set(46);
        URISave.set(33);
        URISave.set(126);
        URISave.set(42);
        URISave.set(39);
        URISave.set(40);
        URISave.set(41);
        URISaveEx = (BitSet)URISave.clone();
        URISaveEx.set(47);
    }
}

