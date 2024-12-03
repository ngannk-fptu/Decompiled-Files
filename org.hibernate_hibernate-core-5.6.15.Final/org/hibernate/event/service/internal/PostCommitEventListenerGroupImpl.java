/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.service.internal;

import org.hibernate.event.service.internal.EventListenerGroupImpl;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostCommitDeleteEventListener;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.event.spi.CallbackRegistry;

class PostCommitEventListenerGroupImpl<T>
extends EventListenerGroupImpl<T> {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(PostCommitEventListenerGroupImpl.class);
    private final Class extendedListenerContract;

    public PostCommitEventListenerGroupImpl(EventType<T> eventType, CallbackRegistry callbackRegistry, boolean isJpaBootstrap) {
        super(eventType, callbackRegistry, isJpaBootstrap);
        if (eventType == EventType.POST_COMMIT_DELETE) {
            this.extendedListenerContract = PostCommitDeleteEventListener.class;
        } else if (eventType == EventType.POST_COMMIT_INSERT) {
            this.extendedListenerContract = PostCommitInsertEventListener.class;
        } else if (eventType == EventType.POST_COMMIT_UPDATE) {
            this.extendedListenerContract = PostCommitUpdateEventListener.class;
        } else {
            throw new IllegalStateException("Unexpected usage of PostCommitEventListenerGroupImpl");
        }
    }

    @Override
    public void appendListener(T listener) {
        this.checkAgainstExtendedContract(listener);
        super.appendListener(listener);
    }

    private void checkAgainstExtendedContract(T listener) {
        if (!this.extendedListenerContract.isInstance(listener)) {
            log.warnf("Encountered event listener [%s] for post-commit event [%s] which did not implement the corresponding extended listener contract [%s]", listener.getClass().getName(), this.getEventType().eventName(), this.extendedListenerContract.getName());
        }
    }

    @Override
    public void prependListener(T listener) {
        this.checkAgainstExtendedContract(listener);
        super.prependListener(listener);
    }
}

