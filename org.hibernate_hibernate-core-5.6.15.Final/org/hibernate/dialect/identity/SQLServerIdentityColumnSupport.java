/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.AbstractTransactSQLIdentityColumnSupport;

public class SQLServerIdentityColumnSupport
extends AbstractTransactSQLIdentityColumnSupport {
    @Override
    public String appendIdentitySelectToInsert(String insertSQL) {
        return insertSQL + " select scope_identity()";
    }
}

