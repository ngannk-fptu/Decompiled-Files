/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.healthcheck.checks.plugin.ConfigProvider;

public class SystemPropertyConfigProviderImpl
implements ConfigProvider {
    @Override
    public boolean isEnabled(String sysprop) {
        return Boolean.getBoolean(sysprop);
    }
}

