/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.GrammarUtil;

public class WriterUtil {
    public static void appendQuotedIfNonToken(StringBuilder b, String value) {
        boolean quote;
        if (value == null) {
            return;
        }
        boolean bl = quote = !GrammarUtil.isTokenString(value);
        if (quote) {
            b.append('\"');
        }
        WriterUtil.appendEscapingQuotes(b, value);
        if (quote) {
            b.append('\"');
        }
    }

    public static void appendQuotedIfWhitespace(StringBuilder b, String value) {
        if (value == null) {
            return;
        }
        boolean quote = GrammarUtil.containsWhiteSpace(value);
        if (quote) {
            b.append('\"');
        }
        WriterUtil.appendEscapingQuotes(b, value);
        if (quote) {
            b.append('\"');
        }
    }

    public static void appendQuoted(StringBuilder b, String value) {
        b.append('\"');
        WriterUtil.appendEscapingQuotes(b, value);
        b.append('\"');
    }

    public static void appendEscapingQuotes(StringBuilder b, String value) {
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c == '\"') {
                b.append('\\');
            }
            b.append(c);
        }
    }
}

