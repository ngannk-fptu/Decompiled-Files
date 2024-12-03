/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.bytecode.enhance.spi.CollectionTracker;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.bytecode.enhance.spi.interceptor.AbstractLazyLoadInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementHelper;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.SelfDirtinessTracker;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.persister.entity.EntityPersister;

public class LazyAttributeLoadingInterceptor
extends AbstractLazyLoadInterceptor {
    private final Object identifier;
    private final Set<String> lazyFields;
    private Set<String> initializedLazyFields;

    public LazyAttributeLoadingInterceptor(String entityName, Object identifier, Set<String> lazyFields, SharedSessionContractImplementor session) {
        super(entityName, session);
        this.identifier = identifier;
        this.lazyFields = CollectionHelper.toSmallSet(lazyFields);
    }

    @Override
    public Object getIdentifier() {
        return this.identifier;
    }

    @Override
    protected Object handleRead(Object target, String attributeName, Object value) {
        if (!this.isAttributeLoaded(attributeName)) {
            Object loadedValue = this.fetchAttribute(target, attributeName);
            this.attributeInitialized(attributeName);
            return loadedValue;
        }
        return value;
    }

    @Override
    protected Object handleWrite(Object target, String attributeName, Object oldValue, Object newValue) {
        if (!this.isAttributeLoaded(attributeName)) {
            this.attributeInitialized(attributeName);
        }
        return newValue;
    }

    public Object fetchAttribute(Object target, String attributeName) {
        return this.loadAttribute(target, attributeName);
    }

    protected Object loadAttribute(Object target, String attributeName) {
        return EnhancementHelper.performWork(this, (session, isTemporarySession) -> {
            EntityPersister persister = session.getFactory().getMetamodel().entityPersister(this.getEntityName());
            if (isTemporarySession.booleanValue()) {
                Serializable id = persister.getIdentifier(target, null);
                Object[] loadedState = null;
                boolean existsInDb = true;
                session.getPersistenceContextInternal().addEntity(target, Status.READ_ONLY, loadedState, session.generateEntityKey(id, persister), persister.getVersion(target), LockMode.NONE, true, persister, true);
            }
            LazyPropertyInitializer initializer = (LazyPropertyInitializer)((Object)persister);
            Object loadedValue = initializer.initializeLazyProperty(attributeName, target, (SharedSessionContractImplementor)session);
            this.takeCollectionSizeSnapshot(target, attributeName, loadedValue);
            return loadedValue;
        }, this.getEntityName(), attributeName);
    }

    @Override
    public boolean isAttributeLoaded(String fieldName) {
        return !this.isLazyAttribute(fieldName) || this.isInitializedLazyField(fieldName);
    }

    private boolean isLazyAttribute(String fieldName) {
        return this.lazyFields.contains(fieldName);
    }

    private boolean isInitializedLazyField(String fieldName) {
        return this.initializedLazyFields != null && this.initializedLazyFields.contains(fieldName);
    }

    @Override
    public boolean hasAnyUninitializedAttributes() {
        if (this.lazyFields.isEmpty()) {
            return false;
        }
        if (this.initializedLazyFields == null) {
            return true;
        }
        for (String fieldName : this.lazyFields) {
            if (this.initializedLazyFields.contains(fieldName)) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(entityName=" + this.getEntityName() + " ,lazyFields=" + this.lazyFields + ')';
    }

    private void takeCollectionSizeSnapshot(Object target, String fieldName, Object value) {
        if (value instanceof Collection && ManagedTypeHelper.isSelfDirtinessTracker(target)) {
            SelfDirtinessTracker trackerAsSDT = ManagedTypeHelper.asSelfDirtinessTracker(target);
            CollectionTracker tracker = trackerAsSDT.$$_hibernate_getCollectionTracker();
            if (tracker == null) {
                trackerAsSDT.$$_hibernate_clearDirtyAttributes();
                tracker = trackerAsSDT.$$_hibernate_getCollectionTracker();
            }
            if (value instanceof PersistentCollection && !((PersistentCollection)value).wasInitialized()) {
                return;
            }
            tracker.add(fieldName, ((Collection)value).size());
        }
    }

    @Override
    public void attributeInitialized(String name) {
        if (!this.isLazyAttribute(name)) {
            return;
        }
        if (this.initializedLazyFields == null) {
            this.initializedLazyFields = new HashSet<String>();
        }
        this.initializedLazyFields.add(name);
    }

    @Override
    public Set<String> getInitializedLazyAttributeNames() {
        return this.initializedLazyFields == null ? Collections.emptySet() : this.initializedLazyFields;
    }
}

