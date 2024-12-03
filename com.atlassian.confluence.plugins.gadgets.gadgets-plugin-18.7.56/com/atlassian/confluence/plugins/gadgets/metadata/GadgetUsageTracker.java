/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.confluence.plugins.gadgets.events.GadgetDirectoryRetrievalEvent;
import com.atlassian.confluence.plugins.gadgets.events.GadgetInfoRestFetchEvent;
import com.atlassian.confluence.plugins.gadgets.events.GadgetMacroRenderedEvent;
import com.atlassian.confluence.plugins.gadgets.events.GadgetPlaceholderRenderedEvent;
import com.atlassian.event.api.EventListener;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GadgetUsageTracker {
    private static final Logger log = LoggerFactory.getLogger(GadgetUsageTracker.class);
    private final Set<URI> gadgetsInUse = Collections.newSetFromMap(new ConcurrentHashMap());
    private final AtomicBoolean preventGadgetCacheClearance = new AtomicBoolean(false);

    boolean shouldGadgetsCacheFlushingBePrevented() {
        return this.preventGadgetCacheClearance.get();
    }

    Set<URI> getGadgetUrisInUse() {
        return Sets.newHashSet(this.gadgetsInUse);
    }

    private void recordGadgetUsage(URI gadgetUri) {
        if (this.gadgetsInUse.add(gadgetUri)) {
            log.debug("{} gadgets now in use", (Object)this.gadgetsInUse.size());
        }
    }

    private void preventGadgetCacheClearance() {
        this.preventGadgetCacheClearance.set(true);
        log.debug("Preventing further gadget cache clearance");
    }

    @EventListener
    public void gadgetMacroRendered(GadgetMacroRenderedEvent event) {
        log.debug("Gadget macro rendered for uri={}", (Object)event.getGadgetUri());
        this.recordGadgetUsage(event.getGadgetUri());
    }

    @EventListener
    public void gadgetPlaceholderRendered(GadgetPlaceholderRenderedEvent event) {
        log.debug("Gadget placeholder rendered for uri={}", (Object)event.getGadgetUri());
        this.recordGadgetUsage(event.getGadgetUri());
    }

    @EventListener
    public void gadgetDirectoryFetched(GadgetDirectoryRetrievalEvent event) {
        log.debug("Gadget directory retrieved for {} gadgets", (Object)event.getGadgetCount());
        this.preventGadgetCacheClearance();
    }

    @EventListener
    public void gadgetInfoRestFetched(GadgetInfoRestFetchEvent event) {
        log.debug("Gadget info REST fetched {} gadgets", (Object)event.getGadgetCount());
        this.preventGadgetCacheClearance();
    }
}

