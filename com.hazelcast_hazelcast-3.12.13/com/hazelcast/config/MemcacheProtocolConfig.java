/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

public class MemcacheProtocolConfig {
    private boolean enabled;

    public boolean isEnabled() {
        return this.enabled;
    }

    public MemcacheProtocolConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String toString() {
        return "MemcacheProtocolConfig{enabled=" + this.enabled + "}";
    }
}

