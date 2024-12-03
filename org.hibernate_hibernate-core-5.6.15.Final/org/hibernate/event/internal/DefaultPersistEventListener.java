/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.util.IdentityHashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.PersistentObjectException;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.AbstractSaveEventListener;
import org.hibernate.event.internal.EntityState;
import org.hibernate.event.internal.EventUtil;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.id.ForeignGenerator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public class DefaultPersistEventListener
extends AbstractSaveEventListener
implements PersistEventListener,
CallbackRegistryConsumer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultPersistEventListener.class);

    @Override
    protected CascadingAction getCascadeAction() {
        return CascadingActions.PERSIST;
    }

    @Override
    public void onPersist(PersistEvent event) throws HibernateException {
        this.onPersist(event, new IdentityHashMap(10));
    }

    @Override
    public void onPersist(PersistEvent event, Map createCache) throws HibernateException {
        EntityPersister persister;
        String entityName;
        Object entity;
        EventSource source = event.getSession();
        Object object = event.getObject();
        if (object instanceof HibernateProxy) {
            LazyInitializer li = ((HibernateProxy)object).getHibernateLazyInitializer();
            if (li.isUninitialized()) {
                if (li.getSession() == source) {
                    return;
                }
                throw new PersistentObjectException("uninitialized proxy passed to persist()");
            }
            entity = li.getImplementation();
        } else {
            entity = object;
        }
        if (event.getEntityName() != null) {
            entityName = event.getEntityName();
        } else {
            entityName = source.bestGuessEntityName(entity);
            event.setEntityName(entityName);
        }
        EntityEntry entityEntry = source.getPersistenceContextInternal().getEntry(entity);
        EntityState entityState = EntityState.getEntityState(entity, entityName, entityEntry, source, true);
        if (entityState == EntityState.DETACHED && ForeignGenerator.class.isInstance((persister = source.getFactory().getEntityPersister(entityName)).getIdentifierGenerator())) {
            if (LOG.isDebugEnabled() && persister.getIdentifier(entity, source) != null) {
                LOG.debug("Resetting entity id attribute to null for foreign generator");
            }
            persister.setIdentifier(entity, null, source);
            entityState = EntityState.getEntityState(entity, entityName, entityEntry, source, true);
        }
        switch (entityState) {
            case DETACHED: {
                throw new PersistentObjectException("detached entity passed to persist: " + EventUtil.getLoggableName(event.getEntityName(), entity));
            }
            case PERSISTENT: {
                this.entityIsPersistent(event, createCache);
                break;
            }
            case TRANSIENT: {
                this.entityIsTransient(event, createCache);
                break;
            }
            case DELETED: {
                entityEntry.setStatus(Status.MANAGED);
                entityEntry.setDeletedState(null);
                event.getSession().getActionQueue().unScheduleDeletion(entityEntry, event.getObject());
                this.entityIsDeleted(event, createCache);
                break;
            }
            default: {
                throw new ObjectDeletedException("deleted entity passed to persist", null, EventUtil.getLoggableName(event.getEntityName(), entity));
            }
        }
    }

    protected void entityIsPersistent(PersistEvent event, Map createCache) {
        LOG.trace("Ignoring persistent instance");
        EventSource source = event.getSession();
        Object entity = source.getPersistenceContextInternal().unproxy(event.getObject());
        EntityPersister persister = source.getEntityPersister(event.getEntityName(), entity);
        if (createCache.put(entity, entity) == null) {
            this.justCascade(createCache, source, entity, persister);
        }
    }

    private void justCascade(Map createCache, EventSource source, Object entity, EntityPersister persister) {
        this.cascadeBeforeSave(source, persister, entity, createCache);
        this.cascadeAfterSave(source, persister, entity, createCache);
    }

    protected void entityIsTransient(PersistEvent event, Map createCache) {
        LOG.trace("Saving transient instance");
        EventSource source = event.getSession();
        Object entity = source.getPersistenceContextInternal().unproxy(event.getObject());
        if (createCache.put(entity, entity) == null) {
            this.saveWithGeneratedId(entity, event.getEntityName(), createCache, source, false);
        }
    }

    private void entityIsDeleted(PersistEvent event, Map createCache) {
        EventSource source = event.getSession();
        Object entity = source.getPersistenceContextInternal().unproxy(event.getObject());
        EntityPersister persister = source.getEntityPersister(event.getEntityName(), entity);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("un-scheduling entity deletion [%s]", MessageHelper.infoString(persister, persister.getIdentifier(entity, source), source.getFactory()));
        }
        if (createCache.put(entity, entity) == null) {
            this.justCascade(createCache, source, entity, persister);
        }
    }
}

