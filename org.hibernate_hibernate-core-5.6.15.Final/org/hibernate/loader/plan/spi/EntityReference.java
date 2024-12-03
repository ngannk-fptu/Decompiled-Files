/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.entity.EntityPersister;

public interface EntityReference
extends FetchSource {
    @Override
    public String getQuerySpaceUid();

    public EntityPersister getEntityPersister();

    public EntityIdentifierDescription getIdentifierDescription();
}

