/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.MappingException;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class Chache71IdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public boolean hasDataTypeInIdentityColumn() {
        return true;
    }

    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        return "identity";
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
    }
}

