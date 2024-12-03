/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class Escaper {
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final Set<Character> JS_ESCAPE_CHARS;
    private static final Set<Character> HTML_ESCAPE_CHARS;
    private final boolean escapeHtmlCharacters;

    Escaper(boolean escapeHtmlCharacters) {
        this.escapeHtmlCharacters = escapeHtmlCharacters;
    }

    public String escapeJsonString(CharSequence plainText) {
        StringBuilder escapedString = new StringBuilder(plainText.length() + 20);
        try {
            this.escapeJsonString(plainText, escapedString);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return escapedString.toString();
    }

    private void escapeJsonString(CharSequence plainText, StringBuilder out) throws IOException {
        int charCount;
        int pos = 0;
        int len = plainText.length();
        block10: for (int i = 0; i < len; i += charCount) {
            int codePoint = Character.codePointAt(plainText, i);
            charCount = Character.charCount(codePoint);
            if (!Escaper.isControlCharacter(codePoint) && !this.mustEscapeCharInJsString(codePoint)) continue;
            out.append(plainText, pos, i);
            pos = i + charCount;
            switch (codePoint) {
                case 8: {
                    out.append("\\b");
                    continue block10;
                }
                case 9: {
                    out.append("\\t");
                    continue block10;
                }
                case 10: {
                    out.append("\\n");
                    continue block10;
                }
                case 12: {
                    out.append("\\f");
                    continue block10;
                }
                case 13: {
                    out.append("\\r");
                    continue block10;
                }
                case 92: {
                    out.append("\\\\");
                    continue block10;
                }
                case 47: {
                    out.append("\\/");
                    continue block10;
                }
                case 34: {
                    out.append("\\\"");
                    continue block10;
                }
                default: {
                    Escaper.appendHexJavaScriptRepresentation(codePoint, out);
                }
            }
        }
        out.append(plainText, pos, len);
    }

    private boolean mustEscapeCharInJsString(int codepoint) {
        if (!Character.isSupplementaryCodePoint(codepoint)) {
            char c = (char)codepoint;
            return JS_ESCAPE_CHARS.contains(Character.valueOf(c)) || this.escapeHtmlCharacters && HTML_ESCAPE_CHARS.contains(Character.valueOf(c));
        }
        return false;
    }

    private static boolean isControlCharacter(int codePoint) {
        return codePoint < 32 || codePoint == 8232 || codePoint == 8233 || codePoint >= 127 && codePoint <= 159;
    }

    private static void appendHexJavaScriptRepresentation(int codePoint, Appendable out) throws IOException {
        if (Character.isSupplementaryCodePoint(codePoint)) {
            char[] surrogates = Character.toChars(codePoint);
            Escaper.appendHexJavaScriptRepresentation(surrogates[0], out);
            Escaper.appendHexJavaScriptRepresentation(surrogates[1], out);
            return;
        }
        out.append("\\u").append(HEX_CHARS[codePoint >>> 12 & 0xF]).append(HEX_CHARS[codePoint >>> 8 & 0xF]).append(HEX_CHARS[codePoint >>> 4 & 0xF]).append(HEX_CHARS[codePoint & 0xF]);
    }

    static {
        HashSet<Character> mandatoryEscapeSet = new HashSet<Character>();
        mandatoryEscapeSet.add(Character.valueOf('\"'));
        mandatoryEscapeSet.add(Character.valueOf('\\'));
        JS_ESCAPE_CHARS = Collections.unmodifiableSet(mandatoryEscapeSet);
        HashSet<Character> htmlEscapeSet = new HashSet<Character>();
        htmlEscapeSet.add(Character.valueOf('<'));
        htmlEscapeSet.add(Character.valueOf('>'));
        htmlEscapeSet.add(Character.valueOf('&'));
        htmlEscapeSet.add(Character.valueOf('='));
        htmlEscapeSet.add(Character.valueOf('\''));
        HTML_ESCAPE_CHARS = Collections.unmodifiableSet(htmlEscapeSet);
    }
}

