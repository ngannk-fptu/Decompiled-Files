/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.GetGeneratedKeysDelegate;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.dialect.identity.Oracle12cGetGeneratedKeysDelegate;
import org.hibernate.id.PostInsertIdentityPersister;

public class Oracle12cIdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public boolean supportsInsertSelectIdentity() {
        return true;
    }

    @Override
    public String getIdentityColumnString(int type) {
        return "generated as identity";
    }

    @Override
    public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
        return new Oracle12cGetGeneratedKeysDelegate(persister, dialect);
    }

    @Override
    public String getIdentityInsertString() {
        return "default";
    }
}

