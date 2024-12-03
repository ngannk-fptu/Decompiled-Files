/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.AssertionFailure;
import org.hibernate.LockMode;
import org.hibernate.action.internal.EntityIncrementVersionProcess;
import org.hibernate.action.internal.EntityVerifyVersionProcess;
import org.hibernate.classic.Lifecycle;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.persister.entity.EntityPersister;

public class DefaultPostLoadEventListener
implements PostLoadEventListener,
CallbackRegistryConsumer {
    private CallbackRegistry callbackRegistry;

    @Override
    public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        Object entity = event.getEntity();
        this.callbackRegistry.postLoad(entity);
        EventSource session = event.getSession();
        EntityEntry entry = session.getPersistenceContextInternal().getEntry(entity);
        if (entry == null) {
            throw new AssertionFailure("possible non-threadsafe access to the session");
        }
        LockMode lockMode = entry.getLockMode();
        if (LockMode.PESSIMISTIC_FORCE_INCREMENT.equals((Object)lockMode)) {
            EntityPersister persister = entry.getPersister();
            Object nextVersion = persister.forceVersionIncrement(entry.getId(), entry.getVersion(), session);
            entry.forceLocked(entity, nextVersion);
        } else if (LockMode.OPTIMISTIC_FORCE_INCREMENT.equals((Object)lockMode)) {
            EntityIncrementVersionProcess incrementVersion = new EntityIncrementVersionProcess(entity);
            session.getActionQueue().registerProcess(incrementVersion);
        } else if (LockMode.OPTIMISTIC.equals((Object)lockMode)) {
            EntityVerifyVersionProcess verifyVersion = new EntityVerifyVersionProcess(entity);
            session.getActionQueue().registerProcess(verifyVersion);
        }
        this.invokeLoadLifecycle(event, session);
    }

    protected void invokeLoadLifecycle(PostLoadEvent event, EventSource session) {
        if (event.getPersister().implementsLifecycle()) {
            ((Lifecycle)event.getEntity()).onLoad(session, event.getId());
        }
    }
}

