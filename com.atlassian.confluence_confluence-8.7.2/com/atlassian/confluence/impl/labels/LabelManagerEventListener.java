/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.labels;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class LabelManagerEventListener {
    private final LabelManager labelManager;
    private final EventListenerRegistrar eventListenerRegistrar;

    LabelManagerEventListener(LabelManager labelManager, EventListenerRegistrar eventListenerRegistrar) {
        this.labelManager = labelManager;
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
        event.visit(this.labelManager);
    }

    public static interface VisitorEvent {
        public void visit(LabelManager var1);
    }
}

