/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.stream;

import java.io.File;
import java.io.UnsupportedEncodingException;

class FilePathToURI {
    private static boolean[] gNeedEscaping = new boolean[128];
    private static char[] gAfterEscaping1 = new char[128];
    private static char[] gAfterEscaping2 = new char[128];
    private static char[] gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    FilePathToURI() {
    }

    public static String filepath2URI(String string) {
        int n;
        if (string == null) {
            return null;
        }
        char c = File.separatorChar;
        string = string.replace(c, '/');
        int n2 = string.length();
        StringBuffer stringBuffer = new StringBuffer(n2 * 3);
        stringBuffer.append("file://");
        if (n2 >= 2 && string.charAt(1) == ':' && (n = Character.toUpperCase(string.charAt(0))) >= 65 && n <= 90) {
            stringBuffer.append('/');
        }
        int n3 = 0;
        while (n3 < n2) {
            n = string.charAt(n3);
            if (n >= 128) break;
            if (gNeedEscaping[n]) {
                stringBuffer.append('%');
                stringBuffer.append(gAfterEscaping1[n]);
                stringBuffer.append(gAfterEscaping2[n]);
            } else {
                stringBuffer.append((char)n);
            }
            ++n3;
        }
        if (n3 < n2) {
            byte[] byArray = null;
            try {
                byArray = string.substring(n3).getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                return string;
            }
            n2 = byArray.length;
            n3 = 0;
            while (n3 < n2) {
                byte by = byArray[n3];
                if (by < 0) {
                    n = by + 256;
                    stringBuffer.append('%');
                    stringBuffer.append(gHexChs[n >> 4]);
                    stringBuffer.append(gHexChs[n & 0xF]);
                } else if (gNeedEscaping[by]) {
                    stringBuffer.append('%');
                    stringBuffer.append(gAfterEscaping1[by]);
                    stringBuffer.append(gAfterEscaping2[by]);
                } else {
                    stringBuffer.append((char)by);
                }
                ++n3;
            }
        }
        return stringBuffer.toString();
    }

    static {
        int n = 0;
        while (n <= 31) {
            FilePathToURI.gNeedEscaping[n] = true;
            FilePathToURI.gAfterEscaping1[n] = gHexChs[n >> 4];
            FilePathToURI.gAfterEscaping2[n] = gHexChs[n & 0xF];
            ++n;
        }
        FilePathToURI.gNeedEscaping[127] = true;
        FilePathToURI.gAfterEscaping1[127] = 55;
        FilePathToURI.gAfterEscaping2[127] = 70;
        char[] cArray = new char[]{' ', '<', '>', '#', '%', '\"', '{', '}', '|', '\\', '^', '~', '[', ']', '`'};
        int n2 = cArray.length;
        int n3 = 0;
        while (n3 < n2) {
            char c = cArray[n3];
            FilePathToURI.gNeedEscaping[c] = true;
            FilePathToURI.gAfterEscaping1[c] = gHexChs[c >> 4];
            FilePathToURI.gAfterEscaping2[c] = gHexChs[c & 0xF];
            ++n3;
        }
    }
}

