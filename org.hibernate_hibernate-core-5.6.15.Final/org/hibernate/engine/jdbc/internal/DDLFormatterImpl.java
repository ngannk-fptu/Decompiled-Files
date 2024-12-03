/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.internal;

import java.util.Locale;
import java.util.StringTokenizer;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.internal.util.StringHelper;

public class DDLFormatterImpl
implements Formatter {
    private static final String INITIAL_LINE = System.lineSeparator() + "    ";
    private static final String OTHER_LINES = System.lineSeparator() + "       ";
    public static final DDLFormatterImpl INSTANCE = new DDLFormatterImpl();

    @Override
    public String format(String sql) {
        if (StringHelper.isEmpty(sql)) {
            return sql;
        }
        if (sql.toLowerCase(Locale.ROOT).startsWith("create table")) {
            return this.formatCreateTable(sql);
        }
        if (sql.toLowerCase(Locale.ROOT).startsWith("create")) {
            return sql;
        }
        if (sql.toLowerCase(Locale.ROOT).startsWith("alter table")) {
            return this.formatAlterTable(sql);
        }
        if (sql.toLowerCase(Locale.ROOT).startsWith("comment on")) {
            return this.formatCommentOn(sql);
        }
        return INITIAL_LINE + sql;
    }

    private String formatCommentOn(String sql) {
        StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        StringTokenizer tokens = new StringTokenizer(sql, " '[]\"", true);
        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            result.append(token);
            if (DDLFormatterImpl.isQuote(token)) {
                quoted = !quoted;
                continue;
            }
            if (quoted || !"is".equals(token)) continue;
            result.append(OTHER_LINES);
        }
        return result.toString();
    }

    private String formatAlterTable(String sql) {
        StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        StringTokenizer tokens = new StringTokenizer(sql, " (,)'[]\"", true);
        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (DDLFormatterImpl.isQuote(token)) {
                quoted = !quoted;
            } else if (!quoted && DDLFormatterImpl.isBreak(token)) {
                result.append(OTHER_LINES);
            }
            result.append(token);
        }
        return result.toString();
    }

    private String formatCreateTable(String sql) {
        StringBuilder result = new StringBuilder(60).append(INITIAL_LINE);
        StringTokenizer tokens = new StringTokenizer(sql, "(,)'[]\"", true);
        int depth = 0;
        boolean quoted = false;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (DDLFormatterImpl.isQuote(token)) {
                quoted = !quoted;
                result.append(token);
                continue;
            }
            if (quoted) {
                result.append(token);
                continue;
            }
            if (")".equals(token) && --depth == 0) {
                result.append(INITIAL_LINE);
            }
            result.append(token);
            if (",".equals(token) && depth == 1) {
                result.append(OTHER_LINES);
            }
            if (!"(".equals(token) || ++depth != 1) continue;
            result.append(OTHER_LINES);
        }
        return result.toString();
    }

    private static boolean isBreak(String token) {
        return "drop".equals(token) || "add".equals(token) || "references".equals(token) || "foreign".equals(token) || "on".equals(token);
    }

    private static boolean isQuote(String tok) {
        return "\"".equals(tok) || "`".equals(tok) || "]".equals(tok) || "[".equals(tok) || "'".equals(tok);
    }
}

