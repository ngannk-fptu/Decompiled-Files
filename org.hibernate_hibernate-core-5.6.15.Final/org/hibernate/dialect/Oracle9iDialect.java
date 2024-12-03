/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.util.Locale;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.sql.ANSICaseFragment;
import org.hibernate.sql.CaseFragment;

public class Oracle9iDialect
extends Oracle8iDialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            sql = sql.trim();
            String forUpdateClause = null;
            boolean isForUpdate = false;
            int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
            if (forUpdateIndex > -1) {
                forUpdateClause = sql.substring(forUpdateIndex);
                sql = sql.substring(0, forUpdateIndex - 1);
                isForUpdate = true;
            }
            StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
            if (hasOffset) {
                pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
            } else {
                pagingSelect.append("select * from ( ");
            }
            pagingSelect.append(sql);
            if (hasOffset) {
                pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
            } else {
                pagingSelect.append(" ) where rownum <= ?");
            }
            if (isForUpdate) {
                pagingSelect.append(" ");
                pagingSelect.append(forUpdateClause);
            }
            return pagingSelect.toString();
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean bindLimitParametersInReverseOrder() {
            return true;
        }

        @Override
        public boolean useMaxForLimit() {
            return true;
        }
    };

    @Override
    protected void registerCharacterTypeMappings() {
        this.registerColumnType(1, "char(1 char)");
        this.registerColumnType(12, 4000L, "varchar2($l char)");
        this.registerColumnType(12, "long");
        this.registerColumnType(-9, "nvarchar2($l)");
        this.registerColumnType(-16, "nvarchar2($l)");
    }

    @Override
    protected void registerDateTimeTypeMappings() {
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "date");
        this.registerColumnType(93, "timestamp");
    }

    @Override
    public CaseFragment createCaseFragment() {
        return new ANSICaseFragment();
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        sql = sql.trim();
        String forUpdateClause = null;
        boolean isForUpdate = false;
        int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
        if (forUpdateIndex > -1) {
            forUpdateClause = sql.substring(forUpdateIndex);
            sql = sql.substring(0, forUpdateIndex - 1);
            isForUpdate = true;
        }
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
        } else {
            pagingSelect.append(" ) where rownum <= ?");
        }
        if (isForUpdate) {
            pagingSelect.append(" ");
            pagingSelect.append(forUpdateClause);
        }
        return pagingSelect.toString();
    }

    @Override
    public String getSelectClauseNullString(int sqlType) {
        return this.getBasicSelectClauseNullString(sqlType);
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select systimestamp from dual";
    }

    @Override
    public String getCurrentTimestampSQLFunctionName() {
        return "current_timestamp";
    }

    @Override
    public String getForUpdateString() {
        return " for update";
    }

    @Override
    public String getWriteLockString(int timeout) {
        if (timeout == 0) {
            return " for update nowait";
        }
        if (timeout > 0) {
            float seconds = (float)timeout / 1000.0f;
            timeout = Math.round(seconds);
            return " for update wait " + timeout;
        }
        return " for update";
    }

    @Override
    public String getReadLockString(int timeout) {
        return this.getWriteLockString(timeout);
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }

    @Override
    public boolean supportsTupleDistinctCounts() {
        return false;
    }
}

