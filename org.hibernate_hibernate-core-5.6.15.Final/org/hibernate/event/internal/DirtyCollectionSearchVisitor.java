/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.event.internal.AbstractVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.type.CollectionType;

public class DirtyCollectionSearchVisitor
extends AbstractVisitor {
    private final EnhancementAsProxyLazinessInterceptor interceptor;
    private final boolean[] propertyVersionability;
    private boolean dirty;

    public DirtyCollectionSearchVisitor(Object entity, EventSource session, boolean[] propertyVersionability) {
        super(session);
        EnhancementAsProxyLazinessInterceptor interceptor = null;
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(entity) && ManagedTypeHelper.asPersistentAttributeInterceptable(entity).$$_hibernate_getInterceptor() instanceof EnhancementAsProxyLazinessInterceptor) {
            interceptor = (EnhancementAsProxyLazinessInterceptor)((PersistentAttributeInterceptable)entity).$$_hibernate_getInterceptor();
        }
        this.interceptor = interceptor;
        this.propertyVersionability = propertyVersionability;
    }

    public boolean wasDirtyCollectionFound() {
        return this.dirty;
    }

    @Override
    Object processCollection(Object collection, CollectionType type) throws HibernateException {
        if (collection != null) {
            PersistentCollection persistentCollection;
            EventSource session = this.getSession();
            if (type.isArrayType()) {
                persistentCollection = session.getPersistenceContextInternal().getCollectionHolder(collection);
            } else {
                if (this.interceptor != null && !this.interceptor.isAttributeLoaded(type.getName())) {
                    return null;
                }
                persistentCollection = (PersistentCollection)collection;
            }
            if (persistentCollection.isDirty()) {
                this.dirty = true;
                return null;
            }
        }
        return null;
    }

    @Override
    boolean includeEntityProperty(Object[] values, int i) {
        return this.propertyVersionability[i] && super.includeEntityProperty(values, i);
    }
}

