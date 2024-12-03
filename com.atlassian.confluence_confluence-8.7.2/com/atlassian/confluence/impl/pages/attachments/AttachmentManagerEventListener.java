/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.pages.attachments;

import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class AttachmentManagerEventListener {
    private final AttachmentManagerInternal attachmentManager;
    private final EventListenerRegistrar eventListenerRegistrar;

    AttachmentManagerEventListener(AttachmentManagerInternal attachmentManager, EventListenerRegistrar eventListenerRegistrar) {
        this.attachmentManager = attachmentManager;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    @PostConstruct
    void registerListeners() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    void unregisterListeners() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onLabelManagerEvent(VisitorEvent event) {
        event.visit(this.attachmentManager);
    }

    public static interface VisitorEvent {
        public void visit(AttachmentManagerInternal var1);
    }
}

