/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.AbstractTransactSQLIdentityColumnSupport;

public class SybaseAnywhereIdentityColumnSupport
extends AbstractTransactSQLIdentityColumnSupport {
    @Override
    public boolean supportsInsertSelectIdentity() {
        return false;
    }
}

