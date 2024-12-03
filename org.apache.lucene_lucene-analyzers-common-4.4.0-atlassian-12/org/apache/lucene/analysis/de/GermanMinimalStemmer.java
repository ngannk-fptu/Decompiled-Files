/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.de;

public class GermanMinimalStemmer {
    public int stem(char[] s, int len) {
        if (len < 5) {
            return len;
        }
        block14: for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e4': {
                    s[i] = 97;
                    continue block14;
                }
                case '\u00f6': {
                    s[i] = 111;
                    continue block14;
                }
                case '\u00fc': {
                    s[i] = 117;
                }
            }
        }
        if (len > 6 && s[len - 3] == 'n' && s[len - 2] == 'e' && s[len - 1] == 'n') {
            return len - 3;
        }
        if (len > 5) {
            switch (s[len - 1]) {
                case 'n': {
                    if (s[len - 2] != 'e') break;
                    return len - 2;
                }
                case 'e': {
                    if (s[len - 2] != 's') break;
                    return len - 2;
                }
                case 's': {
                    if (s[len - 2] != 'e') break;
                    return len - 2;
                }
                case 'r': {
                    if (s[len - 2] != 'e') break;
                    return len - 2;
                }
            }
        }
        switch (s[len - 1]) {
            case 'e': 
            case 'n': 
            case 'r': 
            case 's': {
                return len - 1;
            }
        }
        return len;
    }
}

