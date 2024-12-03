/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.pdf.PRTokeniser;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Utilities {
    public static Set<String> getKeySet(Map<String, ?> table) {
        return table == null ? Collections.emptySet() : table.keySet();
    }

    public static Object[][] addToArray(Object[][] original, Object[] item) {
        if (original == null) {
            original = new Object[][]{item};
            return original;
        }
        Object[][] original2 = new Object[original.length + 1][];
        System.arraycopy(original, 0, original2, 0, original.length);
        original2[original.length] = item;
        return original2;
    }

    public static boolean checkTrueOrFalse(Properties attributes, String key) {
        return "true".equalsIgnoreCase(attributes.getProperty(key));
    }

    public static String unEscapeURL(String src) {
        StringBuilder bf = new StringBuilder();
        char[] s = src.toCharArray();
        for (int k = 0; k < s.length; ++k) {
            char c = s[k];
            if (c == '%') {
                if (k + 2 >= s.length) {
                    bf.append(c);
                    continue;
                }
                int a0 = PRTokeniser.getHex(s[k + 1]);
                int a1 = PRTokeniser.getHex(s[k + 2]);
                if (a0 < 0 || a1 < 0) {
                    bf.append(c);
                    continue;
                }
                bf.append((char)(a0 * 16 + a1));
                k += 2;
                continue;
            }
            bf.append(c);
        }
        return bf.toString();
    }

    public static URL toURL(String filename) throws MalformedURLException {
        try {
            return new URL(filename);
        }
        catch (Exception e) {
            return new File(filename).toURI().toURL();
        }
    }

    public static void skip(InputStream is, int size) throws IOException {
        long n;
        while (size > 0 && (n = is.skip(size)) > 0L) {
            size = (int)((long)size - n);
        }
    }

    public static float millimetersToPoints(float value) {
        return Utilities.inchesToPoints(Utilities.millimetersToInches(value));
    }

    public static float millimetersToInches(float value) {
        return value / 25.4f;
    }

    public static float pointsToMillimeters(float value) {
        return Utilities.inchesToMillimeters(Utilities.pointsToInches(value));
    }

    public static float pointsToInches(float value) {
        return value / 72.0f;
    }

    public static float inchesToMillimeters(float value) {
        return value * 25.4f;
    }

    public static float inchesToPoints(float value) {
        return value * 72.0f;
    }

    public static boolean isSurrogateHigh(char c) {
        return c >= '\ud800' && c <= '\udbff';
    }

    public static boolean isSurrogateLow(char c) {
        return c >= '\udc00' && c <= '\udfff';
    }

    public static boolean isSurrogatePair(String text, int idx) {
        if (idx < 0 || idx > text.length() - 2) {
            return false;
        }
        return Utilities.isSurrogateHigh(text.charAt(idx)) && Utilities.isSurrogateLow(text.charAt(idx + 1));
    }

    public static boolean isSurrogatePair(char[] text, int idx) {
        if (idx < 0 || idx > text.length - 2) {
            return false;
        }
        return Utilities.isSurrogateHigh(text[idx]) && Utilities.isSurrogateLow(text[idx + 1]);
    }

    public static int convertToUtf32(char highSurrogate, char lowSurrogate) {
        return (highSurrogate - 55296) * 1024 + (lowSurrogate - 56320) + 65536;
    }

    public static int convertToUtf32(char[] text, int idx) {
        return (text[idx] - 55296) * 1024 + (text[idx + 1] - 56320) + 65536;
    }

    public static int convertToUtf32(String text, int idx) {
        return (text.charAt(idx) - 55296) * 1024 + (text.charAt(idx + 1) - 56320) + 65536;
    }

    public static String convertFromUtf32(int codePoint) {
        if (codePoint < 65536) {
            return Character.toString((char)codePoint);
        }
        return new String(new char[]{(char)((codePoint -= 65536) / 1024 + 55296), (char)(codePoint % 1024 + 56320)});
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        int nRead;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}

