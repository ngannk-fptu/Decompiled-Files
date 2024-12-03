/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.parser;

import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.core.util.UnescapedCharSequence;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.parser.ParseException;

public class EscapeQuerySyntaxImpl
implements EscapeQuerySyntax {
    private static final char[] wildcardChars = new char[]{'*', '?'};
    private static final String[] escapableTermExtraFirstChars = new String[]{"+", "-", "@"};
    private static final String[] escapableTermChars = new String[]{"\"", "<", ">", "=", "!", "(", ")", "^", "[", "{", ":", "]", "}", "~", "/"};
    private static final String[] escapableQuotedChars = new String[]{"\""};
    private static final String[] escapableWhiteChars = new String[]{" ", "\t", "\n", "\r", "\f", "\b", "\u3000"};
    private static final String[] escapableWordTokens = new String[]{"AND", "OR", "NOT", "TO", "WITHIN", "SENTENCE", "PARAGRAPH", "INORDER"};

    private static final CharSequence escapeChar(CharSequence str, Locale locale) {
        int i;
        if (str == null || str.length() == 0) {
            return str;
        }
        CharSequence buffer = str;
        for (i = 0; i < escapableTermChars.length; ++i) {
            buffer = EscapeQuerySyntaxImpl.replaceIgnoreCase(buffer, escapableTermChars[i].toLowerCase(locale), "\\", locale);
        }
        for (i = 0; i < escapableTermExtraFirstChars.length; ++i) {
            if (buffer.charAt(0) != escapableTermExtraFirstChars[i].charAt(0)) continue;
            buffer = "\\" + buffer.charAt(0) + buffer.subSequence(1, buffer.length());
            break;
        }
        return buffer;
    }

    private final CharSequence escapeQuoted(CharSequence str, Locale locale) {
        if (str == null || str.length() == 0) {
            return str;
        }
        CharSequence buffer = str;
        for (int i = 0; i < escapableQuotedChars.length; ++i) {
            buffer = EscapeQuerySyntaxImpl.replaceIgnoreCase(buffer, escapableTermChars[i].toLowerCase(locale), "\\", locale);
        }
        return buffer;
    }

    private static final CharSequence escapeTerm(CharSequence term, Locale locale) {
        if (term == null) {
            return term;
        }
        term = EscapeQuerySyntaxImpl.escapeChar(term, locale);
        term = EscapeQuerySyntaxImpl.escapeWhiteChar(term, locale);
        for (int i = 0; i < escapableWordTokens.length; ++i) {
            if (!escapableWordTokens[i].equalsIgnoreCase(term.toString())) continue;
            return "\\" + term;
        }
        return term;
    }

    private static CharSequence replaceIgnoreCase(CharSequence string, CharSequence sequence1, CharSequence escapeChar, Locale locale) {
        int firstIndex;
        if (escapeChar == null || sequence1 == null || string == null) {
            throw new NullPointerException();
        }
        int count = string.length();
        int sequence1Length = sequence1.length();
        if (sequence1Length == 0) {
            StringBuilder result = new StringBuilder((count + 1) * escapeChar.length());
            result.append(escapeChar);
            for (int i = 0; i < count; ++i) {
                result.append(string.charAt(i));
                result.append(escapeChar);
            }
            return result.toString();
        }
        StringBuilder result = new StringBuilder();
        char first = sequence1.charAt(0);
        int start = 0;
        int copyStart = 0;
        while (start < count && (firstIndex = string.toString().toLowerCase(locale).indexOf(first, start)) != -1) {
            boolean found = true;
            if (sequence1.length() > 1) {
                if (firstIndex + sequence1Length > count) break;
                for (int i = 1; i < sequence1Length; ++i) {
                    if (string.toString().toLowerCase(locale).charAt(firstIndex + i) == sequence1.charAt(i)) continue;
                    found = false;
                    break;
                }
            }
            if (found) {
                result.append(string.toString().substring(copyStart, firstIndex));
                result.append(escapeChar);
                result.append(string.toString().substring(firstIndex, firstIndex + sequence1Length));
                copyStart = start = firstIndex + sequence1Length;
                continue;
            }
            start = firstIndex + 1;
        }
        if (result.length() == 0 && copyStart == 0) {
            return string;
        }
        result.append(string.toString().substring(copyStart));
        return result.toString();
    }

    private static final CharSequence escapeWhiteChar(CharSequence str, Locale locale) {
        if (str == null || str.length() == 0) {
            return str;
        }
        CharSequence buffer = str;
        for (int i = 0; i < escapableWhiteChars.length; ++i) {
            buffer = EscapeQuerySyntaxImpl.replaceIgnoreCase(buffer, escapableWhiteChars[i].toLowerCase(locale), "\\", locale);
        }
        return buffer;
    }

    @Override
    public CharSequence escape(CharSequence text, Locale locale, EscapeQuerySyntax.Type type) {
        if (text == null || text.length() == 0) {
            return text;
        }
        text = text instanceof UnescapedCharSequence ? ((UnescapedCharSequence)text).toStringEscaped(wildcardChars) : new UnescapedCharSequence(text).toStringEscaped(wildcardChars);
        if (type == EscapeQuerySyntax.Type.STRING) {
            return this.escapeQuoted(text, locale);
        }
        return EscapeQuerySyntaxImpl.escapeTerm(text, locale);
    }

    public static UnescapedCharSequence discardEscapeChar(CharSequence input) throws ParseException {
        char[] output = new char[input.length()];
        boolean[] wasEscaped = new boolean[input.length()];
        int length = 0;
        boolean lastCharWasEscapeChar = false;
        int codePointMultiplier = 0;
        int codePoint = 0;
        for (int i = 0; i < input.length(); ++i) {
            char curChar = input.charAt(i);
            if (codePointMultiplier > 0) {
                codePoint += EscapeQuerySyntaxImpl.hexToInt(curChar) * codePointMultiplier;
                if ((codePointMultiplier >>>= 4) != 0) continue;
                output[length++] = (char)codePoint;
                codePoint = 0;
                continue;
            }
            if (lastCharWasEscapeChar) {
                if (curChar == 'u') {
                    codePointMultiplier = 4096;
                } else {
                    output[length] = curChar;
                    wasEscaped[length] = true;
                    ++length;
                }
                lastCharWasEscapeChar = false;
                continue;
            }
            if (curChar == '\\') {
                lastCharWasEscapeChar = true;
                continue;
            }
            output[length] = curChar;
            ++length;
        }
        if (codePointMultiplier > 0) {
            throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION));
        }
        if (lastCharWasEscapeChar) {
            throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_ESCAPE_CHARACTER));
        }
        return new UnescapedCharSequence(output, wasEscaped, 0, length);
    }

    private static final int hexToInt(char c) throws ParseException {
        if ('0' <= c && c <= '9') {
            return c - 48;
        }
        if ('a' <= c && c <= 'f') {
            return c - 97 + 10;
        }
        if ('A' <= c && c <= 'F') {
            return c - 65 + 10;
        }
        throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_ESCAPE_NONE_HEX_UNICODE, Character.valueOf(c)));
    }
}

