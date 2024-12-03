/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core.impl;

import com.atlassian.healthcheck.core.ExtendedHealthCheck;
import com.atlassian.healthcheck.core.HealthCheck;
import com.atlassian.healthcheck.core.HealthStatus;

public class PluginSuppliedHealthCheck
implements ExtendedHealthCheck {
    private final HealthCheck delegate;
    protected final String key;
    private final String name;
    private final String description;
    private final String tag;
    private final int timeOut;

    public PluginSuppliedHealthCheck(HealthCheck delegate, String key, String name, String description, String tag, int timeOut) {
        this.delegate = delegate;
        this.key = key;
        this.name = name;
        this.description = description;
        this.tag = tag;
        this.timeOut = timeOut;
    }

    @Override
    public HealthStatus check() {
        return this.delegate.check();
    }

    @Override
    public String getName() {
        return null == this.name || this.name.isEmpty() ? this.key : this.name;
    }

    @Override
    public String getDescription() {
        return null == this.description || this.description.isEmpty() ? String.format("This was provided by plugin '%s' via class '%s'", this.key, this.delegate.getClass().getName()) : this.description;
    }

    @Override
    public int getTimeOut() {
        return this.timeOut;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getTag() {
        return this.tag;
    }
}

