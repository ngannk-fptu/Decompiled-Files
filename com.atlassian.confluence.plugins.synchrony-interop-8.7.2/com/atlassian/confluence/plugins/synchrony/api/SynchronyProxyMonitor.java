/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.synchrony.api;

public interface SynchronyProxyMonitor {
    public static final String SYNCHRONY_PROXY_CONTEXT_PATH = "synchrony-proxy";
    public static final String SYNCHRONY_PROXY_HEALTHCHECK_DISABLED = "synchrony.proxy.healthcheck.disabled";

    public boolean isSynchronyProxyUp();

    public void startHealthcheck();
}

