/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class JDataStoreIdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return null;
    }

    @Override
    public String getIdentityColumnString(int type) {
        return "autoincrement";
    }
}

