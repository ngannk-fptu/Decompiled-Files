/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.basicauth.service;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;
import com.atlassian.plugins.authentication.impl.basicauth.event.BasicAuthUpdatedEvent;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class BasicAuthDao {
    private static final Logger log = LoggerFactory.getLogger(BasicAuthDao.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private final EventPublisher eventPublisher;
    private final ClusterLockService clusterLockService;
    private final I18nResolver i18nResolver;

    @Inject
    public BasicAuthDao(@ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport EventPublisher eventPublisher, @ComponentImport ClusterLockService clusterLockService, @ComponentImport I18nResolver i18nResolver) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.eventPublisher = eventPublisher;
        this.clusterLockService = clusterLockService;
        this.i18nResolver = i18nResolver;
    }

    public void save(BasicAuthConfig newConfig) {
        this.save(currentConfig -> newConfig);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save(UnaryOperator<BasicAuthConfig> update) {
        ClusterLock lock = this.clusterLockService.getLockForName("com.atlassian.plugins.authentication.basicauth");
        if (lock.tryLock()) {
            try {
                PluginSettings settings = this.settings();
                BasicAuthConfig oldConfig = this.get();
                BasicAuthConfig newConfig = (BasicAuthConfig)update.apply(oldConfig);
                settings.put("com.atlassian.plugins.authentication.basicauth.block.requests", (Object)Boolean.toString(newConfig.isBlockRequests()));
                settings.put("com.atlassian.plugins.authentication.basicauth.allowed.paths", new ArrayList<String>(newConfig.getAllowedPaths()));
                settings.put("com.atlassian.plugins.authentication.basicauth.allowed.users", new ArrayList<String>(newConfig.getAllowedUsers()));
                settings.put("com.atlassian.plugins.authentication.basicauth.show.warning.message", (Object)Boolean.toString(newConfig.isShowWarningMessage()));
                this.eventPublisher.publish((Object)new BasicAuthUpdatedEvent(oldConfig, newConfig));
            }
            finally {
                lock.unlock();
            }
        } else {
            throw new IllegalMonitorStateException(this.i18nResolver.getText("authentication.basic.auth.multiple.saves.error"));
        }
    }

    public BasicAuthConfig get() {
        try {
            PluginSettings settings = this.settings();
            boolean blockRequests = "true".equals(settings.get("com.atlassian.plugins.authentication.basicauth.block.requests"));
            List allowedPaths = (List)settings.get("com.atlassian.plugins.authentication.basicauth.allowed.paths");
            List allowedUsers = (List)settings.get("com.atlassian.plugins.authentication.basicauth.allowed.users");
            boolean showWarningMessage = Optional.ofNullable(settings.get("com.atlassian.plugins.authentication.basicauth.show.warning.message")).map("true"::equals).orElse(true);
            return new BasicAuthConfig(blockRequests, allowedPaths, allowedUsers, showWarningMessage);
        }
        catch (Exception e) {
            log.info("Could not read basic authentication settings", (Throwable)e);
            return BasicAuthConfig.DEFAULT;
        }
    }

    @Nonnull
    private PluginSettings settings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }

    static interface Config {
        public static final String PREFIX = "com.atlassian.plugins.authentication.basicauth";
        public static final String BLOCK_REQUESTS = "com.atlassian.plugins.authentication.basicauth.block.requests";
        public static final String ALLOWED_PATHS = "com.atlassian.plugins.authentication.basicauth.allowed.paths";
        public static final String ALLOWED_USERS = "com.atlassian.plugins.authentication.basicauth.allowed.users";
        public static final String SHOW_WARNING_MESSAGE = "com.atlassian.plugins.authentication.basicauth.show.warning.message";
    }
}

