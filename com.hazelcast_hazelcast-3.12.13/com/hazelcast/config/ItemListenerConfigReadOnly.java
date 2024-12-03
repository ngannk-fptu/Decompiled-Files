/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.ItemListener;
import java.util.EventListener;

public class ItemListenerConfigReadOnly
extends ItemListenerConfig {
    public ItemListenerConfigReadOnly(ItemListenerConfig config) {
        super(config);
    }

    @Override
    public ItemListenerConfig setImplementation(ItemListener implementation) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public ItemListenerConfig setIncludeValue(boolean includeValue) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public ListenerConfig setClassName(String className) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public ListenerConfig setImplementation(EventListener implementation) {
        throw new UnsupportedOperationException("This config is read-only");
    }
}

