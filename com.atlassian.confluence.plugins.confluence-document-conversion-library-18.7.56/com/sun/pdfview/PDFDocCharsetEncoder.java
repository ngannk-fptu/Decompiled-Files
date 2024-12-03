/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFStringUtil;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;

public class PDFDocCharsetEncoder
extends CharsetEncoder {
    static final boolean[] IDENT_PDF_DOC_ENCODING_MAP = new boolean[256];
    static final Map<Character, Byte> EXTENDED_TO_PDF_DOC_ENCODING_MAP = new HashMap<Character, Byte>();

    public static boolean isIdentityEncoding(char ch) {
        return ch >= '\u0000' && ch <= '\u00ff' && IDENT_PDF_DOC_ENCODING_MAP[ch];
    }

    public PDFDocCharsetEncoder() {
        super(null, 1.0f, 1.0f);
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        while (in.remaining() > 0) {
            if (out.remaining() < 1) {
                return CoderResult.OVERFLOW;
            }
            char c = in.get();
            if (c >= '\u0000' && c < '\u0100' && IDENT_PDF_DOC_ENCODING_MAP[c]) {
                out.put((byte)c);
                continue;
            }
            Byte mapped = EXTENDED_TO_PDF_DOC_ENCODING_MAP.get(Character.valueOf(c));
            if (mapped != null) {
                out.put(mapped);
                continue;
            }
            return CoderResult.unmappableForLength(1);
        }
        return CoderResult.UNDERFLOW;
    }

    @Override
    public boolean isLegalReplacement(byte[] repl) {
        return true;
    }

    static {
        for (byte i = 0; i < PDFStringUtil.PDF_DOC_ENCODING_MAP.length; i = (byte)(i + 1)) {
            boolean identical;
            char c = PDFStringUtil.PDF_DOC_ENCODING_MAP[i];
            PDFDocCharsetEncoder.IDENT_PDF_DOC_ENCODING_MAP[i] = identical = c == i;
            if (identical) continue;
            EXTENDED_TO_PDF_DOC_ENCODING_MAP.put(Character.valueOf(c), i);
        }
    }
}

