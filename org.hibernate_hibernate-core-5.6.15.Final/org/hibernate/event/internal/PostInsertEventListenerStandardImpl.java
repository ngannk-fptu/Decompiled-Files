/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.jpa.event.spi.CallbackType;
import org.hibernate.persister.entity.EntityPersister;

public class PostInsertEventListenerStandardImpl
implements PostInsertEventListener,
CallbackRegistryConsumer {
    private CallbackRegistry callbackRegistry;

    @Override
    public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object entity = event.getEntity();
        this.callbackRegistry.postCreate(entity);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return this.callbackRegistry.hasRegisteredCallbacks(persister.getMappedClass(), CallbackType.POST_PERSIST);
    }
}

