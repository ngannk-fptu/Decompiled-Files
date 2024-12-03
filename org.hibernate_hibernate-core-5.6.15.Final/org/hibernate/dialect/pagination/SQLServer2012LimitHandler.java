/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.dialect.pagination.SQLServer2005LimitHandler;
import org.hibernate.engine.spi.RowSelection;

public class SQLServer2012LimitHandler
extends SQLServer2005LimitHandler {
    private boolean usedOffsetFetch;

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsVariableLimit() {
        return true;
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        if (this.hasOrderBy(sql)) {
            if (!LimitHelper.useLimit(this, selection)) {
                return sql;
            }
            return this.applyOffsetFetch(selection, sql, this.getInsertPosition(sql));
        }
        return super.processSql(sql, selection);
    }

    @Override
    public boolean useMaxForLimit() {
        return this.usedOffsetFetch ? false : super.useMaxForLimit();
    }

    @Override
    public int convertToFirstRowValue(int zeroBasedFirstResult) {
        if (this.usedOffsetFetch) {
            return zeroBasedFirstResult;
        }
        return super.convertToFirstRowValue(zeroBasedFirstResult);
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        if (this.usedOffsetFetch && !LimitHelper.hasFirstRow(selection)) {
            statement.setInt(index, this.getMaxOrLimit(selection));
            return 1;
        }
        return super.bindLimitParametersAtEndOfQuery(selection, statement, index);
    }

    private String getOffsetFetch(RowSelection selection) {
        if (!LimitHelper.hasFirstRow(selection)) {
            return " offset 0 rows fetch next ? rows only";
        }
        return " offset ? rows fetch next ? rows only";
    }

    private int getInsertPosition(String sql) {
        char ch;
        int position;
        for (position = sql.length() - 1; position > 0 && ((ch = sql.charAt(position)) == ';' || ch == ' ' || ch == '\r' || ch == '\n'); --position) {
        }
        return position + 1;
    }

    private String applyOffsetFetch(RowSelection selection, String sql, int position) {
        this.usedOffsetFetch = true;
        StringBuilder sb = new StringBuilder();
        sb.append(sql.substring(0, position));
        sb.append(this.getOffsetFetch(selection));
        if (position > sql.length()) {
            sb.append(sql.substring(position - 1));
        }
        return sb.toString();
    }

    private boolean hasOrderBy(String sql) {
        int depth = 0;
        String lowerCaseSQL = sql.toLowerCase();
        for (int i = lowerCaseSQL.length() - 1; i >= 0; --i) {
            char ch = lowerCaseSQL.charAt(i);
            if (ch == '(') {
                ++depth;
            } else if (ch == ')') {
                --depth;
            }
            if (depth != 0 || !lowerCaseSQL.startsWith("order by ", i)) continue;
            return true;
        }
        return false;
    }
}

