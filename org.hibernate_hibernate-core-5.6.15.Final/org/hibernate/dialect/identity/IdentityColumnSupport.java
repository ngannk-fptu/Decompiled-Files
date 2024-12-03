/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.GetGeneratedKeysDelegate;
import org.hibernate.id.PostInsertIdentityPersister;

public interface IdentityColumnSupport {
    public boolean supportsIdentityColumns();

    public boolean supportsInsertSelectIdentity();

    public boolean hasDataTypeInIdentityColumn();

    public String appendIdentitySelectToInsert(String var1);

    default public String appendIdentitySelectToInsert(String identityColumnName, String insertString) {
        return this.appendIdentitySelectToInsert(insertString);
    }

    public String getIdentitySelectString(String var1, String var2, int var3) throws MappingException;

    public String getIdentityColumnString(int var1) throws MappingException;

    public String getIdentityInsertString();

    public GetGeneratedKeysDelegate buildGetGeneratedKeysDelegate(PostInsertIdentityPersister var1, Dialect var2);
}

