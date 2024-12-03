/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.io.File;
import java.io.UnsupportedEncodingException;

final class FilePathToURI {
    private static boolean[] gNeedEscaping = new boolean[128];
    private static char[] gAfterEscaping1 = new char[128];
    private static char[] gAfterEscaping2 = new char[128];
    private static char[] gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private FilePathToURI() {
    }

    public static String filepath2URI(String string) {
        int n;
        int n2;
        if (string == null) {
            return null;
        }
        char c = File.separatorChar;
        string = string.replace(c, '/');
        int n3 = string.length();
        StringBuffer stringBuffer = new StringBuffer(n3 * 3);
        stringBuffer.append("file://");
        if (n3 >= 2 && string.charAt(1) == ':' && (n2 = Character.toUpperCase(string.charAt(0))) >= 65 && n2 <= 90) {
            stringBuffer.append('/');
        }
        for (n = 0; n < n3 && (n2 = string.charAt(n)) < 128; ++n) {
            if (gNeedEscaping[n2]) {
                stringBuffer.append('%');
                stringBuffer.append(gAfterEscaping1[n2]);
                stringBuffer.append(gAfterEscaping2[n2]);
                continue;
            }
            stringBuffer.append((char)n2);
        }
        if (n < n3) {
            byte[] byArray = null;
            try {
                byArray = string.substring(n).getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                return string;
            }
            for (byte by : byArray) {
                if (by < 0) {
                    n2 = by + 256;
                    stringBuffer.append('%');
                    stringBuffer.append(gHexChs[n2 >> 4]);
                    stringBuffer.append(gHexChs[n2 & 0xF]);
                    continue;
                }
                if (gNeedEscaping[by]) {
                    stringBuffer.append('%');
                    stringBuffer.append(gAfterEscaping1[by]);
                    stringBuffer.append(gAfterEscaping2[by]);
                    continue;
                }
                stringBuffer.append((char)by);
            }
        }
        return stringBuffer.toString();
    }

    static {
        for (int i = 0; i <= 31; ++i) {
            FilePathToURI.gNeedEscaping[i] = true;
            FilePathToURI.gAfterEscaping1[i] = gHexChs[i >> 4];
            FilePathToURI.gAfterEscaping2[i] = gHexChs[i & 0xF];
        }
        FilePathToURI.gNeedEscaping[127] = true;
        FilePathToURI.gAfterEscaping1[127] = 55;
        FilePathToURI.gAfterEscaping2[127] = 70;
        for (char c : new char[]{' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`'}) {
            FilePathToURI.gNeedEscaping[c] = true;
            FilePathToURI.gAfterEscaping1[c] = gHexChs[c >> 4];
            FilePathToURI.gAfterEscaping2[c] = gHexChs[c & 0xF];
        }
    }
}

