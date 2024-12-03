/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.de;

public class GermanLightStemmer {
    public int stem(char[] s, int len) {
        block6: for (int i = 0; i < len; ++i) {
            switch (s[i]) {
                case '\u00e0': 
                case '\u00e1': 
                case '\u00e2': 
                case '\u00e4': {
                    s[i] = 97;
                    continue block6;
                }
                case '\u00f2': 
                case '\u00f3': 
                case '\u00f4': 
                case '\u00f6': {
                    s[i] = 111;
                    continue block6;
                }
                case '\u00ec': 
                case '\u00ed': 
                case '\u00ee': 
                case '\u00ef': {
                    s[i] = 105;
                    continue block6;
                }
                case '\u00f9': 
                case '\u00fa': 
                case '\u00fb': 
                case '\u00fc': {
                    s[i] = 117;
                }
            }
        }
        len = this.step1(s, len);
        return this.step2(s, len);
    }

    private boolean stEnding(char ch) {
        switch (ch) {
            case 'b': 
            case 'd': 
            case 'f': 
            case 'g': 
            case 'h': 
            case 'k': 
            case 'l': 
            case 'm': 
            case 'n': 
            case 't': {
                return true;
            }
        }
        return false;
    }

    private int step1(char[] s, int len) {
        if (len > 5 && s[len - 3] == 'e' && s[len - 2] == 'r' && s[len - 1] == 'n') {
            return len - 3;
        }
        if (len > 4 && s[len - 2] == 'e') {
            switch (s[len - 1]) {
                case 'm': 
                case 'n': 
                case 'r': 
                case 's': {
                    return len - 2;
                }
            }
        }
        if (len > 3 && s[len - 1] == 'e') {
            return len - 1;
        }
        if (len > 3 && s[len - 1] == 's' && this.stEnding(s[len - 2])) {
            return len - 1;
        }
        return len;
    }

    private int step2(char[] s, int len) {
        if (len > 5 && s[len - 3] == 'e' && s[len - 2] == 's' && s[len - 1] == 't') {
            return len - 3;
        }
        if (len > 4 && s[len - 2] == 'e' && (s[len - 1] == 'r' || s[len - 1] == 'n')) {
            return len - 2;
        }
        if (len > 4 && s[len - 2] == 's' && s[len - 1] == 't' && this.stEnding(s[len - 3])) {
            return len - 2;
        }
        return len;
    }
}

