/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

public interface PluginEnablementService {
    public boolean enablePlugin(String var1);

    public boolean disablePlugin(String var1);

    public boolean enablePluginModule(String var1);

    public boolean disablePluginModule(String var1);
}

