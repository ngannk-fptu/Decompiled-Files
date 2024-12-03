/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import java.util.regex.Pattern;

public final class HtmlEscaper {
    private static final Pattern ENTITY_PATTERN = Pattern.compile("^&[#A-Za-z0-9][A-Za-z0-9]{1,7};");
    private static final CharMap ESCAPE_ALL_CHAR_MAP = new CharMap(){

        @Override
        public String get(char c) {
            if (c == '\'') {
                return "&#39;";
            }
            if (c == '\"') {
                return "&quot;";
            }
            return ESCAPE_ALL_EXCEPT_QUOTES_CHAR_MAP.get(c);
        }
    };
    private static final CharMap ESCAPE_ALL_EXCEPT_QUOTES_CHAR_MAP = new CharMap(){

        @Override
        public String get(char c) {
            switch (c) {
                case '&': {
                    return "&amp;";
                }
                case '>': {
                    return "&gt;";
                }
                case '<': {
                    return "&lt;";
                }
                case '\u0091': {
                    return "&lsquo;";
                }
                case '\u0092': {
                    return "&rsquo;";
                }
                case '\u0093': {
                    return "&ldquo;";
                }
                case '\u0094': {
                    return "&rdquo;";
                }
            }
            return Character.toString(c);
        }
    };
    private static final CharMap ESCAPE_AMPERSAND_CHAR_MAP = new CharMap(){

        @Override
        public String get(char c) {
            if (c == '&') {
                return "&amp;";
            }
            return Character.toString(c);
        }
    };

    private HtmlEscaper() {
    }

    public static String escapeAll(String s, boolean preserveExistingEntities) {
        return HtmlEscaper.doReplacement(s, preserveExistingEntities, ESCAPE_ALL_CHAR_MAP);
    }

    public static String escapeAllExceptQuotes(String s, boolean preserveExistingEntities) {
        return HtmlEscaper.doReplacement(s, preserveExistingEntities, ESCAPE_ALL_EXCEPT_QUOTES_CHAR_MAP);
    }

    public static String escapeAmpersands(String s, boolean preserveExistingEntities) {
        return HtmlEscaper.doReplacement(s, preserveExistingEntities, ESCAPE_AMPERSAND_CHAR_MAP);
    }

    private static String doReplacement(String s, boolean preserveExistingEntities, CharMap charMap) {
        if (s == null) {
            return null;
        }
        StringBuffer out = new StringBuffer(s.length() + 50);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '&' && preserveExistingEntities && HtmlEscaper.entityAt(s, i)) {
                out.append(c);
                continue;
            }
            out.append(charMap.get(c));
        }
        return out.toString();
    }

    private static boolean entityAt(String s, int startIndex) {
        String substring = s.substring(startIndex);
        return ENTITY_PATTERN.matcher(substring).find();
    }

    private static interface CharMap {
        public String get(char var1);
    }
}

