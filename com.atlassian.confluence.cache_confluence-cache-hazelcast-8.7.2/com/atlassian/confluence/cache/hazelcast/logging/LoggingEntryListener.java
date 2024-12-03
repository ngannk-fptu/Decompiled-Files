/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.EntryEvent
 *  com.hazelcast.core.EntryListener
 *  com.hazelcast.core.MapEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.hazelcast.logging;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEntryListener<K, V>
implements EntryListener<K, V> {
    private static final Logger log = LoggerFactory.getLogger(LoggingEntryListener.class);

    public void entryAdded(EntryEvent<K, V> event) {
        log.debug("entryAdded: {}", event);
    }

    public void entryRemoved(EntryEvent<K, V> event) {
        log.debug("entryRemoved: {}", event);
    }

    public void entryUpdated(EntryEvent<K, V> event) {
        log.debug("entryUpdated: {}", event);
    }

    public void entryEvicted(EntryEvent<K, V> event) {
        log.debug("entryEvicted: {}", event);
    }

    public void mapEvicted(MapEvent event) {
        log.debug("mapEvicted: {}", (Object)event);
    }

    public void mapCleared(MapEvent event) {
        log.debug("mapCleared: {}", (Object)event);
    }
}

