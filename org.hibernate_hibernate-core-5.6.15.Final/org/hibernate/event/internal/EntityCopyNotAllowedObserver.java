/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.AssertionFailure;
import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EntityCopyObserverFactory;
import org.hibernate.event.spi.EventSource;
import org.hibernate.pretty.MessageHelper;

public final class EntityCopyNotAllowedObserver
implements EntityCopyObserver {
    public static final String SHORT_NAME = "disallow";
    private static final EntityCopyNotAllowedObserver INSTANCE = new EntityCopyNotAllowedObserver();
    public static final EntityCopyObserverFactory FACTORY_OF_SELF = () -> INSTANCE;

    private EntityCopyNotAllowedObserver() {
    }

    @Override
    public void entityCopyDetected(Object managedEntity, Object mergeEntity1, Object mergeEntity2, EventSource session) {
        if (mergeEntity1 == managedEntity && mergeEntity2 == managedEntity) {
            throw new AssertionFailure("entity1 and entity2 are the same as managedEntity; must be different.");
        }
        String managedEntityString = MessageHelper.infoString(session.getEntityName(managedEntity), session.getIdentifier(managedEntity));
        throw new IllegalStateException("Multiple representations of the same entity " + managedEntityString + " are being merged. " + this.getManagedOrDetachedEntityString(managedEntity, mergeEntity1) + "; " + this.getManagedOrDetachedEntityString(managedEntity, mergeEntity2));
    }

    private String getManagedOrDetachedEntityString(Object managedEntity, Object entity) {
        if (entity == managedEntity) {
            return "Managed: [" + entity + "]";
        }
        return "Detached: [" + entity + "]";
    }

    @Override
    public void clear() {
    }

    @Override
    public void topLevelMergeComplete(EventSource session) {
    }
}

