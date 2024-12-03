/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.engine.spi.RowSelection;

public class SybaseASE157LimitHandler
extends AbstractLimitHandler {
    private static final Pattern SELECT_DISTINCT_PATTERN = Pattern.compile("^(\\s*select\\s+distinct\\s+).*", 2);
    private static final Pattern SELECT_PATTERN = Pattern.compile("^(\\s*select\\s+).*", 2);
    private static final Pattern TOP_PATTERN = Pattern.compile("^\\s*top\\s+.*", 2);

    @Override
    public String processSql(String sql, RowSelection selection) {
        if (selection.getMaxRows() == null) {
            return sql;
        }
        int top = this.getMaxOrLimit(selection);
        if (top == Integer.MAX_VALUE) {
            return sql;
        }
        Matcher selectDistinctMatcher = SELECT_DISTINCT_PATTERN.matcher(sql);
        if (selectDistinctMatcher.matches()) {
            return SybaseASE157LimitHandler.insertTop(selectDistinctMatcher, sql, top);
        }
        Matcher selectMatcher = SELECT_PATTERN.matcher(sql);
        if (selectMatcher.matches()) {
            return SybaseASE157LimitHandler.insertTop(selectMatcher, sql, top);
        }
        return sql;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return false;
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    private static String insertTop(Matcher matcher, String sql, int top) {
        int end = matcher.end(1);
        if (TOP_PATTERN.matcher(sql.substring(end)).matches()) {
            return sql;
        }
        StringBuilder sb = new StringBuilder(sql);
        sb.insert(end, "top " + top + " ");
        return sb.toString();
    }
}

