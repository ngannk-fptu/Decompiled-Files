/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ListenerConfig;
import com.hazelcast.quorum.QuorumListener;

public class QuorumListenerConfig
extends ListenerConfig {
    public QuorumListenerConfig() {
    }

    public QuorumListenerConfig(String className) {
        super(className);
    }

    public QuorumListenerConfig(QuorumListener implementation) {
        super(implementation);
    }

    @Override
    public QuorumListener getImplementation() {
        return (QuorumListener)this.implementation;
    }

    public ListenerConfig setImplementation(QuorumListener implementation) {
        return super.setImplementation(implementation);
    }

    @Override
    public boolean isIncludeValue() {
        return false;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public int getId() {
        return 45;
    }
}

