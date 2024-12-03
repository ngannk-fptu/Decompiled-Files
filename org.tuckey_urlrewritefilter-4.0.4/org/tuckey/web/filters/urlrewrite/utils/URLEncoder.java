/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class URLEncoder {
    public static final BitSet MARK = new BitSet();
    public static final BitSet LOW_ALPHA;
    public static final BitSet UP_ALPHA;
    public static final BitSet ALPHA;
    public static final BitSet DIGIT;
    public static final BitSet ALPHANUM;
    public static final BitSet UNRESERVED;
    public static final BitSet PCHAR;

    public static String encodePathParam(String pathParam, String charset) throws UnsupportedEncodingException {
        return URLEncoder.encodePathSegment(pathParam, charset);
    }

    public static String encodePathSegment(String pathSegment, String charset) throws UnsupportedEncodingException {
        if (pathSegment == null) {
            return null;
        }
        StringBuffer encoded = new StringBuffer(pathSegment.length() * 3);
        char[] toEncode = pathSegment.toCharArray();
        for (int i = 0; i < toEncode.length; ++i) {
            char c = toEncode[i];
            if (PCHAR.get(c)) {
                encoded.append(c);
                continue;
            }
            byte[] bytes = String.valueOf(c).getBytes(charset);
            for (int j = 0; j < bytes.length; ++j) {
                byte b = bytes[j];
                int u8 = b & 0xFF;
                encoded.append("%");
                if (u8 < 16) {
                    encoded.append("0");
                }
                encoded.append(Integer.toHexString(u8));
            }
        }
        return encoded.toString();
    }

    static {
        MARK.set(45);
        MARK.set(95);
        MARK.set(46);
        MARK.set(33);
        MARK.set(126);
        MARK.set(42);
        MARK.set(39);
        MARK.set(40);
        MARK.set(41);
        LOW_ALPHA = new BitSet();
        LOW_ALPHA.set(97);
        LOW_ALPHA.set(98);
        LOW_ALPHA.set(99);
        LOW_ALPHA.set(100);
        LOW_ALPHA.set(101);
        LOW_ALPHA.set(102);
        LOW_ALPHA.set(103);
        LOW_ALPHA.set(104);
        LOW_ALPHA.set(105);
        LOW_ALPHA.set(106);
        LOW_ALPHA.set(107);
        LOW_ALPHA.set(108);
        LOW_ALPHA.set(109);
        LOW_ALPHA.set(110);
        LOW_ALPHA.set(111);
        LOW_ALPHA.set(112);
        LOW_ALPHA.set(113);
        LOW_ALPHA.set(114);
        LOW_ALPHA.set(115);
        LOW_ALPHA.set(116);
        LOW_ALPHA.set(117);
        LOW_ALPHA.set(118);
        LOW_ALPHA.set(119);
        LOW_ALPHA.set(120);
        LOW_ALPHA.set(121);
        LOW_ALPHA.set(122);
        UP_ALPHA = new BitSet();
        UP_ALPHA.set(65);
        UP_ALPHA.set(66);
        UP_ALPHA.set(67);
        UP_ALPHA.set(68);
        UP_ALPHA.set(69);
        UP_ALPHA.set(70);
        UP_ALPHA.set(71);
        UP_ALPHA.set(72);
        UP_ALPHA.set(73);
        UP_ALPHA.set(74);
        UP_ALPHA.set(75);
        UP_ALPHA.set(76);
        UP_ALPHA.set(77);
        UP_ALPHA.set(78);
        UP_ALPHA.set(79);
        UP_ALPHA.set(80);
        UP_ALPHA.set(81);
        UP_ALPHA.set(82);
        UP_ALPHA.set(83);
        UP_ALPHA.set(84);
        UP_ALPHA.set(85);
        UP_ALPHA.set(86);
        UP_ALPHA.set(87);
        UP_ALPHA.set(88);
        UP_ALPHA.set(89);
        UP_ALPHA.set(90);
        ALPHA = new BitSet();
        ALPHA.or(LOW_ALPHA);
        ALPHA.or(UP_ALPHA);
        DIGIT = new BitSet();
        DIGIT.set(48);
        DIGIT.set(49);
        DIGIT.set(50);
        DIGIT.set(51);
        DIGIT.set(52);
        DIGIT.set(53);
        DIGIT.set(54);
        DIGIT.set(55);
        DIGIT.set(56);
        DIGIT.set(57);
        ALPHANUM = new BitSet();
        ALPHANUM.or(ALPHA);
        ALPHANUM.or(DIGIT);
        UNRESERVED = new BitSet();
        UNRESERVED.or(ALPHANUM);
        UNRESERVED.or(MARK);
        PCHAR = new BitSet();
        PCHAR.or(UNRESERVED);
        PCHAR.set(58);
        PCHAR.set(64);
        PCHAR.set(38);
        PCHAR.set(61);
        PCHAR.set(43);
        PCHAR.set(36);
        PCHAR.set(44);
    }
}

