/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.settings;

public interface ProviderSettingsDao {
    public void saveJwtSecret();

    public void resetJwtSecret();

    public String getJwtSecret();
}

