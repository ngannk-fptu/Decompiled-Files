/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.IngresDialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.Ingres9IdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.type.StandardBasicTypes;

public class Ingres9Dialect
extends IngresDialect {
    private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            String soff = " offset " + selection.getFirstRow();
            String slim = " fetch first " + this.getMaxOrLimit(selection) + " rows only";
            StringBuilder sb = new StringBuilder(sql.length() + soff.length() + slim.length()).append(sql);
            if (LimitHelper.hasFirstRow(selection)) {
                sb.append(soff);
            }
            if (LimitHelper.hasMaxRows(selection)) {
                sb.append(slim);
            }
            return sb.toString();
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean supportsVariableLimit() {
            return false;
        }
    };

    public Ingres9Dialect() {
        this.registerDateTimeFunctions();
        this.registerDateTimeColumnTypes();
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
    }

    protected void registerDateTimeFunctions() {
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
    }

    protected void registerDateTimeColumnTypes() {
        this.registerColumnType(91, "ansidate");
        this.registerColumnType(93, "timestamp(9) with time zone");
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public boolean forUpdateOfColumns() {
        return true;
    }

    @Override
    public String getQuerySequencesString() {
        return "select seq_name from iisequences";
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select current_timestamp";
    }

    @Override
    public String getCurrentTimestampSQLFunctionName() {
        return "current_timestamp";
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return true;
    }

    @Override
    public boolean doesRepeatableReadCauseReadersToBlockWriters() {
        return true;
    }

    @Override
    public LimitHandler getDefaultLimitHandler() {
        return LIMIT_HANDLER;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    @Override
    public boolean useMaxForLimit() {
        return false;
    }

    @Override
    public String getLimitString(String querySelect, int offset, int limit) {
        StringBuilder soff = new StringBuilder(" offset " + offset);
        StringBuilder slim = new StringBuilder(" fetch first " + limit + " rows only");
        StringBuilder sb = new StringBuilder(querySelect.length() + soff.length() + slim.length()).append(querySelect);
        if (offset > 0) {
            sb.append((CharSequence)soff);
        }
        if (limit > 0) {
            sb.append((CharSequence)slim);
        }
        return sb.toString();
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new Ingres9IdentityColumnSupport();
    }
}

