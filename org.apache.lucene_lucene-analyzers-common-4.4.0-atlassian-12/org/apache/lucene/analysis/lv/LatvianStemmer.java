/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.lv;

import org.apache.lucene.analysis.util.StemmerUtil;

public class LatvianStemmer {
    static final Affix[] affixes = new Affix[]{new Affix("ajiem", 3, false), new Affix("ajai", 3, false), new Affix("ajam", 2, false), new Affix("aj\u0101m", 2, false), new Affix("ajos", 2, false), new Affix("aj\u0101s", 2, false), new Affix("iem", 2, true), new Affix("aj\u0101", 2, false), new Affix("ais", 2, false), new Affix("ai", 2, false), new Affix("ei", 2, false), new Affix("\u0101m", 1, false), new Affix("am", 1, false), new Affix("\u0113m", 1, false), new Affix("\u012bm", 1, false), new Affix("im", 1, false), new Affix("um", 1, false), new Affix("us", 1, true), new Affix("as", 1, false), new Affix("\u0101s", 1, false), new Affix("es", 1, false), new Affix("os", 1, true), new Affix("ij", 1, false), new Affix("\u012bs", 1, false), new Affix("\u0113s", 1, false), new Affix("is", 1, false), new Affix("ie", 1, false), new Affix("u", 1, true), new Affix("a", 1, true), new Affix("i", 1, true), new Affix("e", 1, false), new Affix("\u0101", 1, false), new Affix("\u0113", 1, false), new Affix("\u012b", 1, false), new Affix("\u016b", 1, false), new Affix("o", 1, false), new Affix("s", 0, false), new Affix("\u0161", 0, false)};

    public int stem(char[] s, int len) {
        int numVowels = this.numVowels(s, len);
        for (int i = 0; i < affixes.length; ++i) {
            Affix affix = affixes[i];
            if (numVowels <= affix.vc || len < affix.affix.length + 3 || !StemmerUtil.endsWith(s, len, affix.affix)) continue;
            return affix.palatalizes ? this.unpalatalize(s, len) : (len -= affix.affix.length);
        }
        return len;
    }

    private int unpalatalize(char[] s, int len) {
        if (s[len] == 'u') {
            if (StemmerUtil.endsWith(s, len, "k\u0161")) {
                s[++len - 2] = 115;
                s[len - 1] = 116;
                return len;
            }
            if (StemmerUtil.endsWith(s, len, "\u0146\u0146")) {
                s[len - 2] = 110;
                s[len - 1] = 110;
                return len;
            }
        }
        if (StemmerUtil.endsWith(s, len, "pj") || StemmerUtil.endsWith(s, len, "bj") || StemmerUtil.endsWith(s, len, "mj") || StemmerUtil.endsWith(s, len, "vj")) {
            return len - 1;
        }
        if (StemmerUtil.endsWith(s, len, "\u0161\u0146")) {
            s[len - 2] = 115;
            s[len - 1] = 110;
            return len;
        }
        if (StemmerUtil.endsWith(s, len, "\u017e\u0146")) {
            s[len - 2] = 122;
            s[len - 1] = 110;
            return len;
        }
        if (StemmerUtil.endsWith(s, len, "\u0161\u013c")) {
            s[len - 2] = 115;
            s[len - 1] = 108;
            return len;
        }
        if (StemmerUtil.endsWith(s, len, "\u017e\u013c")) {
            s[len - 2] = 122;
            s[len - 1] = 108;
            return len;
        }
        if (StemmerUtil.endsWith(s, len, "\u013c\u0146")) {
            s[len - 2] = 108;
            s[len - 1] = 110;
            return len;
        }
        if (StemmerUtil.endsWith(s, len, "\u013c\u013c")) {
            s[len - 2] = 108;
            s[len - 1] = 108;
            return len;
        }
        if (s[len - 1] == '\u010d') {
            s[len - 1] = 99;
            return len;
        }
        if (s[len - 1] == '\u013c') {
            s[len - 1] = 108;
            return len;
        }
        if (s[len - 1] == '\u0146') {
            s[len - 1] = 110;
            return len;
        }
        return len;
    }

    private int numVowels(char[] s, int len) {
        int n = 0;
        for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case 'a': 
                case 'e': 
                case 'i': 
                case 'o': 
                case 'u': 
                case '\u0101': 
                case '\u0113': 
                case '\u012b': 
                case '\u016b': {
                    ++n;
                }
            }
        }
        return n;
    }

    static class Affix {
        char[] affix;
        int vc;
        boolean palatalizes;

        Affix(String affix, int vc, boolean palatalizes) {
            this.affix = affix.toCharArray();
            this.vc = vc;
            this.palatalizes = palatalizes;
        }
    }
}

