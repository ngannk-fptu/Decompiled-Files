/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.EntityAction;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.spi.PostCommitDeleteEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.spi.StatisticsImplementor;

public class EntityDeleteAction
extends EntityAction {
    private final Object version;
    private final boolean isCascadeDeleteEnabled;
    private final Object[] state;
    private SoftLock lock;
    private Object[] naturalIdValues;

    public EntityDeleteAction(Serializable id, Object[] state, Object version, Object instance, EntityPersister persister, boolean isCascadeDeleteEnabled, SessionImplementor session) {
        super(session, id, instance, persister);
        this.version = version;
        this.isCascadeDeleteEnabled = isCascadeDeleteEnabled;
        this.state = state;
        this.naturalIdValues = session.getPersistenceContextInternal().getNaturalIdHelper().removeLocalNaturalIdCrossReference(this.getPersister(), this.getId(), state);
    }

    public Object getVersion() {
        return this.version;
    }

    public boolean isCascadeDeleteEnabled() {
        return this.isCascadeDeleteEnabled;
    }

    public Object[] getState() {
        return this.state;
    }

    protected Object[] getNaturalIdValues() {
        return this.naturalIdValues;
    }

    protected SoftLock getLock() {
        return this.lock;
    }

    protected void setLock(SoftLock lock) {
        this.lock = lock;
    }

    @Override
    public void execute() throws HibernateException {
        PersistenceContext persistenceContext;
        EntityEntry entry;
        Object ck;
        Serializable id = this.getId();
        EntityPersister persister = this.getPersister();
        SharedSessionContractImplementor session = this.getSession();
        Object instance = this.getInstance();
        boolean veto = this.preDelete();
        Object version = this.version;
        if (persister.isVersionPropertyGenerated()) {
            version = persister.getVersion(instance);
        }
        if (persister.canWriteToCache()) {
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            ck = cache.generateCacheKey(id, persister, session.getFactory(), session.getTenantIdentifier());
            this.lock = cache.lockItem(session, ck, version);
        } else {
            ck = null;
        }
        if (!this.isCascadeDeleteEnabled && !veto) {
            persister.delete(id, version, instance, session);
        }
        if ((entry = (persistenceContext = session.getPersistenceContextInternal()).removeEntry(instance)) == null) {
            throw new AssertionFailure("possible nonthreadsafe access to session");
        }
        entry.postDelete();
        persistenceContext.removeEntity(entry.getEntityKey());
        persistenceContext.removeProxy(entry.getEntityKey());
        if (persister.canWriteToCache()) {
            persister.getCacheAccessStrategy().remove(session, ck);
        }
        persistenceContext.getNaturalIdHelper().removeSharedNaturalIdCrossReference(persister, id, this.naturalIdValues);
        this.postDelete();
        StatisticsImplementor statistics = this.getSession().getFactory().getStatistics();
        if (statistics.isStatisticsEnabled() && !veto) {
            statistics.deleteEntity(this.getPersister().getEntityName());
        }
    }

    protected boolean preDelete() {
        boolean veto = false;
        EventListenerGroup<PreDeleteEventListener> listenerGroup = this.getFastSessionServices().eventListenerGroup_PRE_DELETE;
        if (listenerGroup.isEmpty()) {
            return veto;
        }
        PreDeleteEvent event = new PreDeleteEvent(this.getInstance(), this.getId(), this.state, this.getPersister(), this.eventSource());
        for (PreDeleteEventListener listener : listenerGroup.listeners()) {
            veto |= listener.onPreDelete(event);
        }
        return veto;
    }

    protected void postDelete() {
        this.getFastSessionServices().eventListenerGroup_POST_DELETE.fireLazyEventOnEachListener(this::newPostDeleteEvent, PostDeleteEventListener::onPostDelete);
    }

    PostDeleteEvent newPostDeleteEvent() {
        return new PostDeleteEvent(this.getInstance(), this.getId(), this.state, this.getPersister(), this.eventSource());
    }

    protected void postCommitDelete(boolean success) {
        EventListenerGroup<PostDeleteEventListener> eventListeners = this.getFastSessionServices().eventListenerGroup_POST_COMMIT_DELETE;
        if (success) {
            eventListeners.fireLazyEventOnEachListener(this::newPostDeleteEvent, PostDeleteEventListener::onPostDelete);
        } else {
            eventListeners.fireLazyEventOnEachListener(this::newPostDeleteEvent, EntityDeleteAction::postCommitDeleteOnUnsuccessful);
        }
    }

    private static void postCommitDeleteOnUnsuccessful(PostDeleteEventListener listener, PostDeleteEvent event) {
        if (listener instanceof PostCommitDeleteEventListener) {
            ((PostCommitDeleteEventListener)listener).onPostDeleteCommitFailed(event);
        } else {
            listener.onPostDelete(event);
        }
    }

    @Override
    public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) throws HibernateException {
        EntityPersister entityPersister = this.getPersister();
        if (entityPersister.canWriteToCache()) {
            EntityDataAccess cache = entityPersister.getCacheAccessStrategy();
            Object ck = cache.generateCacheKey(this.getId(), entityPersister, session.getFactory(), session.getTenantIdentifier());
            cache.unlockItem(session, ck, this.lock);
        }
        this.postCommitDelete(success);
    }

    @Override
    protected boolean hasPostCommitEventListeners() {
        EventListenerGroup<PostDeleteEventListener> group = this.getFastSessionServices().eventListenerGroup_POST_COMMIT_DELETE;
        for (PostDeleteEventListener listener : group.listeners()) {
            if (!listener.requiresPostCommitHandling(this.getPersister())) continue;
            return true;
        }
        return false;
    }
}

