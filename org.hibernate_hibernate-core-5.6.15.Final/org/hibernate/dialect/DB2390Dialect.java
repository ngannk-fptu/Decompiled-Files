/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.identity.DB2390IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class DB2390Dialect
extends DB2Dialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            if (LimitHelper.hasFirstRow(selection)) {
                throw new UnsupportedOperationException("query result offset is not supported");
            }
            return sql + " fetch first " + this.getMaxOrLimit(selection) + " rows only";
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean useMaxForLimit() {
            return true;
        }

        @Override
        public boolean supportsVariableLimit() {
            return false;
        }
    };
    private static final AbstractLimitHandler LEGACY_LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            return sql + " fetch first " + this.getMaxOrLimit(selection) + " rows only";
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
    };

    @Override
    public boolean supportsSequences() {
        return false;
    }

    @Override
    public String getQuerySequencesString() {
        return null;
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

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        if (limit == 0) {
            return sql;
        }
        return sql + " fetch first " + limit + " rows only ";
    }

    @Override
    public LimitHandler getLimitHandler() {
        if (this.isLegacyLimitHandlerBehaviorEnabled()) {
            return LEGACY_LIMIT_HANDLER;
        }
        return LIMIT_HANDLER;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new DB2390IdentityColumnSupport();
    }
}

