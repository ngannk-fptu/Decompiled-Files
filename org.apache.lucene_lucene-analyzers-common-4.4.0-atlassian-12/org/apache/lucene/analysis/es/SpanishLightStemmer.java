/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.es;

public class SpanishLightStemmer {
    public int stem(char[] s, int len) {
        if (len < 5) {
            return len;
        }
        block11: for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e0': 
                case '\u00e1': 
                case '\u00e2': 
                case '\u00e4': {
                    s[i] = 97;
                    continue block11;
                }
                case '\u00f2': 
                case '\u00f3': 
                case '\u00f4': 
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
                }
            }
        }
        switch (s[len - 1]) {
            case 'a': 
            case 'e': 
            case 'o': {
                return len - 1;
            }
            case 's': {
                if (s[len - 2] == 'e' && s[len - 3] == 's' && s[len - 4] == 'e') {
                    return len - 2;
                }
                if (s[len - 2] == 'e' && s[len - 3] == 'c') {
                    s[len - 3] = 122;
                    return len - 2;
                }
                if (s[len - 2] != 'o' && s[len - 2] != 'a' && s[len - 2] != 'e') break;
                return len - 2;
            }
        }
        return len;
    }
}

