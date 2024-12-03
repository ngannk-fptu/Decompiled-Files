/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFDocCharsetEncoder;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;

public class PDFStringUtil {
    static final char[] PDF_DOC_ENCODING_MAP = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u02d8', '\u02c7', '\u02c6', '\u02d9', '\u02dd', '\u02db', '\u02da', '\u02dc', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\ufffd', '\u2022', '\u2020', '\u2021', '\u2026', '\u2014', '\u2013', '\u0192', '\u2044', '\u2039', '\u203a', '\u2212', '\u2030', '\u201e', '\u201c', '\u201d', '\u2018', '\u2019', '\u201a', '\u2122', '\ufb01', '\ufb02', '\u0141', '\u0152', '\u0160', '\u0178', '\u017d', '\u0131', '\u0142', '\u0153', '\u0161', '\u017e', '\ufffd', '\u20ac', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a7', '\u00a8', '\u00a9', '\u00aa', '\u00ab', '\u00ac', '\ufffd', '\u00ae', '\u00af', '\u00b0', '\u00b1', '\u00b2', '\u00b3', '\u00b4', '\u00b5', '\u00b6', '\u00b7', '\u00b8', '\u00b9', '\u00ba', '\u00bb', '\u00bc', '\u00bd', '\u00be', '\u00bf', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'};

    public static String asTextString(String basicString) {
        if (basicString == null) {
            return null;
        }
        if (basicString.length() >= 2 && basicString.charAt(0) == '\u00fe' && basicString.charAt(1) == '\u00ff') {
            return PDFStringUtil.asUTF16BEEncoded(basicString);
        }
        return PDFStringUtil.asPDFDocEncoded(basicString);
    }

    public static String asPDFDocEncoded(String basicString) {
        StringBuilder buf = new StringBuilder(basicString.length());
        for (int i = 0; i < basicString.length(); ++i) {
            char c = PDF_DOC_ENCODING_MAP[basicString.charAt(i) & 0xFF];
            buf.append(c);
        }
        return buf.toString();
    }

    public byte[] toPDFDocEncoded(String string) throws CharacterCodingException {
        return new PDFDocCharsetEncoder().encode(CharBuffer.wrap(string)).array();
    }

    public static String asUTF16BEEncoded(String basicString) {
        try {
            return new String(PDFStringUtil.asBytes(basicString), 2, basicString.length() - 2, "UTF-16BE");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("No UTF-16BE charset!");
        }
    }

    public static byte[] asBytes(String basicString) {
        byte[] b = new byte[basicString.length()];
        for (int i = 0; i < b.length; ++i) {
            b[i] = (byte)basicString.charAt(i);
        }
        return b;
    }

    public static String asBasicString(byte[] bytes, int offset, int length) {
        char[] c = new char[length];
        for (int i = 0; i < c.length; ++i) {
            c[i] = (char)bytes[i + offset];
        }
        return new String(c);
    }

    public static String asBasicString(byte[] bytes) {
        return PDFStringUtil.asBasicString(bytes, 0, bytes.length);
    }
}

