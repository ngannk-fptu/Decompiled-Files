/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http;

public class HeaderUtil {
    public static String toPrintableString(byte[] bytes, int offset, int len) {
        StringBuilder result = new StringBuilder();
        for (int i = offset; i < offset + len; ++i) {
            char c = (char)(bytes[i] & 0xFF);
            if (c < ' ' || c > '~') {
                result.append("0x");
                result.append(Character.forDigit(c >> 4 & 0xF, 16));
                result.append(Character.forDigit(c & 0xF, 16));
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    private HeaderUtil() {
    }
}

