/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.analytics.events;

public enum ServerResourceCacheInvalidationCause {
    PROGRAMMATIC_TRIGGER("programmaticTrigger"),
    PLUGIN_DISABLED_EVENT("pluginDisabled"),
    PLUGIN_ENABLED_EVENT("pluginEnabled"),
    PLUGIN_WEBRESOURCE_MODULE_DISABLED("pluginWebResourceModuleDisabled"),
    PLUGIN_WEBRESOURCE_MODULE_ENABLED("pluginWebResourceModuleEnabled");

    private final String value;

    private ServerResourceCacheInvalidationCause(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.getValue();
    }
}

