/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.pocketknife.api.lifecycle.modules;

import com.atlassian.plugin.ModuleCompleteKey;

public interface ModuleRegistrationHandle {
    public void unregister();

    public Iterable<ModuleCompleteKey> getModules();

    public ModuleRegistrationHandle union(ModuleRegistrationHandle var1);
}

