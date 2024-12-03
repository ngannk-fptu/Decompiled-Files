/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import org.hibernate.LockMode;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.Status;
import org.hibernate.persister.entity.EntityPersister;

public interface EntityEntryFactory
extends Serializable {
    public EntityEntry createEntityEntry(Status var1, Object[] var2, Object var3, Serializable var4, Object var5, LockMode var6, boolean var7, EntityPersister var8, boolean var9, PersistenceContext var10);
}

