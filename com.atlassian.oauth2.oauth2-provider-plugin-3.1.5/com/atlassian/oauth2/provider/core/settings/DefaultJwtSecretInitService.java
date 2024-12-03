/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.settings.JwtSecretInitService
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.settings;

import com.atlassian.oauth2.provider.api.settings.JwtSecretInitService;
import com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJwtSecretInitService
implements JwtSecretInitService,
LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(DefaultJwtSecretInitService.class);
    private final ProviderSettingsDao providerSettingsDao;

    public DefaultJwtSecretInitService(ProviderSettingsDao providerSettingsDao) {
        this.providerSettingsDao = providerSettingsDao;
    }

    public void init() {
        logger.debug("Initialising provider settings with JWT secret");
        this.providerSettingsDao.saveJwtSecret();
    }

    public void onStart() {
        this.init();
    }

    public void onStop() {
    }
}

