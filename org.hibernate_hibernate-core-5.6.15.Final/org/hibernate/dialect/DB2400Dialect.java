/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.identity.DB2390IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;

public class DB2400Dialect
extends DB2Dialect {
    @Override
    public boolean supportsSequences() {
        return false;
    }

    @Override
    public String getQuerySequencesString() {
        return null;
    }

    @Override
    public boolean supportsLimitOffset() {
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
    public String getForUpdateString() {
        return " for update with rs";
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new DB2390IdentityColumnSupport();
    }
}

