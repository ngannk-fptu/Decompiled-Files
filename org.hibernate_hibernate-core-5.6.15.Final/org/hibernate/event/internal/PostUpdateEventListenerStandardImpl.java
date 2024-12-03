/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.jpa.event.spi.CallbackType;
import org.hibernate.persister.entity.EntityPersister;

public class PostUpdateEventListenerStandardImpl
implements PostUpdateEventListener,
CallbackRegistryConsumer {
    private CallbackRegistry callbackRegistry;

    @Override
    public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        Object entity = event.getEntity();
        EventSource eventSource = event.getSession();
        this.handlePostUpdate(entity, eventSource);
    }

    private void handlePostUpdate(Object entity, EventSource source) {
        EntityEntry entry = source.getPersistenceContextInternal().getEntry(entity);
        if (Status.DELETED != entry.getStatus()) {
            this.callbackRegistry.postUpdate(entity);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return this.callbackRegistry.hasRegisteredCallbacks(persister.getMappedClass(), CallbackType.POST_UPDATE);
    }
}

