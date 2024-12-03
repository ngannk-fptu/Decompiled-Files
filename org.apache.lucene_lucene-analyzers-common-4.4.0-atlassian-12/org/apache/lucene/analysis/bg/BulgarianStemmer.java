/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.bg;

import org.apache.lucene.analysis.util.StemmerUtil;

public class BulgarianStemmer {
    public int stem(char[] s, int len) {
        if (len < 4) {
            return len;
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "\u0438\u0449\u0430")) {
            return len - 3;
        }
        len = this.removeArticle(s, len);
        if ((len = this.removePlural(s, len)) > 3) {
            if (StemmerUtil.endsWith(s, len, "\u044f")) {
                --len;
            }
            if (StemmerUtil.endsWith(s, len, "\u0430") || StemmerUtil.endsWith(s, len, "\u043e") || StemmerUtil.endsWith(s, len, "\u0435")) {
                --len;
            }
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u0435\u043d")) {
            s[len - 2] = 1085;
            --len;
        }
        if (len > 5 && s[len - 2] == '\u044a') {
            s[len - 2] = s[len - 1];
            --len;
        }
        return len;
    }

    private int removeArticle(char[] s, int len) {
        if (len > 6 && StemmerUtil.endsWith(s, len, "\u0438\u044f\u0442")) {
            return len - 3;
        }
        if (len > 5 && (StemmerUtil.endsWith(s, len, "\u044a\u0442") || StemmerUtil.endsWith(s, len, "\u0442\u043e") || StemmerUtil.endsWith(s, len, "\u0442\u0435") || StemmerUtil.endsWith(s, len, "\u0442\u0430") || StemmerUtil.endsWith(s, len, "\u0438\u044f"))) {
            return len - 2;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u044f\u0442")) {
            return len - 2;
        }
        return len;
    }

    private int removePlural(char[] s, int len) {
        if (len > 6) {
            if (StemmerUtil.endsWith(s, len, "\u043e\u0432\u0446\u0438")) {
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "\u043e\u0432\u0435")) {
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "\u0435\u0432\u0435")) {
                s[len - 3] = 1081;
                return len - 2;
            }
        }
        if (len > 5) {
            if (StemmerUtil.endsWith(s, len, "\u0438\u0449\u0430")) {
                return len - 3;
            }
            if (StemmerUtil.endsWith(s, len, "\u0442\u0430")) {
                return len - 2;
            }
            if (StemmerUtil.endsWith(s, len, "\u0446\u0438")) {
                s[len - 2] = 1082;
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "\u0437\u0438")) {
                s[len - 2] = 1075;
                return len - 1;
            }
            if (s[len - 3] == '\u0435' && s[len - 1] == '\u0438') {
                s[len - 3] = 1103;
                return len - 1;
            }
        }
        if (len > 4) {
            if (StemmerUtil.endsWith(s, len, "\u0441\u0438")) {
                s[len - 2] = 1093;
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "\u0438")) {
                return len - 1;
            }
        }
        return len;
    }
}

