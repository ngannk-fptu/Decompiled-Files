/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder.wmf.tosvg;

import java.io.UnsupportedEncodingException;
import org.apache.batik.transcoder.wmf.tosvg.WMFFont;

public class WMFUtilities {
    public static String decodeString(WMFFont wmfFont, byte[] bstr) {
        try {
            switch (wmfFont.charset) {
                case 0: {
                    return new String(bstr, "ISO-8859-1");
                }
                case 1: {
                    return new String(bstr, "US-ASCII");
                }
                case 128: {
                    return new String(bstr, "Shift_JIS");
                }
                case 129: {
                    return new String(bstr, "cp949");
                }
                case 130: {
                    return new String(bstr, "x-Johab");
                }
                case 134: {
                    return new String(bstr, "GB2312");
                }
                case 136: {
                    return new String(bstr, "Big5");
                }
                case 161: {
                    return new String(bstr, "windows-1253");
                }
                case 162: {
                    return new String(bstr, "cp1254");
                }
                case 163: {
                    return new String(bstr, "cp1258");
                }
                case 177: {
                    return new String(bstr, "windows-1255");
                }
                case 178: {
                    return new String(bstr, "windows-1256");
                }
                case 204: {
                    return new String(bstr, "windows-1251");
                }
                case 222: {
                    return new String(bstr, "cp874");
                }
                case 238: {
                    return new String(bstr, "cp1250");
                }
                case 255: {
                    return new String(bstr, "cp437");
                }
            }
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        return new String(bstr);
    }

    public static int getHorizontalAlignment(int align) {
        int v = align;
        v %= 24;
        if ((v %= 8) >= 6) {
            return 6;
        }
        if (v >= 2) {
            return 2;
        }
        return 0;
    }

    public static int getVerticalAlignment(int align) {
        int v = align;
        if (v / 24 != 0) {
            return 24;
        }
        if ((v %= 24) / 8 != 0) {
            return 8;
        }
        return 0;
    }
}

