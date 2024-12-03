/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.DB2IdentityColumnSupport;

public class DB2390IdentityColumnSupport
extends DB2IdentityColumnSupport {
    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "select identity_val_local() from sysibm.sysdummy1";
    }
}

