/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.internal.EvictVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.EvictEvent;
import org.hibernate.event.spi.EvictEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public class DefaultEvictEventListener
implements EvictEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultEvictEventListener.class);

    @Override
    public void onEvict(EvictEvent event) throws HibernateException {
        Object object = event.getObject();
        if (object == null) {
            throw new NullPointerException("null passed to Session.evict()");
        }
        EventSource source = event.getSession();
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        if (object instanceof HibernateProxy) {
            Object entity;
            LazyInitializer li = ((HibernateProxy)object).getHibernateLazyInitializer();
            Serializable id = li.getInternalIdentifier();
            if (id == null) {
                throw new IllegalArgumentException("Could not determine identifier of proxy passed to evict()");
            }
            EntityPersister persister = source.getFactory().getEntityPersister(li.getEntityName());
            EntityKey key = source.generateEntityKey(id, persister);
            persistenceContext.removeProxy(key);
            if (!li.isUninitialized() && (entity = persistenceContext.removeEntity(key)) != null) {
                EntityEntry e = persistenceContext.removeEntry(entity);
                this.doEvict(entity, key, e.getPersister(), event.getSession());
            }
            li.unsetSession();
        } else {
            EntityEntry e = persistenceContext.getEntry(object);
            if (e != null) {
                this.doEvict(object, e.getEntityKey(), e.getPersister(), source);
            } else {
                EntityPersister persister = null;
                String entityName = persistenceContext.getSession().guessEntityName(object);
                if (entityName != null) {
                    try {
                        persister = persistenceContext.getSession().getFactory().getEntityPersister(entityName);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (persister == null) {
                    throw new IllegalArgumentException("Non-entity object instance passed to evict : " + object);
                }
            }
        }
    }

    protected void doEvict(Object object, EntityKey key, EntityPersister persister, EventSource session) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Evicting {0}", MessageHelper.infoString(persister));
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        if (persister.hasNaturalIdentifier()) {
            persistenceContext.getNaturalIdHelper().handleEviction(object, persister, key.getIdentifier());
        }
        if (persister.hasCollections()) {
            new EvictVisitor(session, object).process(object, persister);
        }
        persistenceContext.removeEntity(key);
        persistenceContext.removeEntry(object);
        Cascade.cascade(CascadingActions.EVICT, CascadePoint.AFTER_EVICT, session, persister, object);
    }
}

