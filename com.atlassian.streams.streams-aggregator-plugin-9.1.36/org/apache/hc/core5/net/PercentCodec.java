/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.net;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class PercentCodec {
    static final BitSet GEN_DELIMS;
    static final BitSet SUB_DELIMS;
    static final BitSet UNRESERVED;
    static final BitSet URIC;
    private static final int RADIX = 16;

    static void encode(StringBuilder buf, CharSequence content, Charset charset, BitSet safechars, boolean blankAsPlus) {
        if (content == null) {
            return;
        }
        CharBuffer cb = CharBuffer.wrap(content);
        ByteBuffer bb = (charset != null ? charset : StandardCharsets.UTF_8).encode(cb);
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xFF;
            if (safechars.get(b)) {
                buf.append((char)b);
                continue;
            }
            if (blankAsPlus && b == 32) {
                buf.append("+");
                continue;
            }
            buf.append("%");
            char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
            char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
            buf.append(hex1);
            buf.append(hex2);
        }
    }

    static void encode(StringBuilder buf, CharSequence content, Charset charset, boolean blankAsPlus) {
        PercentCodec.encode(buf, content, charset, UNRESERVED, blankAsPlus);
    }

    public static void encode(StringBuilder buf, CharSequence content, Charset charset) {
        PercentCodec.encode(buf, content, charset, UNRESERVED, false);
    }

    public static String encode(CharSequence content, Charset charset) {
        if (content == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        PercentCodec.encode(buf, content, charset, UNRESERVED, false);
        return buf.toString();
    }

    static String decode(CharSequence content, Charset charset, boolean plusAsBlank) {
        if (content == null) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.allocate(content.length());
        CharBuffer cb = CharBuffer.wrap(content);
        while (cb.hasRemaining()) {
            char c = cb.get();
            if (c == '%' && cb.remaining() >= 2) {
                char uc = cb.get();
                char lc = cb.get();
                int u = Character.digit(uc, 16);
                int l = Character.digit(lc, 16);
                if (u != -1 && l != -1) {
                    bb.put((byte)((u << 4) + l));
                    continue;
                }
                bb.put((byte)37);
                bb.put((byte)uc);
                bb.put((byte)lc);
                continue;
            }
            if (plusAsBlank && c == '+') {
                bb.put((byte)32);
                continue;
            }
            bb.put((byte)c);
        }
        bb.flip();
        return (charset != null ? charset : StandardCharsets.UTF_8).decode(bb).toString();
    }

    public static String decode(CharSequence content, Charset charset) {
        return PercentCodec.decode(content, charset, false);
    }

    static {
        int i;
        GEN_DELIMS = new BitSet(256);
        SUB_DELIMS = new BitSet(256);
        UNRESERVED = new BitSet(256);
        URIC = new BitSet(256);
        GEN_DELIMS.set(58);
        GEN_DELIMS.set(47);
        GEN_DELIMS.set(63);
        GEN_DELIMS.set(35);
        GEN_DELIMS.set(91);
        GEN_DELIMS.set(93);
        GEN_DELIMS.set(64);
        SUB_DELIMS.set(33);
        SUB_DELIMS.set(36);
        SUB_DELIMS.set(38);
        SUB_DELIMS.set(39);
        SUB_DELIMS.set(40);
        SUB_DELIMS.set(41);
        SUB_DELIMS.set(42);
        SUB_DELIMS.set(43);
        SUB_DELIMS.set(44);
        SUB_DELIMS.set(59);
        SUB_DELIMS.set(61);
        for (i = 97; i <= 122; ++i) {
            UNRESERVED.set(i);
        }
        for (i = 65; i <= 90; ++i) {
            UNRESERVED.set(i);
        }
        for (i = 48; i <= 57; ++i) {
            UNRESERVED.set(i);
        }
        UNRESERVED.set(45);
        UNRESERVED.set(46);
        UNRESERVED.set(95);
        UNRESERVED.set(126);
        URIC.or(SUB_DELIMS);
        URIC.or(UNRESERVED);
    }
}

