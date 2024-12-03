/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.event.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.event.spi.EntityCopyObserver;
import org.hibernate.event.spi.EventSource;
import org.hibernate.pretty.MessageHelper;
import org.jboss.logging.Logger;

public class MergeContext
implements Map {
    private static final Logger LOG = Logger.getLogger(MergeContext.class);
    private final EventSource session;
    private final EntityCopyObserver entityCopyObserver;
    private Map<Object, Object> mergeToManagedEntityXref = new IdentityHashMap<Object, Object>(10);
    private Map<Object, Object> managedToMergeEntityXref = new IdentityHashMap<Object, Object>(10);
    private Map<Object, Boolean> mergeEntityToOperatedOnFlagMap = new IdentityHashMap<Object, Boolean>(10);

    public MergeContext(EventSource session, EntityCopyObserver entityCopyObserver) {
        this.session = session;
        this.entityCopyObserver = entityCopyObserver;
    }

    @Override
    public void clear() {
        this.mergeToManagedEntityXref.clear();
        this.managedToMergeEntityXref.clear();
        this.mergeEntityToOperatedOnFlagMap.clear();
    }

    @Override
    public boolean containsKey(Object mergeEntity) {
        if (mergeEntity == null) {
            throw new NullPointerException("null entities are not supported by " + this.getClass().getName());
        }
        return this.mergeToManagedEntityXref.containsKey(mergeEntity);
    }

    @Override
    public boolean containsValue(Object managedEntity) {
        if (managedEntity == null) {
            throw new NullPointerException("null copies are not supported by " + this.getClass().getName());
        }
        return this.managedToMergeEntityXref.containsKey(managedEntity);
    }

    public Set entrySet() {
        return Collections.unmodifiableSet(this.mergeToManagedEntityXref.entrySet());
    }

    public Object get(Object mergeEntity) {
        if (mergeEntity == null) {
            throw new NullPointerException("null entities are not supported by " + this.getClass().getName());
        }
        return this.mergeToManagedEntityXref.get(mergeEntity);
    }

    @Override
    public boolean isEmpty() {
        return this.mergeToManagedEntityXref.isEmpty();
    }

    public Set keySet() {
        return Collections.unmodifiableSet(this.mergeToManagedEntityXref.keySet());
    }

    public Object put(Object mergeEntity, Object managedEntity) {
        return this.put(mergeEntity, managedEntity, Boolean.FALSE);
    }

    public Object put(Object mergeEntity, Object managedEntity, boolean isOperatedOn) {
        if (mergeEntity == null || managedEntity == null) {
            throw new NullPointerException("null merge and managed entities are not supported by " + this.getClass().getName());
        }
        Object oldManagedEntity = this.mergeToManagedEntityXref.put(mergeEntity, managedEntity);
        Boolean oldOperatedOn = this.mergeEntityToOperatedOnFlagMap.put(mergeEntity, isOperatedOn);
        Object oldMergeEntity = this.managedToMergeEntityXref.put(managedEntity, mergeEntity);
        if (oldManagedEntity == null) {
            if (oldMergeEntity != null) {
                this.entityCopyObserver.entityCopyDetected(managedEntity, mergeEntity, oldMergeEntity, this.session);
            }
            if (oldOperatedOn != null) {
                throw new IllegalStateException("MergeContext#mergeEntityToOperatedOnFlagMap contains a merge entity " + this.printEntity(mergeEntity) + ", but MergeContext#mergeToManagedEntityXref does not.");
            }
        } else {
            if (oldManagedEntity != managedEntity) {
                throw new IllegalArgumentException("Error occurred while storing a merge Entity " + this.printEntity(mergeEntity) + ". It was previously associated with managed entity " + this.printEntity(oldManagedEntity) + ". Attempted to replace managed entity with " + this.printEntity(managedEntity));
            }
            if (oldOperatedOn == null) {
                throw new IllegalStateException("MergeContext#mergeToManagedEntityXref contained a merge entity " + this.printEntity(mergeEntity) + ", but MergeContext#mergeEntityToOperatedOnFlagMap did not.");
            }
        }
        return oldManagedEntity;
    }

    public void putAll(Map map) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object mergeEntity) {
        throw new UnsupportedOperationException(String.format("Operation not supported: %s.remove()", this.getClass().getName()));
    }

    @Override
    public int size() {
        return this.mergeToManagedEntityXref.size();
    }

    public Collection values() {
        return Collections.unmodifiableSet(this.managedToMergeEntityXref.keySet());
    }

    public boolean isOperatedOn(Object mergeEntity) {
        if (mergeEntity == null) {
            throw new NullPointerException("null merge entities are not supported by " + this.getClass().getName());
        }
        Boolean isOperatedOn = this.mergeEntityToOperatedOnFlagMap.get(mergeEntity);
        return isOperatedOn == null ? false : isOperatedOn;
    }

    public void setOperatedOn(Object mergeEntity, boolean isOperatedOn) {
        if (mergeEntity == null) {
            throw new NullPointerException("null entities are not supported by " + this.getClass().getName());
        }
        if (!this.mergeEntityToOperatedOnFlagMap.containsKey(mergeEntity) || !this.mergeToManagedEntityXref.containsKey(mergeEntity)) {
            throw new IllegalStateException("called MergeContext#setOperatedOn() for mergeEntity not found in MergeContext");
        }
        this.mergeEntityToOperatedOnFlagMap.put(mergeEntity, isOperatedOn);
    }

    public Map invertMap() {
        return Collections.unmodifiableMap(this.managedToMergeEntityXref);
    }

    private String printEntity(Object entity) {
        if (this.session.getPersistenceContextInternal().getEntry(entity) != null) {
            return MessageHelper.infoString(this.session.getEntityName(entity), this.session.getIdentifier(entity));
        }
        return "[" + entity + "]";
    }
}

