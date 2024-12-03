/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.internal.ProxyVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class WrapVisitor
extends ProxyVisitor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(WrapVisitor.class);
    protected Object entity;
    protected Serializable id;
    private boolean substitute;

    public WrapVisitor(Object entity, Serializable id, EventSource session) {
        super(session);
        this.entity = entity;
        this.id = id;
    }

    public boolean isSubstitutionRequired() {
        return this.substitute;
    }

    public WrapVisitor(EventSource session) {
        super(session);
    }

    @Override
    Object processCollection(Object collection, CollectionType collectionType) throws HibernateException {
        if (collection == null) {
            return null;
        }
        if (collection == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
            return null;
        }
        if (collection instanceof PersistentCollection) {
            PersistentCollection coll = (PersistentCollection)collection;
            EventSource session = this.getSession();
            if (coll.setCurrentSession(session)) {
                this.reattachCollection(coll, collectionType);
            }
            return null;
        }
        return this.processArrayOrNewCollection(collection, collectionType);
    }

    final Object processArrayOrNewCollection(Object collection, CollectionType collectionType) throws HibernateException {
        EventSource session = this.getSession();
        if (collection == null) {
            return null;
        }
        CollectionPersister persister = session.getFactory().getCollectionPersister(collectionType.getRole());
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        if (collectionType.hasHolder()) {
            if (collection == CollectionType.UNFETCHED_COLLECTION) {
                return null;
            }
            PersistentCollection ah = persistenceContext.getCollectionHolder(collection);
            if (ah == null) {
                ah = collectionType.wrap(session, collection);
                persistenceContext.addNewCollection(persister, ah);
                persistenceContext.addCollectionHolder(ah);
            }
            return null;
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(this.entity) && ManagedTypeHelper.asPersistentAttributeInterceptable(this.entity).$$_hibernate_getInterceptor() instanceof EnhancementAsProxyLazinessInterceptor) {
            return null;
        }
        PersistentCollection persistentCollection = collectionType.wrap(session, collection);
        persistenceContext.addNewCollection(persister, persistentCollection);
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Wrapped collection in role: {0}", collectionType.getRole());
        }
        return persistentCollection;
    }

    @Override
    void processValue(int i, Object[] values, Type[] types) {
        Object result = this.processValue(values[i], types[i]);
        if (result != null) {
            this.substitute = true;
            values[i] = result;
        }
    }

    @Override
    Object processComponent(Object component, CompositeType componentType) throws HibernateException {
        if (component != null) {
            Object[] values = componentType.getPropertyValues(component, this.getSession());
            Type[] types = componentType.getSubtypes();
            boolean substituteComponent = false;
            for (int i = 0; i < types.length; ++i) {
                Object result = this.processValue(values[i], types[i]);
                if (result == null) continue;
                values[i] = result;
                substituteComponent = true;
            }
            if (substituteComponent) {
                componentType.setPropertyValues(component, values, EntityMode.POJO);
            }
        }
        return null;
    }

    @Override
    public void process(Object object, EntityPersister persister) throws HibernateException {
        Object[] values = persister.getPropertyValues(object);
        Type[] types = persister.getPropertyTypes();
        this.processEntityPropertyValues(values, types);
        if (this.isSubstitutionRequired()) {
            persister.setPropertyValues(object, values);
        }
    }
}

