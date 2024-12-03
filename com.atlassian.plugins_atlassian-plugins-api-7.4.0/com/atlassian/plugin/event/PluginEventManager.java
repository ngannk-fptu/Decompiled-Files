/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.event;

public interface PluginEventManager {
    public void register(Object var1);

    public void unregister(Object var1);

    public void broadcast(Object var1);
}

