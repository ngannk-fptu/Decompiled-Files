/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.MappingException;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class AbstractTransactSQLIdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        return "identity not null";
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) throws MappingException {
        return "select @@identity";
    }

    @Override
    public boolean supportsInsertSelectIdentity() {
        return true;
    }

    @Override
    public String appendIdentitySelectToInsert(String insertSQL) {
        return insertSQL + "\nselect @@identity";
    }
}

