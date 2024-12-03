/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class VelocityHelper {
    private static final BitSet dontNeedEncoding;
    private static final int caseDiff = 32;

    public static String encode(String str, String encoding) throws UnsupportedEncodingException {
        boolean needToChange = false;
        boolean wroteUnencodedChar = false;
        int maxBytesPerChar = 10;
        StringBuilder out = new StringBuilder(str.length());
        ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)buf, encoding));
        for (int i = 0; i < str.length(); ++i) {
            byte[] ba;
            int c = str.charAt(i);
            if (dontNeedEncoding.get(c)) {
                if (c == 32) {
                    c = 43;
                    needToChange = true;
                }
                out.append((char)c);
                wroteUnencodedChar = true;
                continue;
            }
            try {
                char d;
                if (wroteUnencodedChar) {
                    writer = new BufferedWriter(new OutputStreamWriter((OutputStream)buf, encoding));
                    wroteUnencodedChar = false;
                }
                writer.write(c);
                if (c >= 55296 && c <= 56319 && i + 1 < str.length() && (d = str.charAt(i + 1)) >= '\udc00' && d <= '\udfff') {
                    writer.write(d);
                    ++i;
                }
                writer.flush();
            }
            catch (IOException e) {
                buf.reset();
                continue;
            }
            for (byte b : ba = buf.toByteArray()) {
                out.append('%');
                char ch = Character.forDigit(b >> 4 & 0xF, 16);
                if (Character.isLetter(ch)) {
                    ch = (char)(ch - 32);
                }
                out.append(ch);
                ch = Character.forDigit(b & 0xF, 16);
                if (Character.isLetter(ch)) {
                    ch = (char)(ch - 32);
                }
                out.append(ch);
            }
            buf.reset();
            needToChange = true;
        }
        return needToChange ? out.toString() : str;
    }

    static {
        int i;
        dontNeedEncoding = new BitSet(256);
        for (i = 97; i <= 122; ++i) {
            dontNeedEncoding.set(i);
        }
        for (i = 65; i <= 90; ++i) {
            dontNeedEncoding.set(i);
        }
        for (i = 48; i <= 57; ++i) {
            dontNeedEncoding.set(i);
        }
        dontNeedEncoding.set(32);
        dontNeedEncoding.set(45);
        dontNeedEncoding.set(95);
        dontNeedEncoding.set(46);
        dontNeedEncoding.set(42);
    }
}

