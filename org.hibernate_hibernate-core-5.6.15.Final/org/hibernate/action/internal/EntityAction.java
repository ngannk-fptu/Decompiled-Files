/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.action.internal.DelayedPostInsertIdentifier;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.action.spi.Executable;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;

public abstract class EntityAction
implements Executable,
Serializable,
Comparable,
AfterTransactionCompletionProcess {
    private final String entityName;
    private final Serializable id;
    private transient Object instance;
    private transient SharedSessionContractImplementor session;
    private transient EntityPersister persister;
    private transient boolean veto;

    protected EntityAction(SharedSessionContractImplementor session, Serializable id, Object instance, EntityPersister persister) {
        this.entityName = persister.getEntityName();
        this.id = id;
        this.instance = instance;
        this.session = session;
        this.persister = persister;
    }

    public boolean isVeto() {
        return this.veto;
    }

    public void setVeto(boolean veto) {
        this.veto = veto;
    }

    @Override
    public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
        return null;
    }

    @Override
    public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
        return this.needsAfterTransactionCompletion() ? this : null;
    }

    protected abstract boolean hasPostCommitEventListeners();

    protected boolean needsAfterTransactionCompletion() {
        return this.persister.canWriteToCache() || this.hasPostCommitEventListeners();
    }

    public String getEntityName() {
        return this.entityName;
    }

    public final Serializable getId() {
        if (this.id instanceof DelayedPostInsertIdentifier) {
            EntityEntry entry = this.session.getPersistenceContextInternal().getEntry(this.instance);
            Serializable eeId = entry == null ? null : entry.getId();
            return eeId instanceof DelayedPostInsertIdentifier ? null : eeId;
        }
        return this.id;
    }

    public final DelayedPostInsertIdentifier getDelayedId() {
        return DelayedPostInsertIdentifier.class.isInstance(this.id) ? (DelayedPostInsertIdentifier)DelayedPostInsertIdentifier.class.cast(this.id) : null;
    }

    public final Object getInstance() {
        return this.instance;
    }

    public final SharedSessionContractImplementor getSession() {
        return this.session;
    }

    public final EntityPersister getPersister() {
        return this.persister;
    }

    @Override
    public final Serializable[] getPropertySpaces() {
        return this.persister.getPropertySpaces();
    }

    @Override
    public void beforeExecutions() {
        throw new AssertionFailure("beforeExecutions() called for non-collection action");
    }

    public String toString() {
        return StringHelper.unqualify(this.getClass().getName()) + MessageHelper.infoString(this.entityName, this.id);
    }

    public int compareTo(Object other) {
        EntityAction action = (EntityAction)other;
        int roleComparison = this.entityName.compareTo(action.entityName);
        if (roleComparison != 0) {
            return roleComparison;
        }
        return this.persister.getIdentifierType().compare(this.id, action.id);
    }

    @Override
    public void afterDeserialize(SharedSessionContractImplementor session) {
        if (this.session != null || this.persister != null) {
            throw new IllegalStateException("already attached to a session.");
        }
        if (session != null) {
            this.session = session;
            this.persister = session.getFactory().getMetamodel().entityPersister(this.entityName);
            this.instance = session.getPersistenceContext().getEntity(session.generateEntityKey(this.id, this.persister));
        }
    }

    @Deprecated
    protected <T> EventListenerGroup<T> listenerGroup(EventType<T> eventType) {
        return this.getSession().getFactory().getServiceRegistry().getService(EventListenerRegistry.class).getEventListenerGroup(eventType);
    }

    protected EventSource eventSource() {
        return (EventSource)this.getSession();
    }

    protected FastSessionServices getFastSessionServices() {
        return this.session.getFactory().getFastSessionServices();
    }
}

