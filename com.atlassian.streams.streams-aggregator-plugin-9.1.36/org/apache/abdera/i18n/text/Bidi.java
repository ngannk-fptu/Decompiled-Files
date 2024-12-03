/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.text.AttributedString;
import java.util.Arrays;
import org.apache.abdera.i18n.rfc4646.Lang;

public class Bidi {
    private static final String[] RTL_LANGS = new String[]{"ar", "dv", "fa", "he", "ps", "syr", "ur", "yi"};
    private static final String[] RTL_SCRIPTS = new String[]{"arab", "avst", "hebr", "hung", "lydi", "mand", "mani", "mero", "mong", "nkoo", "orkh", "phlv", "phnx", "samr", "syrc", "syre", "syrj", "syrn", "tfng", "thaa"};
    private static final String[] RTL_ENCODINGS = new String[]{"iso-8859-6", "iso-8859-6-bidi", "iso-8859-6-i", "iso-ir-127", "ecma-114", "asmo-708", "arabic", "csisolatinarabic", "windows-1256", "ibm-864", "macarabic", "macfarsi", "iso-8859-8-i", "iso-8859-8-bidi", "windows-1255", "iso-8859-8", "ibm-862", "machebrew", "asmo-449", "iso-9036", "arabic7", "iso-ir-89", "csiso89asmo449", "iso-unicode-ibm-1264", "csunicodeibm1264", "iso_8859-8:1988", "iso-ir-138", "hebrew", "csisolatinhebrew", "iso-unicode-ibm-1265", "csunicodeibm1265", "cp862", "862", "cspc862latinhebrew"};

    public static Direction guessDirectionFromLanguage(Lang lang) {
        String script;
        if (lang.getScript() != null && Arrays.binarySearch(RTL_SCRIPTS, (script = lang.getScript().getName()).toLowerCase()) > -1) {
            return Direction.RTL;
        }
        String primary = lang.getLanguage().getName();
        if (Arrays.binarySearch(RTL_LANGS, primary.toLowerCase()) > -1) {
            return Direction.RTL;
        }
        return Direction.UNSPECIFIED;
    }

    public static Direction guessDirectionFromEncoding(String charset) {
        if (charset == null) {
            return Direction.UNSPECIFIED;
        }
        charset = charset.replace('_', '-');
        Arrays.sort(RTL_ENCODINGS);
        if (Arrays.binarySearch(RTL_ENCODINGS, charset.toLowerCase()) > -1) {
            return Direction.RTL;
        }
        return Direction.UNSPECIFIED;
    }

    public static Direction guessDirectionFromTextProperties(String text) {
        if (text != null && text.length() > 0) {
            if (text.charAt(0) == '\u200f') {
                return Direction.RTL;
            }
            if (text.charAt(0) == '\u200e') {
                return Direction.LTR;
            }
            int c = 0;
            for (int n = 0; n < text.length(); ++n) {
                char ch = text.charAt(n);
                if (java.text.Bidi.requiresBidi(new char[]{ch}, 0, 1)) {
                    ++c;
                    continue;
                }
                --c;
            }
            return c > 0 ? Direction.RTL : Direction.LTR;
        }
        return Direction.UNSPECIFIED;
    }

    public static Direction guessDirectionFromJavaBidi(String text) {
        if (text != null) {
            AttributedString s = new AttributedString(text);
            java.text.Bidi bidi = new java.text.Bidi(s.getIterator());
            return bidi.baseIsLeftToRight() ? Direction.LTR : Direction.RTL;
        }
        return Direction.UNSPECIFIED;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Direction {
        UNSPECIFIED,
        LTR,
        RTL;

    }
}

