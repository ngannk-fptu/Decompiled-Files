/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.entity.EntityPersister;

public interface EntityQuerySpace
extends QuerySpace {
    public EntityPersister getEntityPersister();
}

