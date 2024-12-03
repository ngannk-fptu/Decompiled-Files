/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EntityCopyObserverFactory;
import org.hibernate.event.spi.EventSource;

public final class EntityCopyAllowedObserver
implements EntityCopyObserver {
    public static final String SHORT_NAME = "allow";
    private static final EntityCopyObserver INSTANCE = new EntityCopyAllowedObserver();
    public static final EntityCopyObserverFactory FACTORY_OF_SELF = () -> INSTANCE;

    private EntityCopyAllowedObserver() {
    }

    @Override
    public void entityCopyDetected(Object managedEntity, Object mergeEntity1, Object mergeEntity2, EventSource session) {
    }

    @Override
    public void clear() {
    }

    @Override
    public void topLevelMergeComplete(EventSource session) {
    }
}

