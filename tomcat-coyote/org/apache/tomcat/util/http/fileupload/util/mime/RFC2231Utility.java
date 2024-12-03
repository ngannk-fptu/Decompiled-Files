/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload.util.mime;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public final class RFC2231Utility {
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final byte MASK = 127;
    private static final int MASK_128 = 128;
    private static final byte[] HEX_DECODE = new byte[128];

    private RFC2231Utility() {
    }

    public static boolean hasEncodedValue(String paramName) {
        if (paramName != null) {
            return paramName.lastIndexOf(42) == paramName.length() - 1;
        }
        return false;
    }

    public static String stripDelimiter(String paramName) {
        if (RFC2231Utility.hasEncodedValue(paramName)) {
            StringBuilder paramBuilder = new StringBuilder(paramName);
            paramBuilder.deleteCharAt(paramName.lastIndexOf(42));
            return paramBuilder.toString();
        }
        return paramName;
    }

    public static String decodeText(String encodedText) throws UnsupportedEncodingException {
        int langDelimitStart = encodedText.indexOf(39);
        if (langDelimitStart == -1) {
            return encodedText;
        }
        String mimeCharset = encodedText.substring(0, langDelimitStart);
        int langDelimitEnd = encodedText.indexOf(39, langDelimitStart + 1);
        if (langDelimitEnd == -1) {
            return encodedText;
        }
        byte[] bytes = RFC2231Utility.fromHex(encodedText.substring(langDelimitEnd + 1));
        return new String(bytes, RFC2231Utility.getJavaCharset(mimeCharset));
    }

    private static byte[] fromHex(String text) {
        int shift = 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(text.length());
        int i = 0;
        while (i < text.length()) {
            char c;
            if ((c = text.charAt(i++)) == '%') {
                if (i > text.length() - 2) break;
                byte b1 = HEX_DECODE[text.charAt(i++) & 0x7F];
                byte b2 = HEX_DECODE[text.charAt(i++) & 0x7F];
                out.write(b1 << 4 | b2);
                continue;
            }
            out.write((byte)c);
        }
        return out.toByteArray();
    }

    private static String getJavaCharset(String mimeCharset) {
        return mimeCharset;
    }

    static {
        for (int i = 0; i < HEX_DIGITS.length; ++i) {
            RFC2231Utility.HEX_DECODE[RFC2231Utility.HEX_DIGITS[i]] = (byte)i;
            RFC2231Utility.HEX_DECODE[Character.toLowerCase((char)RFC2231Utility.HEX_DIGITS[i])] = (byte)i;
        }
    }
}

