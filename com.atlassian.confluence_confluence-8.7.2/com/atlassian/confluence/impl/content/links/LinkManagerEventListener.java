/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.content.links;

import com.atlassian.confluence.links.LinkManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class LinkManagerEventListener {
    private final LinkManager linkManager;
    private final EventListenerRegistrar eventListenerRegistrar;

    LinkManagerEventListener(LinkManager linkManager, EventListenerRegistrar eventListenerRegistrar) {
        this.linkManager = linkManager;
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
    public void onLinkManagerEvent(VisitorEvent event) {
        event.visit(this.linkManager);
    }

    public static interface VisitorEvent {
        public void visit(LinkManager var1);
    }
}

