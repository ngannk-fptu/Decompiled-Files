/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

final class PDFDocEncoding {
    private static final char REPLACEMENT_CHARACTER = '\ufffd';
    private static final int[] CODE_TO_UNI = new int[256];
    private static final Map<Character, Integer> UNI_TO_CODE = new HashMap<Character, Integer>(256);

    private PDFDocEncoding() {
    }

    private static void set(int code, char unicode) {
        PDFDocEncoding.CODE_TO_UNI[code] = unicode;
        UNI_TO_CODE.put(Character.valueOf(unicode), code);
    }

    public static String toString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if ((b & 0xFF) >= CODE_TO_UNI.length) {
                sb.append('?');
                continue;
            }
            sb.append((char)CODE_TO_UNI[b & 0xFF]);
        }
        return sb.toString();
    }

    public static byte[] getBytes(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (char c : text.toCharArray()) {
            Integer code = UNI_TO_CODE.get(Character.valueOf(c));
            if (code == null) {
                out.write(0);
                continue;
            }
            out.write(code);
        }
        return out.toByteArray();
    }

    public static boolean containsChar(char character) {
        return UNI_TO_CODE.containsKey(Character.valueOf(character));
    }

    static {
        for (int i = 0; i < 256; ++i) {
            if (i > 23 && i < 32 || i > 126 && i < 161 || i == 173) continue;
            PDFDocEncoding.set(i, (char)i);
        }
        PDFDocEncoding.set(24, '\u02d8');
        PDFDocEncoding.set(25, '\u02c7');
        PDFDocEncoding.set(26, '\u02c6');
        PDFDocEncoding.set(27, '\u02d9');
        PDFDocEncoding.set(28, '\u02dd');
        PDFDocEncoding.set(29, '\u02db');
        PDFDocEncoding.set(30, '\u02da');
        PDFDocEncoding.set(31, '\u02dc');
        PDFDocEncoding.set(127, '\ufffd');
        PDFDocEncoding.set(128, '\u2022');
        PDFDocEncoding.set(129, '\u2020');
        PDFDocEncoding.set(130, '\u2021');
        PDFDocEncoding.set(131, '\u2026');
        PDFDocEncoding.set(132, '\u2014');
        PDFDocEncoding.set(133, '\u2013');
        PDFDocEncoding.set(134, '\u0192');
        PDFDocEncoding.set(135, '\u2044');
        PDFDocEncoding.set(136, '\u2039');
        PDFDocEncoding.set(137, '\u203a');
        PDFDocEncoding.set(138, '\u2212');
        PDFDocEncoding.set(139, '\u2030');
        PDFDocEncoding.set(140, '\u201e');
        PDFDocEncoding.set(141, '\u201c');
        PDFDocEncoding.set(142, '\u201d');
        PDFDocEncoding.set(143, '\u2018');
        PDFDocEncoding.set(144, '\u2019');
        PDFDocEncoding.set(145, '\u201a');
        PDFDocEncoding.set(146, '\u2122');
        PDFDocEncoding.set(147, '\ufb01');
        PDFDocEncoding.set(148, '\ufb02');
        PDFDocEncoding.set(149, '\u0141');
        PDFDocEncoding.set(150, '\u0152');
        PDFDocEncoding.set(151, '\u0160');
        PDFDocEncoding.set(152, '\u0178');
        PDFDocEncoding.set(153, '\u017d');
        PDFDocEncoding.set(154, '\u0131');
        PDFDocEncoding.set(155, '\u0142');
        PDFDocEncoding.set(156, '\u0153');
        PDFDocEncoding.set(157, '\u0161');
        PDFDocEncoding.set(158, '\u017e');
        PDFDocEncoding.set(159, '\ufffd');
        PDFDocEncoding.set(160, '\u20ac');
    }
}

