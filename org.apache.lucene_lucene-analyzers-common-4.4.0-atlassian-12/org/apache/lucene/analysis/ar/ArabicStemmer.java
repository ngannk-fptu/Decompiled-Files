/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.ar;

import org.apache.lucene.analysis.util.StemmerUtil;

public class ArabicStemmer {
    public static final char ALEF = '\u0627';
    public static final char BEH = '\u0628';
    public static final char TEH_MARBUTA = '\u0629';
    public static final char TEH = '\u062a';
    public static final char FEH = '\u0641';
    public static final char KAF = '\u0643';
    public static final char LAM = '\u0644';
    public static final char NOON = '\u0646';
    public static final char HEH = '\u0647';
    public static final char WAW = '\u0648';
    public static final char YEH = '\u064a';
    public static final char[][] prefixes = new char[][]{"\u0627\u0644".toCharArray(), "\u0648\u0627\u0644".toCharArray(), "\u0628\u0627\u0644".toCharArray(), "\u0643\u0627\u0644".toCharArray(), "\u0641\u0627\u0644".toCharArray(), "\u0644\u0644".toCharArray(), "\u0648".toCharArray()};
    public static final char[][] suffixes = new char[][]{"\u0647\u0627".toCharArray(), "\u0627\u0646".toCharArray(), "\u0627\u062a".toCharArray(), "\u0648\u0646".toCharArray(), "\u064a\u0646".toCharArray(), "\u064a\u0647".toCharArray(), "\u064a\u0629".toCharArray(), "\u0647".toCharArray(), "\u0629".toCharArray(), "\u064a".toCharArray()};

    public int stem(char[] s, int len) {
        len = this.stemPrefix(s, len);
        len = this.stemSuffix(s, len);
        return len;
    }

    public int stemPrefix(char[] s, int len) {
        for (int i = 0; i < prefixes.length; ++i) {
            if (!this.startsWithCheckLength(s, len, prefixes[i])) continue;
            return StemmerUtil.deleteN(s, 0, len, prefixes[i].length);
        }
        return len;
    }

    public int stemSuffix(char[] s, int len) {
        for (int i = 0; i < suffixes.length; ++i) {
            if (!this.endsWithCheckLength(s, len, suffixes[i])) continue;
            len = StemmerUtil.deleteN(s, len - suffixes[i].length, len, suffixes[i].length);
        }
        return len;
    }

    boolean startsWithCheckLength(char[] s, int len, char[] prefix) {
        if (prefix.length == 1 && len < 4) {
            return false;
        }
        if (len < prefix.length + 2) {
            return false;
        }
        for (int i = 0; i < prefix.length; ++i) {
            if (s[i] == prefix[i]) continue;
            return false;
        }
        return true;
    }

    boolean endsWithCheckLength(char[] s, int len, char[] suffix) {
        if (len < suffix.length + 2) {
            return false;
        }
        for (int i = 0; i < suffix.length; ++i) {
            if (s[len - suffix.length + i] == suffix[i]) continue;
            return false;
        }
        return true;
    }
}

