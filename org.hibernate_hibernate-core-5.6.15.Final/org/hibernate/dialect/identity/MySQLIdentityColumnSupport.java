/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class MySQLIdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "select last_insert_id()";
    }

    @Override
    public String getIdentityColumnString(int type) {
        return "not null auto_increment";
    }
}

