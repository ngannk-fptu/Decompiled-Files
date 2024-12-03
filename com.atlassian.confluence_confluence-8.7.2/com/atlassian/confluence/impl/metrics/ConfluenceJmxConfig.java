/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.jmx.JmxConfig
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.metrics;

import io.micrometer.jmx.JmxConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceJmxConfig
implements JmxConfig {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceJmxConfig.class);
    private boolean isJmxEnabled = !Boolean.getBoolean("confluence.jmx.disabled");

    public String get(String key) {
        return System.getProperty(key);
    }

    public boolean isJmxEnabled() {
        return this.isJmxEnabled;
    }

    public String prefix() {
        return "confluence.micrometer.jmx";
    }

    public void setIsJmxEnabled(boolean isJmxEnabled) {
        log.debug("Setting is jmx enabled to {}.", (Object)isJmxEnabled);
        this.isJmxEnabled = isJmxEnabled;
    }
}

