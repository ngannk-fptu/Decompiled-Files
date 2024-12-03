/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.pt;

import org.apache.lucene.analysis.util.StemmerUtil;

public class PortugueseLightStemmer {
    public int stem(char[] s, int len) {
        if (len < 4) {
            return len;
        }
        if ((len = this.removeSuffix(s, len)) > 3 && s[len - 1] == 'a') {
            len = this.normFeminine(s, len);
        }
        if (len > 4) {
            switch (s[len - 1]) {
                case 'a': 
                case 'e': 
                case 'o': {
                    --len;
                }
            }
        }
        block11: for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e0': 
                case '\u00e1': 
                case '\u00e2': 
                case '\u00e3': 
                case '\u00e4': {
                    s[i] = 97;
                    continue block11;
                }
                case '\u00f2': 
                case '\u00f3': 
                case '\u00f4': 
                case '\u00f5': 
                case '\u00f6': {
                    s[i] = 111;
                    continue block11;
                }
                case '\u00e8': 
                case '\u00e9': 
                case '\u00ea': 
                case '\u00eb': {
                    s[i] = 101;
                    continue block11;
                }
                case '\u00f9': 
                case '\u00fa': 
                case '\u00fb': 
                case '\u00fc': {
                    s[i] = 117;
                    continue block11;
                }
                case '\u00ec': 
                case '\u00ed': 
                case '\u00ee': 
                case '\u00ef': {
                    s[i] = 105;
                    continue block11;
                }
                case '\u00e7': {
                    s[i] = 99;
                }
            }
        }
        return len;
    }

    private int removeSuffix(char[] s, int len) {
        if (len > 4 && StemmerUtil.endsWith(s, len, "es")) {
            switch (s[len - 3]) {
                case 'l': 
                case 'r': 
                case 's': 
                case 'z': {
                    return len - 2;
                }
            }
        }
        if (len > 3 && StemmerUtil.endsWith(s, len, "ns")) {
            s[len - 2] = 109;
            return len - 1;
        }
        if (len > 4 && (StemmerUtil.endsWith(s, len, "eis") || StemmerUtil.endsWith(s, len, "\u00e9is"))) {
            s[len - 3] = 101;
            s[len - 2] = 108;
            return len - 1;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "ais")) {
            s[len - 2] = 108;
            return len - 1;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "\u00f3is")) {
            s[len - 3] = 111;
            s[len - 2] = 108;
            return len - 1;
        }
        if (len > 4 && StemmerUtil.endsWith(s, len, "is")) {
            s[len - 1] = 108;
            return len;
        }
        if (len > 3 && (StemmerUtil.endsWith(s, len, "\u00f5es") || StemmerUtil.endsWith(s, len, "\u00e3es"))) {
            s[--len - 2] = 227;
            s[len - 1] = 111;
            return len;
        }
        if (len > 6 && StemmerUtil.endsWith(s, len, "mente")) {
            return len - 5;
        }
        if (len > 3 && s[len - 1] == 's') {
            return len - 1;
        }
        return len;
    }

    private int normFeminine(char[] s, int len) {
        if (len > 7 && (StemmerUtil.endsWith(s, len, "inha") || StemmerUtil.endsWith(s, len, "iaca") || StemmerUtil.endsWith(s, len, "eira"))) {
            s[len - 1] = 111;
            return len;
        }
        if (len > 6) {
            if (StemmerUtil.endsWith(s, len, "osa") || StemmerUtil.endsWith(s, len, "ica") || StemmerUtil.endsWith(s, len, "ida") || StemmerUtil.endsWith(s, len, "ada") || StemmerUtil.endsWith(s, len, "iva") || StemmerUtil.endsWith(s, len, "ama")) {
                s[len - 1] = 111;
                return len;
            }
            if (StemmerUtil.endsWith(s, len, "ona")) {
                s[len - 3] = 227;
                s[len - 2] = 111;
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "ora")) {
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "esa")) {
                s[len - 3] = 234;
                return len - 1;
            }
            if (StemmerUtil.endsWith(s, len, "na")) {
                s[len - 1] = 111;
                return len;
            }
        }
        return len;
    }
}

