/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.AbstractEntityInsertAction;
import org.hibernate.action.internal.DelayedPostInsertIdentifier;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.spi.StatisticsImplementor;

public class EntityIdentityInsertAction
extends AbstractEntityInsertAction {
    private final boolean isDelayed;
    private final EntityKey delayedEntityKey;
    private EntityKey entityKey;
    private Serializable generatedId;

    public EntityIdentityInsertAction(Object[] state, Object instance, EntityPersister persister, boolean isVersionIncrementDisabled, SharedSessionContractImplementor session, boolean isDelayed) {
        super(isDelayed ? EntityIdentityInsertAction.generateDelayedPostInsertIdentifier() : null, state, instance, isVersionIncrementDisabled, persister, session);
        this.isDelayed = isDelayed;
        this.delayedEntityKey = isDelayed ? this.generateDelayedEntityKey() : null;
    }

    @Override
    public void execute() throws HibernateException {
        this.nullifyTransientReferencesIfNotAlready();
        EntityPersister persister = this.getPersister();
        SharedSessionContractImplementor session = this.getSession();
        Object instance = this.getInstance();
        this.setVeto(this.preInsert());
        if (!this.isVeto()) {
            this.generatedId = persister.insert(this.getState(), instance, session);
            if (persister.hasInsertGeneratedProperties()) {
                persister.processInsertGeneratedProperties(this.generatedId, instance, this.getState(), session);
            }
            persister.setIdentifier(instance, this.generatedId, session);
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            persistenceContext.registerInsertedKey(this.getPersister(), this.generatedId);
            this.entityKey = session.generateEntityKey(this.generatedId, persister);
            persistenceContext.checkUniqueness(this.entityKey, this.getInstance());
        }
        this.postInsert();
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        if (statistics.isStatisticsEnabled() && !this.isVeto()) {
            statistics.insertEntity(this.getPersister().getEntityName());
        }
        this.markExecuted();
    }

    @Override
    public boolean needsAfterTransactionCompletion() {
        return this.hasPostCommitEventListeners();
    }

    @Override
    protected boolean hasPostCommitEventListeners() {
        EventListenerGroup<PostInsertEventListener> group = this.getFastSessionServices().eventListenerGroup_POST_COMMIT_INSERT;
        for (PostInsertEventListener listener : group.listeners()) {
            if (!listener.requiresPostCommitHandling(this.getPersister())) continue;
            return true;
        }
        return false;
    }

    @Override
    public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
        this.postCommitInsert(success);
    }

    protected void postInsert() {
        if (this.isDelayed) {
            this.eventSource().getPersistenceContextInternal().replaceDelayedEntityIdentityInsertKeys(this.delayedEntityKey, this.generatedId);
        }
        this.getFastSessionServices().eventListenerGroup_POST_INSERT.fireLazyEventOnEachListener(this::newPostInsertEvent, PostInsertEventListener::onPostInsert);
    }

    PostInsertEvent newPostInsertEvent() {
        return new PostInsertEvent(this.getInstance(), this.generatedId, this.getState(), this.getPersister(), this.eventSource());
    }

    protected void postCommitInsert(boolean success) {
        this.getFastSessionServices().eventListenerGroup_POST_COMMIT_INSERT.fireLazyEventOnEachListener(this::newPostInsertEvent, success ? PostInsertEventListener::onPostInsert : this::postCommitInsertOnFailure);
    }

    private void postCommitInsertOnFailure(PostInsertEventListener listener, PostInsertEvent event) {
        if (listener instanceof PostCommitInsertEventListener) {
            ((PostCommitInsertEventListener)listener).onPostInsertCommitFailed(event);
        } else {
            listener.onPostInsert(event);
        }
    }

    protected boolean preInsert() {
        EventListenerGroup<PreInsertEventListener> listenerGroup = this.getFastSessionServices().eventListenerGroup_PRE_INSERT;
        if (listenerGroup.isEmpty()) {
            return false;
        }
        boolean veto = false;
        PreInsertEvent event = new PreInsertEvent(this.getInstance(), null, this.getState(), this.getPersister(), this.eventSource());
        for (PreInsertEventListener listener : listenerGroup.listeners()) {
            veto |= listener.onPreInsert(event);
        }
        return veto;
    }

    public final Serializable getGeneratedId() {
        return this.generatedId;
    }

    protected void setGeneratedId(Serializable generatedId) {
        this.generatedId = generatedId;
    }

    @Deprecated
    public EntityKey getDelayedEntityKey() {
        return this.delayedEntityKey;
    }

    @Override
    public boolean isEarlyInsert() {
        return !this.isDelayed;
    }

    @Override
    protected EntityKey getEntityKey() {
        return this.entityKey != null ? this.entityKey : this.delayedEntityKey;
    }

    protected void setEntityKey(EntityKey entityKey) {
        this.entityKey = entityKey;
    }

    private static DelayedPostInsertIdentifier generateDelayedPostInsertIdentifier() {
        return new DelayedPostInsertIdentifier();
    }

    protected EntityKey generateDelayedEntityKey() {
        if (!this.isDelayed) {
            throw new AssertionFailure("cannot request delayed entity-key for early-insert post-insert-id generation");
        }
        return this.getSession().generateEntityKey(this.getDelayedId(), this.getPersister());
    }
}

