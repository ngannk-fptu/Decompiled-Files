/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.util.Locale;

public final class Type1FontUtil {
    private Type1FontUtil() {
    }

    public static String hexEncode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String string = Integer.toHexString(aByte & 0xFF);
            if (string.length() == 1) {
                sb.append('0');
            }
            sb.append(string.toUpperCase(Locale.US));
        }
        return sb.toString();
    }

    public static byte[] hexDecode(String string) {
        if (string.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0; i < string.length(); i += 2) {
            bytes[i / 2] = (byte)Integer.parseInt(string.substring(i, i + 2), 16);
        }
        return bytes;
    }

    public static byte[] eexecEncrypt(byte[] buffer) {
        return Type1FontUtil.encrypt(buffer, 55665, 4);
    }

    public static byte[] charstringEncrypt(byte[] buffer, int n) {
        return Type1FontUtil.encrypt(buffer, 4330, n);
    }

    private static byte[] encrypt(byte[] plaintextBytes, int r, int n) {
        byte[] buffer = new byte[plaintextBytes.length + n];
        System.arraycopy(plaintextBytes, 0, buffer, n, buffer.length - n);
        int c1 = 52845;
        int c2 = 22719;
        byte[] ciphertextBytes = new byte[buffer.length];
        for (int i = 0; i < buffer.length; ++i) {
            int plain = buffer[i] & 0xFF;
            int cipher = plain ^ r >> 8;
            ciphertextBytes[i] = (byte)cipher;
            r = (cipher + r) * c1 + c2 & 0xFFFF;
        }
        return ciphertextBytes;
    }

    public static byte[] eexecDecrypt(byte[] buffer) {
        return Type1FontUtil.decrypt(buffer, 55665, 4);
    }

    public static byte[] charstringDecrypt(byte[] buffer, int n) {
        return Type1FontUtil.decrypt(buffer, 4330, n);
    }

    private static byte[] decrypt(byte[] ciphertextBytes, int r, int n) {
        byte[] buffer = new byte[ciphertextBytes.length];
        int c1 = 52845;
        int c2 = 22719;
        for (int i = 0; i < ciphertextBytes.length; ++i) {
            int cipher = ciphertextBytes[i] & 0xFF;
            int plain = cipher ^ r >> 8;
            buffer[i] = (byte)plain;
            r = (cipher + r) * c1 + c2 & 0xFFFF;
        }
        byte[] plaintextBytes = new byte[ciphertextBytes.length - n];
        System.arraycopy(buffer, n, plaintextBytes, 0, plaintextBytes.length);
        return plaintextBytes;
    }
}

