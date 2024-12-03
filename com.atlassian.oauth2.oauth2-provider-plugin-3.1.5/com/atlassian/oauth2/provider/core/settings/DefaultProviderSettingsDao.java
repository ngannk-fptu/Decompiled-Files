/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.settings;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao;
import com.atlassian.oauth2.provider.core.credentials.ClientCredentialsGenerator;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProviderSettingsDao
implements ProviderSettingsDao {
    private static final Logger logger = LoggerFactory.getLogger(DefaultProviderSettingsDao.class);
    private static final String JWT_SECRET_KEY = "com.atlassian.plugins.oauth2.provider.jwt.secret";
    private static final ClientCredentialsGenerator.Length CREDENTIALS_LENGTH = ClientCredentialsGenerator.Length.SIXTY_FOUR;
    @VisibleForTesting
    static final String PROVIDER_SETTINGS_SERVICE_LOCK = "com.atlassian.plugins.oauth2.provider.settings.service.lock";
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ClusterLockService clusterLockService;
    private final ClientCredentialsGenerator clientCredentialsGenerator;

    public DefaultProviderSettingsDao(PluginSettingsFactory pluginSettingsFactory, ClusterLockService clusterLockService, ClientCredentialsGenerator clientCredentialsGenerator) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.clusterLockService = clusterLockService;
        this.clientCredentialsGenerator = clientCredentialsGenerator;
    }

    public void saveJwtSecret() {
        PluginSettings pluginSettings = this.pluginSettings();
        this.executeWithLock(() -> {
            if (pluginSettings.get(JWT_SECRET_KEY) == null) {
                logger.debug("Generating new JWT secret");
                pluginSettings.put(JWT_SECRET_KEY, (Object)this.clientCredentialsGenerator.generate(CREDENTIALS_LENGTH));
            }
        });
    }

    private void executeWithLock(Runnable callback) throws InterruptedException {
        ClusterLock lock = this.clusterLockService.getLockForName(PROVIDER_SETTINGS_SERVICE_LOCK);
        if (lock.tryLock(SystemProperty.GLOBAL_CLUSTER_LOCK_TIMEOUT_SECONDS.getValue().intValue(), TimeUnit.SECONDS)) {
            try {
                callback.run();
            }
            finally {
                lock.unlock();
            }
        } else {
            throw new IllegalMonitorStateException("Unable to obtain lock");
        }
    }

    public void resetJwtSecret() {
        PluginSettings pluginSettings = this.pluginSettings();
        logger.debug("Resetting JWT secret");
        this.executeWithLock(() -> pluginSettings.put(JWT_SECRET_KEY, (Object)this.clientCredentialsGenerator.generate(CREDENTIALS_LENGTH)));
    }

    public String getJwtSecret() {
        Object jwtSecret = this.pluginSettings().get(JWT_SECRET_KEY);
        if (jwtSecret == null) {
            logger.warn("JWT secret stored in database was null. You need to call saveJwtSecret first");
            throw new RuntimeException("Failed to get JWT secret: JWT secret stored in database was null. You need to call saveJwtSecret first");
        }
        return jwtSecret.toString();
    }

    @Nonnull
    private PluginSettings pluginSettings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }
}

