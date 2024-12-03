/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ListenerConfig;
import java.util.EventListener;

public class ListenerConfigReadOnly
extends ListenerConfig {
    public ListenerConfigReadOnly(ListenerConfig config) {
        super(config);
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

