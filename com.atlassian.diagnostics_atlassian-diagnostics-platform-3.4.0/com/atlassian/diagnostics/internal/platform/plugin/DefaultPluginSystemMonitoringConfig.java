/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.plugin;

import com.atlassian.diagnostics.internal.platform.plugin.PluginSystemMonitoringConfig;

public class DefaultPluginSystemMonitoringConfig
implements PluginSystemMonitoringConfig {
    protected static final String TRAVERSAL_LIMIT_CLASS_CONTEXT_KEY = "atlassian.diagnostics.traversal.limit.class.context";
    protected static final String TRAVERSAL_LIMIT_STACK_TRACE_KEY = "atlassian.diagnostics.traversal.limit.stack.trace";
    protected static final String DISABLE_CLASS_NAME_TO_PLUGIN_KEY = "atlassian.diagnostics.classname.pluginkey.map.disable";
    protected static final int DEFAULT_CLASS_CONTEXT_TRAVERSAL_LIMIT = 10;
    protected static final int DEFAULT_STACK_TRACE_TRAVERSAL_LIMIT = 20;

    @Override
    public boolean classNameToPluginKeyStoreDisabled() {
        return Boolean.parseBoolean(System.getProperty(DISABLE_CLASS_NAME_TO_PLUGIN_KEY, Boolean.FALSE.toString()));
    }

    @Override
    public int classContextTraversalLimit() {
        return Integer.getInteger(TRAVERSAL_LIMIT_CLASS_CONTEXT_KEY, 10);
    }

    @Override
    public int stackTraceTraversalLimit() {
        return Integer.getInteger(TRAVERSAL_LIMIT_STACK_TRACE_KEY, 20);
    }
}

