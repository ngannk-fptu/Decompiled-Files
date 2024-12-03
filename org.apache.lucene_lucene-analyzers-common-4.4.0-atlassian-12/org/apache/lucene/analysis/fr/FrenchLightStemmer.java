/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.fr;

import org.apache.lucene.analysis.util.StemmerUtil;

public class FrenchLightStemmer {
    public int stem(char[] s, int len) {
        if (len > 5 && s[len - 1] == 'x') {
            if (s[len - 3] == 'a' && s[len - 2] == 'u' && s[len - 4] != 'e') {
                s[len - 2] = 108;
            }
            --len;
        }
        if (len > 3 && s[len - 1] == 'x') {
            --len;
        }
        if (len > 3 && s[len - 1] == 's') {
            --len;
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "issement")) {
            s[(len -= 6) - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "issant")) {
            s[(len -= 4) - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "ement")) {
            if ((len -= 4) > 3 && StemmerUtil.endsWith(s, len, "ive")) {
                s[--len - 1] = 102;
            }
            return this.norm(s, len);
        }
        if (len > 11 && StemmerUtil.endsWith(s, len, "ficatrice")) {
            s[(len -= 5) - 2] = 101;
            s[len - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 10 && StemmerUtil.endsWith(s, len, "ficateur")) {
            s[(len -= 4) - 2] = 101;
            s[len - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "catrice")) {
            s[(len -= 3) - 4] = 113;
            s[len - 3] = 117;
            s[len - 2] = 101;
            return this.norm(s, len);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "cateur")) {
            s[(len -= 2) - 4] = 113;
            s[len - 3] = 117;
            s[len - 2] = 101;
            s[len - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "atrice")) {
            s[(len -= 4) - 2] = 101;
            s[len - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "ateur")) {
            s[(len -= 3) - 2] = 101;
            s[len - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "trice")) {
            s[--len - 3] = 101;
            s[len - 2] = 117;
            s[len - 1] = 114;
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "i\u00e8me")) {
            return this.norm(s, len - 4);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "teuse")) {
            s[(len -= 2) - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "teur")) {
            s[--len - 1] = 114;
            return this.norm(s, len);
        }
        if (len > 5 && StemmerUtil.endsWith(s, len, "euse")) {
            return this.norm(s, len - 2);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "\u00e8re")) {
            s[--len - 2] = 101;
            return this.norm(s, len);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "ive")) {
            s[--len - 1] = 102;
            return this.norm(s, len);
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "folle") || StemmerUtil.endsWith(s, len, "molle"))) {
            s[(len -= 2) - 1] = 117;
            return this.norm(s, len);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "nnelle")) {
            return this.norm(s, len - 5);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "nnel")) {
            return this.norm(s, len - 3);
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u00e8te")) {
            s[--len - 2] = 101;
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "ique")) {
            len -= 4;
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "esse")) {
            return this.norm(s, len - 3);
        }
        if (len > 7 && StemmerUtil.endsWith(s, len, "inage")) {
            return this.norm(s, len - 3);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "isation")) {
            if ((len -= 7) > 5 && StemmerUtil.endsWith(s, len, "ual")) {
                s[len - 2] = 101;
            }
            return this.norm(s, len);
        }
        if (len > 9 && StemmerUtil.endsWith(s, len, "isateur")) {
            return this.norm(s, len - 7);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "ation")) {
            return this.norm(s, len - 5);
        }
        if (len > 8 && StemmerUtil.endsWith(s, len, "ition")) {
            return this.norm(s, len - 5);
        }
        return this.norm(s, len);
    }

    private int norm(char[] s, int len) {
        if (len > 4) {
            block8: for (int i = 0; i < len; ++i) {
                switch (s[i]) {
                    case '\u00e0': 
                    case '\u00e1': 
                    case '\u00e2': {
                        s[i] = 97;
                        continue block8;
                    }
                    case '\u00f4': {
                        s[i] = 111;
                        continue block8;
                    }
                    case '\u00e8': 
                    case '\u00e9': 
                    case '\u00ea': {
                        s[i] = 101;
                        continue block8;
                    }
                    case '\u00f9': 
                    case '\u00fb': {
                        s[i] = 117;
                        continue block8;
                    }
                    case '\u00ee': {
                        s[i] = 105;
                        continue block8;
                    }
                    case '\u00e7': {
                        s[i] = 99;
                    }
                }
            }
            char ch = s[0];
            for (int i = 1; i < len; ++i) {
                if (s[i] == ch && Character.isLetter(ch)) {
                    len = StemmerUtil.delete(s, i--, len);
                    continue;
                }
                ch = s[i];
            }
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "ie")) {
            len -= 2;
        }
        if (len > 4) {
            if (s[len - 1] == 'r') {
                --len;
            }
            if (s[len - 1] == 'e') {
                --len;
            }
            if (s[len - 1] == 'e') {
                --len;
            }
            if (s[len - 1] == s[len - 2] && Character.isLetter(s[len - 1])) {
                --len;
            }
        }
        return len;
    }
}

