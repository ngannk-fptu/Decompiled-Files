/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EntityCopyObserverFactory;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.pretty.MessageHelper;

public final class EntityCopyAllowedLoggedObserver
implements EntityCopyObserver {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EntityCopyAllowedLoggedObserver.class);
    public static final EntityCopyObserverFactory FACTORY_OF_SELF = () -> new EntityCopyAllowedLoggedObserver();
    public static final String SHORT_NAME = "log";
    private Map<String, Integer> countsByEntityName;
    private Map<Object, Set<Object>> managedToMergeEntitiesXref = null;

    private EntityCopyAllowedLoggedObserver() {
    }

    @Override
    public void entityCopyDetected(Object managedEntity, Object mergeEntity1, Object mergeEntity2, EventSource session) {
        String entityName = session.getEntityName(managedEntity);
        LOG.trace(String.format("More than one representation of the same persistent entity being merged for: %s", MessageHelper.infoString(entityName, session.getIdentifier(managedEntity))));
        IdentitySet detachedEntitiesForManaged = null;
        if (this.managedToMergeEntitiesXref == null) {
            this.managedToMergeEntitiesXref = new IdentityHashMap<Object, Set<Object>>();
        } else {
            detachedEntitiesForManaged = this.managedToMergeEntitiesXref.get(managedEntity);
        }
        if (detachedEntitiesForManaged == null) {
            detachedEntitiesForManaged = new IdentitySet();
            this.managedToMergeEntitiesXref.put(managedEntity, detachedEntitiesForManaged);
            this.incrementEntityNameCount(entityName);
        }
        detachedEntitiesForManaged.add((Object)mergeEntity1);
        detachedEntitiesForManaged.add(mergeEntity2);
    }

    private void incrementEntityNameCount(String entityName) {
        Integer countBeforeIncrement = 0;
        if (this.countsByEntityName == null) {
            this.countsByEntityName = new TreeMap<String, Integer>();
        } else {
            countBeforeIncrement = this.countsByEntityName.get(entityName);
            if (countBeforeIncrement == null) {
                countBeforeIncrement = 0;
            }
        }
        this.countsByEntityName.put(entityName, countBeforeIncrement + 1);
    }

    @Override
    public void clear() {
        if (this.managedToMergeEntitiesXref != null) {
            this.managedToMergeEntitiesXref.clear();
            this.managedToMergeEntitiesXref = null;
        }
        if (this.countsByEntityName != null) {
            this.countsByEntityName.clear();
            this.countsByEntityName = null;
        }
    }

    @Override
    public void topLevelMergeComplete(EventSource session) {
        if (this.countsByEntityName != null) {
            for (Map.Entry<String, Integer> entry : this.countsByEntityName.entrySet()) {
                String entityName = entry.getKey();
                int count = entry.getValue();
                LOG.debug(String.format("Summary: number of %s entities with multiple representations merged: %d", entityName, count));
            }
        } else {
            LOG.debug("No entity copies merged.");
        }
        if (this.managedToMergeEntitiesXref != null) {
            for (Map.Entry<Object, Object> entry : this.managedToMergeEntitiesXref.entrySet()) {
                Object managedEntity = entry.getKey();
                Set mergeEntities = (Set)entry.getValue();
                StringBuilder sb = new StringBuilder("Details: merged ").append(mergeEntities.size()).append(" representations of the same entity ").append(MessageHelper.infoString(session.getEntityName(managedEntity), session.getIdentifier(managedEntity))).append(" being merged: ");
                boolean first = true;
                for (Object mergeEntity : mergeEntities) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(this.getManagedOrDetachedEntityString(managedEntity, mergeEntity));
                }
                sb.append("; resulting managed entity: [").append(managedEntity).append(']');
                LOG.debug(sb.toString());
            }
        }
    }

    private String getManagedOrDetachedEntityString(Object managedEntity, Object mergeEntity) {
        if (mergeEntity == managedEntity) {
            return "Managed: [" + mergeEntity + "]";
        }
        return "Detached: [" + mergeEntity + "]";
    }
}

