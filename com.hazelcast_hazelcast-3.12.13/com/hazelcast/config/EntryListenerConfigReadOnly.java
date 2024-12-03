/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.EntryListener;
import java.util.EventListener;

public class EntryListenerConfigReadOnly
extends EntryListenerConfig {
    public EntryListenerConfigReadOnly(EntryListenerConfig config) {
        super(config);
    }

    @Override
    public EntryListenerConfig setImplementation(EntryListener implementation) {
        throw new UnsupportedOperationException("this config is read-only");
    }

    @Override
    public EntryListenerConfig setLocal(boolean local) {
        throw new UnsupportedOperationException("this config is read-only");
    }

    @Override
    public EntryListenerConfig setIncludeValue(boolean includeValue) {
        throw new UnsupportedOperationException("this config is read-only");
    }

    @Override
    public ListenerConfig setClassName(String className) {
        throw new UnsupportedOperationException("this config is read-only");
    }

    @Override
    public ListenerConfig setImplementation(EventListener implementation) {
        throw new UnsupportedOperationException("this config is read-only");
    }
}

