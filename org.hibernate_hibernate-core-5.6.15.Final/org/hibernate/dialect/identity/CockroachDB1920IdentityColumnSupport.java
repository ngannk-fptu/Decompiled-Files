/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class CockroachDB1920IdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return false;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "select 1";
    }

    @Override
    public String getIdentityColumnString(int type) {
        return type == 5 ? "serial4 not null" : "serial8 not null";
    }

    @Override
    public boolean hasDataTypeInIdentityColumn() {
        return false;
    }
}

