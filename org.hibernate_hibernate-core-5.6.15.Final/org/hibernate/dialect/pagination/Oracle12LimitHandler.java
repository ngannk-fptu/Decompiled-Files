/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.util.Locale;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;

public class Oracle12LimitHandler
extends AbstractLimitHandler {
    public boolean bindLimitParametersInReverseOrder;
    public boolean useMaxForLimit;
    public static final Oracle12LimitHandler INSTANCE = new Oracle12LimitHandler();

    Oracle12LimitHandler() {
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        boolean hasFirstRow = LimitHelper.hasFirstRow(selection);
        boolean hasMaxRows = LimitHelper.hasMaxRows(selection);
        if (!hasMaxRows) {
            return sql;
        }
        return this.processSql(sql, this.getForUpdateIndex(sql), hasFirstRow);
    }

    @Override
    public String processSql(String sql, QueryParameters queryParameters) {
        RowSelection selection = queryParameters.getRowSelection();
        boolean hasFirstRow = LimitHelper.hasFirstRow(selection);
        boolean hasMaxRows = LimitHelper.hasMaxRows(selection);
        if (!hasMaxRows) {
            return sql;
        }
        sql = sql.trim();
        LockOptions lockOptions = queryParameters.getLockOptions();
        if (lockOptions != null) {
            LockMode lockMode = lockOptions.getLockMode();
            switch (lockMode) {
                case UPGRADE: 
                case PESSIMISTIC_READ: 
                case PESSIMISTIC_WRITE: 
                case UPGRADE_NOWAIT: 
                case FORCE: 
                case PESSIMISTIC_FORCE_INCREMENT: 
                case UPGRADE_SKIPLOCKED: {
                    return this.processSql(sql, selection);
                }
            }
            return this.processSqlOffsetFetch(sql, hasFirstRow);
        }
        return this.processSqlOffsetFetch(sql, hasFirstRow);
    }

    private String processSqlOffsetFetch(String sql, boolean hasFirstRow) {
        int forUpdateLastIndex = this.getForUpdateIndex(sql);
        if (forUpdateLastIndex > -1) {
            return this.processSql(sql, forUpdateLastIndex, hasFirstRow);
        }
        this.bindLimitParametersInReverseOrder = false;
        this.useMaxForLimit = false;
        String offsetFetchString = hasFirstRow ? " offset ? rows fetch next ? rows only" : " fetch first ? rows only";
        int offsetFetchLength = sql.length() + offsetFetchString.length();
        return new StringBuilder(offsetFetchLength).append(sql).append(offsetFetchString).toString();
    }

    private String processSql(String sql, int forUpdateIndex, boolean hasFirstRow) {
        StringBuilder pagingSelect;
        this.bindLimitParametersInReverseOrder = true;
        this.useMaxForLimit = true;
        String forUpdateClause = null;
        boolean isForUpdate = false;
        if (forUpdateIndex > -1) {
            forUpdateClause = sql.substring(forUpdateIndex);
            sql = sql.substring(0, forUpdateIndex - 1);
            isForUpdate = true;
        }
        int forUpdateClauseLength = forUpdateClause == null ? 0 : forUpdateClause.length() + 1;
        if (hasFirstRow) {
            pagingSelect = new StringBuilder(sql.length() + forUpdateClauseLength + 98);
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
            pagingSelect.append(sql);
            pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
        } else {
            pagingSelect = new StringBuilder(sql.length() + forUpdateClauseLength + 37);
            pagingSelect.append("select * from ( ");
            pagingSelect.append(sql);
            pagingSelect.append(" ) where rownum <= ?");
        }
        if (isForUpdate) {
            pagingSelect.append(" ");
            pagingSelect.append(forUpdateClause);
        }
        return pagingSelect.toString();
    }

    private int getForUpdateIndex(String sql) {
        int forUpdateLastIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
        int lastIndexOfQuote = sql.lastIndexOf("'");
        if (forUpdateLastIndex > -1) {
            if (lastIndexOfQuote == -1) {
                return forUpdateLastIndex;
            }
            if (lastIndexOfQuote > forUpdateLastIndex) {
                return -1;
            }
            return forUpdateLastIndex;
        }
        return forUpdateLastIndex;
    }

    @Override
    public final boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return this.bindLimitParametersInReverseOrder;
    }

    @Override
    public boolean useMaxForLimit() {
        return this.useMaxForLimit;
    }
}

