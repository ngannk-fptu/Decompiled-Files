/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.module;

import com.atlassian.plugin.PluginParseException;

public class ModuleClassNotFoundException
extends PluginParseException {
    private final String className;
    private final String pluginKey;
    private final String moduleKey;
    private String errorMsg;

    public ModuleClassNotFoundException(String className, String pluginKey, String moduleKey, Exception ex, String errorMsg) {
        super(ex);
        this.className = className;
        this.pluginKey = pluginKey;
        this.moduleKey = moduleKey;
        this.errorMsg = errorMsg;
    }

    public String getClassName() {
        return this.className;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    @Override
    public String getMessage() {
        return this.errorMsg;
    }
}

