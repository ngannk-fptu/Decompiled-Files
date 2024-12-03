/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.transformer;

public class TransformerParameters {
    private final String pluginKey;
    private final String moduleKey;

    public TransformerParameters(String pluginKey, String moduleKey) {
        this.pluginKey = pluginKey;
        this.moduleKey = moduleKey;
    }

    @Deprecated
    public TransformerParameters(String pluginKey, String moduleKey, String amdModuleLocation) {
        this(pluginKey, moduleKey);
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getModuleKey() {
        return this.moduleKey;
    }
}

