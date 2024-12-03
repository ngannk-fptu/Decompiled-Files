/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.hibernate.jpa.event.spi.CallbackType;
import org.hibernate.persister.entity.EntityPersister;

public class PostDeleteEventListenerStandardImpl
implements PostDeleteEventListener,
CallbackRegistryConsumer {
    private CallbackRegistry callbackRegistry;

    @Override
    public void injectCallbackRegistry(CallbackRegistry callbackRegistry) {
        this.callbackRegistry = callbackRegistry;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        Object entity = event.getEntity();
        this.callbackRegistry.postRemove(entity);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return this.callbackRegistry.hasRegisteredCallbacks(persister.getMappedClass(), CallbackType.POST_REMOVE);
    }
}

