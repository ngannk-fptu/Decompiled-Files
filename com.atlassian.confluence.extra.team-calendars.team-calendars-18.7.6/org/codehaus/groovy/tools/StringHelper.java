/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import java.util.LinkedList;

public class StringHelper {
    private static final char SPACE = ' ';
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '\"';

    public static String[] tokenizeUnquoted(String s) {
        LinkedList<String> tokens = new LinkedList<String>();
        int first = 0;
        while (first < s.length()) {
            int last;
            if ((first = StringHelper.skipWhitespace(s, first)) < (last = StringHelper.scanToken(s, first))) {
                tokens.add(s.substring(first, last));
            }
            first = last;
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private static int scanToken(String s, int pos0) {
        char c;
        int pos = pos0;
        while (pos < s.length() && ' ' != (c = s.charAt(pos))) {
            ++pos;
            if ('\'' == c) {
                pos = StringHelper.scanQuoted(s, pos, '\'');
                continue;
            }
            if ('\"' != c) continue;
            pos = StringHelper.scanQuoted(s, pos, '\"');
        }
        return pos;
    }

    private static int scanQuoted(String s, int pos0, char quote) {
        char c;
        int pos = pos0;
        while (pos < s.length() && quote != (c = s.charAt(pos++))) {
        }
        return pos;
    }

    private static int skipWhitespace(String s, int pos0) {
        char c;
        int pos;
        for (pos = pos0; pos < s.length() && ' ' == (c = s.charAt(pos)); ++pos) {
        }
        return pos;
    }
}

