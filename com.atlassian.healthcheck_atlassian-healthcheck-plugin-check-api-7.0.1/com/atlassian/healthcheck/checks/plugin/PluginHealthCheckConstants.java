/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.healthcheck.checks.plugin.PluginHealthCheckMode;

public final class PluginHealthCheckConstants {
    public static final String DISABLED_PLUGINS_HEALTHCHECK = "Disabled Plugins";
    public static final String DISABLED_PLUGIN_MODULES_HEALTHCHECK = "Disabled Plugin Modules";
    public static final String DISABLED_BY_DEFAULT_PLUGINS_HEALTHCHECK = "Disabled By Default Plugins";
    public static final String DISABLED_BY_DEFAULT_PLUGIN_MODULES_HEALTHCHECK = "Disabled By Default Plugin Modules";
    public static final String MODE_PREFIX = "mode=";
    public static final String DISABLED_MODE = "(mode=" + PluginHealthCheckMode.DISABLED.name() + ")";
    public static final String HARD_FAIL_MODE = "(mode=" + PluginHealthCheckMode.HARD_FAIL.name() + ")";
    public static final String DEFAULT_NO_FAIL_MODE = "(mode=" + PluginHealthCheckMode.DEFAULT_NO_FAIL.name() + ")";
    public static final String FORCE_NO_FAIL_MODE = "(mode=" + PluginHealthCheckMode.FORCE_NO_FAIL.name() + ")";
    public static final String PASS = "PASS";
    public static final String FAILED = "FAILED";
    public static final String WHITELISTED = "WHITELISTED";

    private PluginHealthCheckConstants() {
    }
}

