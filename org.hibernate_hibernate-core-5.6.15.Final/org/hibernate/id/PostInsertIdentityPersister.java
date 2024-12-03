/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import org.hibernate.persister.entity.EntityPersister;

public interface PostInsertIdentityPersister
extends EntityPersister {
    public String getSelectByUniqueKeyString(String var1);

    public String getIdentitySelectString();

    public String[] getIdentifierColumnNames();

    public String[] getRootTableKeyColumnNames();
}

