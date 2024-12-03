/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.no;

import org.apache.lucene.analysis.util.StemmerUtil;

public class NorwegianMinimalStemmer {
    final boolean useBokmaal;
    final boolean useNynorsk;

    public NorwegianMinimalStemmer(int flags) {
        if (flags <= 0 || flags > 3) {
            throw new IllegalArgumentException("invalid flags");
        }
        this.useBokmaal = (flags & 1) != 0;
        this.useNynorsk = (flags & 2) != 0;
    }

    public int stem(char[] s, int len) {
        if (len > 4 && s[len - 1] == 's') {
            --len;
        }
        if (len > 5 && (StemmerUtil.endsWith(s, len, "ene") || StemmerUtil.endsWith(s, len, "ane") && this.useNynorsk)) {
            return len - 3;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "er") || StemmerUtil.endsWith(s, len, "en") || StemmerUtil.endsWith(s, len, "et") || StemmerUtil.endsWith(s, len, "ar") && this.useNynorsk)) {
            return len - 2;
        }
        if (len > 3) {
            switch (s[len - 1]) {
                case 'a': 
                case 'e': {
                    return len - 1;
                }
            }
        }
        return len;
    }
}

