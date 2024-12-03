/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.impl;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginEnablementService;
import com.atlassian.upm.core.PluginModuleNotFoundException;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.core.analytics.event.PluginDisabledAnalyticsEvent;
import com.atlassian.upm.core.analytics.event.PluginEnabledAnalyticsEvent;
import com.atlassian.upm.core.analytics.event.PluginFailedToEnableAnalyticsEvent;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.license.LicensedPlugins;
import com.atlassian.upm.spi.PluginControlHandler;
import java.util.Iterator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginEnablementServiceImpl
implements PluginEnablementService {
    private static final Logger logger = LoggerFactory.getLogger(PluginEnablementServiceImpl.class);
    private final PluginAccessor pluginAccessor;
    private final PluginController pluginController;
    private final AuditLogService auditLogger;
    private final TransactionTemplate txTemplate;
    private final AnalyticsLogger analytics;
    private final PluginRetriever pluginRetriever;
    private final PluginControlHandlerRegistry pluginControlHandlerRegistry;
    private final UserManager userManager;
    private final DefaultHostApplicationInformation hostApplicationInformation;
    private final SenFinder senFinder;
    private final LicensingUsageVerifier licensingUsageVerifier;

    public PluginEnablementServiceImpl(PluginAccessor pluginAccessor, PluginController pluginController, AuditLogService auditLogger, TransactionTemplate txTemplate, AnalyticsLogger analytics, PluginRetriever pluginRetriever, PluginControlHandlerRegistry pluginControlHandlerRegistry, UserManager userManager, DefaultHostApplicationInformation hostApplicationInformation, SenFinder senFinder, LicensingUsageVerifier licensingUsageVerifier) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginController = Objects.requireNonNull(pluginController, "pluginController");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.txTemplate = Objects.requireNonNull(txTemplate, "txTemplate");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.pluginControlHandlerRegistry = Objects.requireNonNull(pluginControlHandlerRegistry, "pluginControlHandlerRegistry");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostLicenseInformation");
        this.senFinder = Objects.requireNonNull(senFinder, "senFinder");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "applicationPluginsManager");
    }

    @Override
    public boolean enablePlugin(final String pluginKey) {
        return (Boolean)this.txTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                Iterator<Plugin> iterator = PluginEnablementServiceImpl.this.pluginRetriever.getPlugin(pluginKey).iterator();
                if (iterator.hasNext()) {
                    Plugin plugin = iterator.next();
                    String pluginName = plugin.getName();
                    boolean enabled = false;
                    boolean handled = false;
                    try {
                        for (PluginControlHandler handler : PluginEnablementServiceImpl.this.getControlHandlers()) {
                            if (!handler.canControl(pluginKey)) continue;
                            handler.enablePlugins(pluginKey);
                            enabled = handler.isPluginEnabled(pluginKey);
                            handled = true;
                            break;
                        }
                        if (!handled) {
                            PluginEnablementServiceImpl.this.pluginController.enablePlugins(new String[]{pluginKey});
                            enabled = PluginEnablementServiceImpl.this.pluginAccessor.isPluginEnabled(pluginKey);
                        }
                    }
                    catch (RuntimeException re) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.enable.plugin.failure", pluginName, pluginKey);
                        PluginEnablementServiceImpl.this.analytics.log(new PluginFailedToEnableAnalyticsEvent(plugin, PluginEnablementServiceImpl.this.hostApplicationInformation, false, LicensedPlugins.usesLicensing(plugin.getPlugin(), PluginEnablementServiceImpl.this.licensingUsageVerifier), PluginEnablementServiceImpl.this.senFinder.findSen(plugin)));
                        throw re;
                    }
                    if (enabled) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.enable.plugin.success", pluginName, pluginKey);
                        PluginEnablementServiceImpl.this.analytics.log(new PluginEnabledAnalyticsEvent(plugin, PluginEnablementServiceImpl.this.hostApplicationInformation, PluginEnablementServiceImpl.this.senFinder.findSen(plugin)));
                    } else {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.enable.plugin.failure", pluginName, pluginKey);
                        PluginEnablementServiceImpl.this.analytics.log(new PluginFailedToEnableAnalyticsEvent(plugin, PluginEnablementServiceImpl.this.hostApplicationInformation, false, LicensedPlugins.usesLicensing(plugin.getPlugin(), PluginEnablementServiceImpl.this.licensingUsageVerifier), PluginEnablementServiceImpl.this.senFinder.findSen(plugin)));
                    }
                    return enabled;
                }
                logger.warn("Attempted enabling a plugin that was not installed: " + pluginKey);
                return false;
            }
        });
    }

    @Override
    public boolean disablePlugin(final String pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            final String pluginName = plugin.getName();
            boolean disabled = (Boolean)this.txTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

                public Boolean doInTransaction() {
                    boolean disabled = false;
                    boolean handled = false;
                    try {
                        for (PluginControlHandler handler : PluginEnablementServiceImpl.this.getControlHandlers()) {
                            if (!handler.canControl(pluginKey)) continue;
                            handler.disablePlugin(pluginKey);
                            disabled = !handler.isPluginEnabled(pluginKey);
                            handled = true;
                            break;
                        }
                        if (!handled) {
                            PluginEnablementServiceImpl.this.pluginController.disablePlugin(pluginKey);
                            disabled = !PluginEnablementServiceImpl.this.pluginAccessor.isPluginEnabled(pluginKey);
                        }
                    }
                    catch (RuntimeException re) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.disable.plugin.failure", pluginName, pluginKey);
                        throw re;
                    }
                    if (disabled) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.disable.plugin.success", pluginName, pluginKey);
                    } else {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.disable.plugin.failure", pluginName, pluginKey);
                    }
                    return disabled;
                }
            });
            if (disabled) {
                this.analytics.log(new PluginDisabledAnalyticsEvent(plugin, this.hostApplicationInformation, this.senFinder.findSen(plugin)));
            }
            return disabled;
        }
        logger.warn("Attempted disabling a plugin that was not installed: " + pluginKey);
        return false;
    }

    @Override
    public boolean enablePluginModule(final String completeKey) {
        return (Boolean)this.txTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                Iterator<Plugin.Module> iterator = PluginEnablementServiceImpl.this.pluginRetriever.getPluginModule(completeKey).iterator();
                if (iterator.hasNext()) {
                    Plugin.Module module = iterator.next();
                    String pluginModuleName = PluginEnablementServiceImpl.this.getModuleNameOrKey(module);
                    try {
                        PluginEnablementServiceImpl.this.pluginController.enablePluginModule(completeKey);
                    }
                    catch (RuntimeException re) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.enable.plugin.module.failure", pluginModuleName, completeKey);
                        throw re;
                    }
                    boolean enabled = PluginEnablementServiceImpl.this.pluginAccessor.isPluginModuleEnabled(completeKey);
                    if (enabled) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.enable.plugin.module.success", pluginModuleName, completeKey);
                    } else {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.enable.plugin.module.failure", pluginModuleName, completeKey);
                    }
                    return enabled;
                }
                logger.warn(String.format("Trying to enable plugin module, but plugin module with key (%s) was not found.", completeKey));
                throw new PluginModuleNotFoundException("Plugin module not found: " + completeKey);
            }
        });
    }

    @Override
    public boolean disablePluginModule(final String completeKey) {
        return (Boolean)this.txTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                Iterator<Plugin.Module> iterator = PluginEnablementServiceImpl.this.pluginRetriever.getPluginModule(completeKey).iterator();
                if (iterator.hasNext()) {
                    boolean disabled;
                    Plugin.Module module = iterator.next();
                    String pluginModuleName = PluginEnablementServiceImpl.this.getModuleNameOrKey(module);
                    try {
                        PluginEnablementServiceImpl.this.pluginController.disablePluginModule(completeKey);
                    }
                    catch (RuntimeException re) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.disable.plugin.module.failure", pluginModuleName, completeKey);
                        throw re;
                    }
                    boolean bl = disabled = !PluginEnablementServiceImpl.this.pluginAccessor.isPluginModuleEnabled(completeKey);
                    if (disabled) {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.disable.plugin.module.success", pluginModuleName, completeKey);
                    } else {
                        PluginEnablementServiceImpl.this.auditLogger.logI18nMessage("upm.auditLog.disable.plugin.module.failure", pluginModuleName, completeKey);
                    }
                    return disabled;
                }
                logger.warn("Attempted disabling a plugin module that could not be located: " + completeKey);
                return false;
            }
        });
    }

    private String getModuleNameOrKey(Plugin.Module module) {
        String name = module.getName();
        return name != null ? name : module.getKey();
    }

    private String getUsername() {
        Iterator<UserProfile> iterator = Option.option(this.userManager.getRemoteUser()).iterator();
        if (iterator.hasNext()) {
            UserProfile u = iterator.next();
            return u.getEmail();
        }
        return "unknown";
    }

    private Iterable<PluginControlHandler> getControlHandlers() {
        return this.pluginControlHandlerRegistry.getHandlers();
    }
}

