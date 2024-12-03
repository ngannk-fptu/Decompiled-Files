/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import org.hibernate.persister.entity.EntityPersister;

public interface Lockable
extends EntityPersister {
    public String getRootTableName();

    public String getRootTableAlias(String var1);

    public String[] getRootTableIdentifierColumnNames();

    public String getVersionColumnName();
}

