/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.InterbaseDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.type.StandardBasicTypes;

public class FirebirdDialect
extends InterbaseDialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return new StringBuilder(sql.length() + 20).append(sql).insert(6, hasOffset ? " first ? skip ?" : " first ?").toString();
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean bindLimitParametersFirst() {
            return true;
        }

        @Override
        public boolean bindLimitParametersInReverseOrder() {
            return true;
        }
    };

    public FirebirdDialect() {
        this.registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop generator " + sequenceName;
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        return new StringBuilder(sql.length() + 20).append(sql).insert(6, hasOffset ? " first ? skip ?" : " first ?").toString();
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return true;
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }
}

