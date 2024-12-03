/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

public class Native2AsciiUtils {
    private static final int MAX_ASCII = 127;

    public static String native2ascii(String line) {
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c <= '\u007f') {
                sb.append(c);
                continue;
            }
            sb.append(String.format("\\u%04x", c));
        }
        return sb.toString();
    }

    public static String ascii2native(String line) {
        StringBuilder sb = new StringBuilder();
        int inputLen = line.length();
        for (int i = 0; i < inputLen; ++i) {
            int unescaped;
            char u;
            char c = line.charAt(i);
            if (c != '\\' || i >= inputLen - 5) {
                sb.append(c);
                continue;
            }
            if ((u = line.charAt(++i)) == 'u' && (unescaped = Native2AsciiUtils.tryParse(line, i + 1)) >= 0) {
                sb.append((char)unescaped);
                i += 4;
                continue;
            }
            sb.append(c).append(u);
        }
        return sb.toString();
    }

    private static int tryParse(String line, int startIdx) {
        try {
            return Integer.parseInt(line.substring(startIdx, startIdx + 4), 16);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }
}

