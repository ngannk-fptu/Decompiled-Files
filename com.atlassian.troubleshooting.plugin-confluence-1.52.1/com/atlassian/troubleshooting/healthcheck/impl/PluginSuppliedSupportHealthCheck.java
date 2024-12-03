/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SoftLaunch;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.DefaultSupportHealthStatus;

public class PluginSuppliedSupportHealthCheck
implements ExtendedSupportHealthCheck {
    protected final String key;
    private final SupportHealthCheck delegate;
    private final String name;
    private final String description;
    private final int timeOut;
    private final String tag;
    private final String helpPathKey;
    private final String className;
    private final I18nResolver i18nResolver;
    private boolean isEnabled;

    public PluginSuppliedSupportHealthCheck(SupportHealthCheck delegate, String key, String name, String description, int timeOut, String tag, String helpPathKey, String className, I18nResolver i18nResolver, boolean isEnabled) {
        this.delegate = delegate;
        this.key = key;
        this.name = name;
        this.description = description;
        this.timeOut = timeOut;
        this.tag = tag;
        this.helpPathKey = helpPathKey;
        this.className = className;
        this.i18nResolver = i18nResolver;
        this.isEnabled = isEnabled;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public boolean isNodeSpecific() {
        return this.delegate.isNodeSpecific();
    }

    @Override
    public SupportHealthStatus check() {
        if (this.isEnabled()) {
            return this.delegate.check();
        }
        return new DefaultSupportHealthStatus(false, this.i18nResolver.getText("healthcheck.disabled"), System.currentTimeMillis(), Application.Unknown, null, SupportHealthStatus.Severity.DISABLED, "");
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
    public String getHelpPathKey() {
        return this.helpPathKey;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public boolean isSoftLaunch() {
        return this.delegate.getClass().getAnnotation(SoftLaunch.class) != null;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}

