/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.process.internal;

import org.hibernate.engine.spi.EntityKey;
import org.hibernate.loader.plan.spi.EntityReference;

public class HydratedEntityRegistration {
    private final EntityReference entityReference;
    private final EntityKey key;
    private Object instance;

    HydratedEntityRegistration(EntityReference entityReference, EntityKey key, Object instance) {
        this.entityReference = entityReference;
        this.key = key;
        this.instance = instance;
    }

    public EntityReference getEntityReference() {
        return this.entityReference;
    }

    public EntityKey getKey() {
        return this.key;
    }

    public Object getInstance() {
        return this.instance;
    }
}

