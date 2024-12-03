/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExtractIndexAndSql {
    private static final Pattern NAMED_QUERY_PATTERN = Pattern.compile("(?<!:)(:)(\\w+)|\\?(\\d*)(?:\\.(\\w+))?");
    private static final char QUOTE = '\'';
    private final String sql;
    private List<Tuple> indexPropList;
    private String newSql;
    private int index = 0;

    static ExtractIndexAndSql from(String sql) {
        return new ExtractIndexAndSql(sql).invoke();
    }

    static boolean hasNamedParameters(String sql) {
        return NAMED_QUERY_PATTERN.matcher(sql).find();
    }

    private ExtractIndexAndSql(String sql) {
        this.sql = sql;
    }

    List<Tuple> getIndexPropList() {
        return this.indexPropList;
    }

    String getNewSql() {
        return this.newSql;
    }

    private ExtractIndexAndSql invoke() {
        this.indexPropList = new ArrayList<Tuple>();
        StringBuilder sb = new StringBuilder();
        StringBuilder currentChunk = new StringBuilder();
        while (this.index < this.sql.length()) {
            switch (this.sql.charAt(this.index)) {
                case '\'': {
                    sb.append(ExtractIndexAndSql.adaptForNamedParams(currentChunk.toString(), this.indexPropList));
                    currentChunk = new StringBuilder();
                    this.appendToEndOfString(sb);
                    break;
                }
                case '-': {
                    if (this.next() == '-') {
                        sb.append(ExtractIndexAndSql.adaptForNamedParams(currentChunk.toString(), this.indexPropList));
                        currentChunk = new StringBuilder();
                        this.appendToEndOfLine(sb);
                        break;
                    }
                    currentChunk.append(this.sql.charAt(this.index));
                    break;
                }
                case '/': {
                    if (this.next() == '*') {
                        sb.append(ExtractIndexAndSql.adaptForNamedParams(currentChunk.toString(), this.indexPropList));
                        currentChunk = new StringBuilder();
                        this.appendToEndOfComment(sb);
                        break;
                    }
                    currentChunk.append(this.sql.charAt(this.index));
                    break;
                }
                default: {
                    currentChunk.append(this.sql.charAt(this.index));
                }
            }
            ++this.index;
        }
        sb.append(ExtractIndexAndSql.adaptForNamedParams(currentChunk.toString(), this.indexPropList));
        this.newSql = sb.toString();
        return this;
    }

    private void appendToEndOfString(StringBuilder buffer) {
        buffer.append('\'');
        int startQuoteIndex = this.index++;
        boolean foundClosingQuote = false;
        while (this.index < this.sql.length()) {
            char c = this.sql.charAt(this.index);
            buffer.append(c);
            if (c == '\'' && this.next() != '\'') {
                if (startQuoteIndex == this.index - 1) {
                    foundClosingQuote = true;
                    break;
                }
                int previousQuotes = this.countPreviousRepeatingChars('\'');
                if (previousQuotes == 0 || previousQuotes % 2 == 0 && this.index - previousQuotes != startQuoteIndex || previousQuotes % 2 != 0 && this.index - previousQuotes == startQuoteIndex) {
                    foundClosingQuote = true;
                    break;
                }
            }
            ++this.index;
        }
        if (!foundClosingQuote) {
            throw new IllegalStateException("Failed to process query. Unterminated ' character?");
        }
    }

    private int countPreviousRepeatingChars(char c) {
        int pos;
        for (pos = this.index - 1; pos >= 0 && this.sql.charAt(pos) == c; --pos) {
        }
        return this.index - 1 - pos;
    }

    private void appendToEndOfComment(StringBuilder buffer) {
        while (this.index < this.sql.length()) {
            char c = this.sql.charAt(this.index);
            buffer.append(c);
            if (c == '*' && this.next() == '/') {
                buffer.append('/');
                ++this.index;
                break;
            }
            ++this.index;
        }
    }

    private void appendToEndOfLine(StringBuilder buffer) {
        while (this.index < this.sql.length()) {
            char c = this.sql.charAt(this.index);
            buffer.append(c);
            if (c == '\n' || c == '\r') break;
            ++this.index;
        }
    }

    private char next() {
        return this.index + 1 < this.sql.length() ? this.sql.charAt(this.index + 1) : (char)'\u0000';
    }

    private static String adaptForNamedParams(String sql, List<Tuple> indexPropList) {
        StringBuilder newSql = new StringBuilder();
        int txtIndex = 0;
        Matcher matcher = NAMED_QUERY_PATTERN.matcher(sql);
        while (matcher.find()) {
            newSql.append(sql.substring(txtIndex, matcher.start())).append('?');
            String indexStr = matcher.group(1);
            if (indexStr == null) {
                indexStr = matcher.group(3);
            }
            int index = indexStr == null || indexStr.length() == 0 || ":".equals(indexStr) ? 0 : Integer.parseInt(indexStr) - 1;
            String prop = matcher.group(2);
            if (prop == null) {
                prop = matcher.group(4);
            }
            indexPropList.add(new Tuple(new Object[]{index, prop == null || prop.length() == 0 ? "<this>" : prop}));
            txtIndex = matcher.end();
        }
        newSql.append(sql.substring(txtIndex));
        return newSql.toString();
    }
}

