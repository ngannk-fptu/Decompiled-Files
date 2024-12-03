/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.settings;

public interface ProviderSettingsService {
    public String getJwtSecret();

    public void reset();
}

