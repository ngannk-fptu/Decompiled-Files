/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.apache.felix.bundlerepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.osgi.framework.Bundle;

public class Util {
    private static final byte[] encTab = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] decTab = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    public static String getClassName(String className) {
        if (className == null) {
            className = "";
        }
        return className.lastIndexOf(46) < 0 ? "" : className.substring(className.lastIndexOf(46) + 1);
    }

    public static String getBundleName(Bundle bundle) {
        String name = (String)bundle.getHeaders().get("Bundle-Name");
        return name == null ? "Bundle " + Long.toString(bundle.getBundleId()) : name;
    }

    public static String[] parseDelimitedString(String value, String delim) {
        if (value == null) {
            value = "";
        }
        ArrayList<String> list = new ArrayList<String>();
        int CHAR = 1;
        int DELIMITER = 2;
        int STARTQUOTE = 4;
        int ENDQUOTE = 8;
        StringBuffer sb = new StringBuffer();
        int expecting = CHAR | DELIMITER | STARTQUOTE;
        for (int i = 0; i < value.length(); ++i) {
            boolean isQuote;
            char c = value.charAt(i);
            boolean isDelimiter = delim.indexOf(c) >= 0;
            boolean bl = isQuote = c == '\"';
            if (isDelimiter && (expecting & DELIMITER) > 0) {
                list.add(sb.toString().trim());
                sb.delete(0, sb.length());
                expecting = CHAR | DELIMITER | STARTQUOTE;
                continue;
            }
            if (isQuote && (expecting & STARTQUOTE) > 0) {
                sb.append(c);
                expecting = CHAR | ENDQUOTE;
                continue;
            }
            if (isQuote && (expecting & ENDQUOTE) > 0) {
                sb.append(c);
                expecting = CHAR | STARTQUOTE | DELIMITER;
                continue;
            }
            if ((expecting & CHAR) > 0) {
                sb.append(c);
                continue;
            }
            throw new IllegalArgumentException("Invalid delimited string: " + value);
        }
        if (sb.length() > 0) {
            list.add(sb.toString().trim());
        }
        return list.toArray(new String[list.size()]);
    }

    public static int compareVersion(int[] v1, int[] v2) {
        if (v1[0] > v2[0]) {
            return 1;
        }
        if (v1[0] < v2[0]) {
            return -1;
        }
        if (v1[1] > v2[1]) {
            return 1;
        }
        if (v1[1] < v2[1]) {
            return -1;
        }
        if (v1[2] > v2[2]) {
            return 1;
        }
        if (v1[2] < v2[2]) {
            return -1;
        }
        return 0;
    }

    public static String base64Encode(String s) throws IOException {
        return Util.encode(s.getBytes(), 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String encode(byte[] in, int len) throws IOException {
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        try {
            baos = new ByteArrayOutputStream();
            bais = new ByteArrayInputStream(in);
            Util.encode(bais, baos, len);
            String string = new String(baos.toByteArray());
            return string;
        }
        finally {
            if (baos != null) {
                baos.close();
            }
            if (bais != null) {
                bais.close();
            }
        }
    }

    public static void encode(InputStream in, OutputStream out, int len) throws IOException {
        int b;
        if (len % 4 != 0) {
            throw new IllegalArgumentException("Length must be a multiple of 4");
        }
        int bits = 0;
        int nbits = 0;
        int nbytes = 0;
        while ((b = in.read()) != -1) {
            bits = bits << 8 | b;
            nbits += 8;
            while (nbits >= 6) {
                out.write(encTab[0x3F & bits >> (nbits -= 6)]);
                if (len == 0 || ++nbytes < len) continue;
                out.write(13);
                out.write(10);
                nbytes -= len;
            }
        }
        switch (nbits) {
            case 2: {
                out.write(encTab[0x3F & bits << 4]);
                out.write(61);
                out.write(61);
                break;
            }
            case 4: {
                out.write(encTab[0x3F & bits << 2]);
                out.write(61);
            }
        }
        if (len != 0) {
            if (nbytes != 0) {
                out.write(13);
                out.write(10);
            }
            out.write(13);
            out.write(10);
        }
    }
}

