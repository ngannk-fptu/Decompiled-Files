/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.config;

public interface JfrServiceProductSupport {
    public static final String FEATURE_KEY = "com.atlassian.troubleshooting.jfr";
    public static final String FEATURE_KEY_ENABLED = "com.atlassian.troubleshooting.jfr.enabled";
    public static final String FEATURE_KEY_DISABLED = "com.atlassian.troubleshooting.jfr.disabled";

    public boolean isSupported();

    public boolean isRunningByDefault();
}

