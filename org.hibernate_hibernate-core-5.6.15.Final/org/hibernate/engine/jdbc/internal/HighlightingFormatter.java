/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.hibernate.engine.jdbc.env.spi.AnsiSqlKeywords;
import org.hibernate.engine.jdbc.internal.Formatter;

public final class HighlightingFormatter
implements Formatter {
    private static final Set<String> KEYWORDS = new HashSet<String>(AnsiSqlKeywords.INSTANCE.sql2003());
    public static final Formatter INSTANCE;
    private static final String SYMBOLS_AND_WS = "=><!+-*/()',.|&`\"? \n\r\f\t";
    private final String keywordEscape;
    private final String stringEscape;
    private final String quotedEscape;
    private final String normalEscape;

    private static String escape(String code) {
        return "\u001b[" + code + "m";
    }

    public HighlightingFormatter(String keywordCode, String stringCode, String quotedCode) {
        this.keywordEscape = HighlightingFormatter.escape(keywordCode);
        this.stringEscape = HighlightingFormatter.escape(stringCode);
        this.quotedEscape = HighlightingFormatter.escape(quotedCode);
        this.normalEscape = HighlightingFormatter.escape("0");
    }

    @Override
    public String format(String sql) {
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean inQuoted = false;
        StringTokenizer tokenizer = new StringTokenizer(sql, SYMBOLS_AND_WS, true);
        block9: while (tokenizer.hasMoreTokens()) {
            String token;
            switch (token = tokenizer.nextToken()) {
                case "\"": 
                case "`": {
                    if (inString) {
                        result.append(token);
                        continue block9;
                    }
                    if (inQuoted) {
                        inQuoted = false;
                        result.append(token).append(this.normalEscape);
                        continue block9;
                    }
                    inQuoted = true;
                    result.append(this.quotedEscape).append(token);
                    continue block9;
                }
                case "'": {
                    if (inQuoted) {
                        result.append('\'');
                        continue block9;
                    }
                    if (inString) {
                        inString = false;
                        result.append('\'').append(this.normalEscape);
                        continue block9;
                    }
                    inString = true;
                    result.append(this.stringEscape).append('\'');
                    continue block9;
                }
            }
            if (KEYWORDS.contains(token.toUpperCase())) {
                result.append(this.keywordEscape).append(token).append(this.normalEscape);
                continue;
            }
            result.append(token);
        }
        return result.toString();
    }

    static {
        KEYWORDS.addAll(Arrays.asList("KEY", "SEQUENCE", "CASCADE", "INCREMENT"));
        INSTANCE = new HighlightingFormatter("34", "36", "32");
    }
}

