/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class PostgreSQL81IdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "select currval('" + table + '_' + column + "_seq')";
    }

    @Override
    public String getIdentityColumnString(int type) {
        return type == -5 ? "bigserial not null" : "serial not null";
    }

    @Override
    public boolean hasDataTypeInIdentityColumn() {
        return false;
    }
}

