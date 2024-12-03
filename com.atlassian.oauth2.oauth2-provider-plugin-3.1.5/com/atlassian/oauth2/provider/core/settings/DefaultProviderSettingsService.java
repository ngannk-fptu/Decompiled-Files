/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsService
 */
package com.atlassian.oauth2.provider.core.settings;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao;
import com.atlassian.oauth2.provider.api.settings.ProviderSettingsService;

public class DefaultProviderSettingsService
implements ProviderSettingsService {
    private final ProviderSettingsDao providerSettingsDao;
    private final CachedReference<String> jwtSecret;

    public DefaultProviderSettingsService(ProviderSettingsDao providerSettingsDao, CacheFactory cacheFactory) {
        this.providerSettingsDao = providerSettingsDao;
        this.jwtSecret = cacheFactory.getCachedReference("oauth2.provider.jwt.secret", () -> ((ProviderSettingsDao)providerSettingsDao).getJwtSecret(), new CacheSettingsBuilder().remote().replicateAsynchronously().replicateViaInvalidation().build());
    }

    public String getJwtSecret() {
        return (String)this.jwtSecret.get();
    }

    public void reset() {
        this.providerSettingsDao.resetJwtSecret();
        this.jwtSecret.reset();
        this.jwtSecret.get();
    }
}

