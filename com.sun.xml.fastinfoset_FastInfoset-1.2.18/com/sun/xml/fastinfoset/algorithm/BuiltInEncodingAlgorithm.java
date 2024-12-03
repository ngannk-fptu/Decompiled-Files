/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.algorithm;

import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;

public abstract class BuiltInEncodingAlgorithm
implements EncodingAlgorithm {
    protected static final Pattern SPACE_PATTERN = Pattern.compile("\\s");

    public abstract int getPrimtiveLengthFromOctetLength(int var1) throws EncodingAlgorithmException;

    public abstract int getOctetLengthFromPrimitiveLength(int var1);

    public abstract void encodeToBytes(Object var1, int var2, int var3, byte[] var4, int var5);

    public void matchWhiteSpaceDelimnatedWords(CharBuffer cb, WordListener wl) {
        Matcher m = SPACE_PATTERN.matcher(cb);
        int i = 0;
        int s = 0;
        while (m.find()) {
            s = m.start();
            if (s != i) {
                wl.word(i, s);
            }
            i = m.end();
        }
        if (i != cb.length()) {
            wl.word(i, cb.length());
        }
    }

    public StringBuilder removeWhitespace(char[] ch, int start, int length) {
        int idx;
        StringBuilder buf = new StringBuilder();
        int firstNonWS = 0;
        for (idx = 0; idx < length; ++idx) {
            if (!Character.isWhitespace(ch[idx + start])) continue;
            if (firstNonWS < idx) {
                buf.append(ch, firstNonWS + start, idx - firstNonWS);
            }
            firstNonWS = idx + 1;
        }
        if (firstNonWS < idx) {
            buf.append(ch, firstNonWS + start, idx - firstNonWS);
        }
        return buf;
    }

    public static interface WordListener {
        public void word(int var1, int var2);
    }
}

