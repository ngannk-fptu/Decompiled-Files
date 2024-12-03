/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.GetGeneratedKeysDelegate;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.id.PostInsertIdentityPersister;

public class IdentityColumnSupportImpl
implements IdentityColumnSupport {
    @Override
    public boolean supportsIdentityColumns() {
        return false;
    }

    @Override
    public boolean supportsInsertSelectIdentity() {
        return false;
    }

    @Override
    public boolean hasDataTypeInIdentityColumn() {
        return true;
    }

    @Override
    public String appendIdentitySelectToInsert(String insertString) {
        return insertString;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) throws MappingException {
        throw new MappingException(this.getClass().getName() + " does not support identity key generation");
    }

    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        throw new MappingException(this.getClass().getName() + " does not support identity key generation");
    }

    @Override
    public String getIdentityInsertString() {
        return null;
    }

    @Override
    public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
        return new GetGeneratedKeysDelegate(persister, dialect);
    }
}

