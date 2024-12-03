/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.internal.base;

import com.google.template.soy.internal.base.CharEscaper;
import java.util.HashMap;
import java.util.Map;

public class CharEscaperBuilder {
    private final Map<Character, String> map = new HashMap<Character, String>();
    private int max = -1;

    public CharEscaperBuilder addEscape(char c, String r) {
        this.map.put(Character.valueOf(c), r);
        if (c > this.max) {
            this.max = c;
        }
        return this;
    }

    public CharEscaperBuilder addEscapes(char[] cs, String r) {
        for (char c : cs) {
            this.addEscape(c, r);
        }
        return this;
    }

    public char[][] toArray() {
        char[][] result = new char[this.max + 1][];
        for (Map.Entry<Character, String> entry : this.map.entrySet()) {
            result[entry.getKey().charValue()] = entry.getValue().toCharArray();
        }
        return result;
    }

    public CharEscaper toEscaper() {
        return new CharArrayDecorator(this.toArray());
    }

    private static class CharArrayDecorator
    extends CharEscaper {
        private final char[][] replacements;
        private final int replaceLength;

        public CharArrayDecorator(char[][] replacements) {
            this.replacements = replacements;
            this.replaceLength = replacements.length;
        }

        @Override
        public String escape(String s) {
            int slen = s.length();
            for (int index = 0; index < slen; ++index) {
                char c = s.charAt(index);
                if (c >= this.replacements.length || this.replacements[c] == null) continue;
                return this.escapeSlow(s, index);
            }
            return s;
        }

        @Override
        protected char[] escape(char c) {
            return c < this.replaceLength ? this.replacements[c] : null;
        }
    }
}

