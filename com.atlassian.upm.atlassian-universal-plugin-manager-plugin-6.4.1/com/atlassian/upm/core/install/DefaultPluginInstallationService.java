/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.install;

import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.PluginState;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginFactory;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.PluginWithDependenciesInstallResult;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.SafeModeException;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.core.analytics.event.PluginFailedToEnableAnalyticsEvent;
import com.atlassian.upm.core.analytics.event.PluginInstalledAnalyticsEvent;
import com.atlassian.upm.core.analytics.event.PluginUninstalledAnalyticsEvent;
import com.atlassian.upm.core.install.ObrPluginInstallHandler;
import com.atlassian.upm.core.install.PluginInstallHandlerRegistry;
import com.atlassian.upm.core.install.UnknownPluginTypeException;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.license.LicensedPlugins;
import com.atlassian.upm.spi.PluginControlHandler;
import com.atlassian.upm.spi.PluginInstallException;
import com.atlassian.upm.spi.PluginInstallHandler;
import com.atlassian.upm.spi.PluginInstallResult;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPluginInstallationService
implements PluginInstallationService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPluginInstallationService.class);
    private final AnalyticsLogger analytics;
    private final AuditLogService auditLogger;
    private final I18nResolver i18nResolver;
    private final PluginController pluginController;
    private final PluginFactory pluginFactory;
    private final PluginInstallHandlerRegistry pluginInstallHandlerRegistry;
    private final PluginControlHandlerRegistry pluginControlHandlerRegistry;
    private final PluginRetriever pluginRetriever;
    private final SafeModeAccessor safeMode;
    private final TransactionTemplate txTemplate;
    private final DefaultHostApplicationInformation hostApplicationInformation;
    private final SenFinder senFinder;
    protected final LicensingUsageVerifier licensingUsageVerifier;
    private Function<String, Option<String>> filterContentType = new Function<String, Option<String>>(){

        public Option<String> apply(String type) {
            if (type.startsWith("application/octet-stream")) {
                return Option.none();
            }
            return Option.some(type);
        }
    };

    public DefaultPluginInstallationService(AnalyticsLogger analytics, AuditLogService auditLogger, I18nResolver i18nResolver, PluginController pluginController, PluginFactory pluginFactory, PluginInstallHandlerRegistry pluginInstallHandlerRegistry, PluginControlHandlerRegistry pluginControlHandlerRegistry, PluginRetriever pluginRetriever, SafeModeAccessor safeMode, TransactionTemplate txTemplate, DefaultHostApplicationInformation hostApplicationInformation, SenFinder senFinder, LicensingUsageVerifier licensingUsageVerifier) {
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.pluginController = Objects.requireNonNull(pluginController, "pluginController");
        this.pluginFactory = Objects.requireNonNull(pluginFactory, "pluginFactory");
        this.pluginInstallHandlerRegistry = Objects.requireNonNull(pluginInstallHandlerRegistry, "pluginInstallHandlerRegistry");
        this.pluginControlHandlerRegistry = Objects.requireNonNull(pluginControlHandlerRegistry, "pluginControlHandlerRegistry");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
        this.txTemplate = Objects.requireNonNull(txTemplate, "txTemplate");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
        this.senFinder = Objects.requireNonNull(senFinder, "senFinder");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "applicationPluginsManager");
    }

    @Override
    public PluginWithDependenciesInstallResult install(File artifactFile, String source, Option<String> contentType, boolean recheckForUpdates) {
        return this.execute(artifactFile, source, contentType, ExecutionType.INSTALL);
    }

    @Override
    public PluginWithDependenciesInstallResult update(File artifactFile, String source, Option<String> contentType, boolean recheckForUpdates) {
        return this.execute(artifactFile, source, contentType, ExecutionType.UPDATE);
    }

    private PluginWithDependenciesInstallResult execute(File artifactFile, String source, Option<String> contentType, ExecutionType executionType) {
        String i18nFailureMessage = "upm.auditLog." + executionType.name + ".plugin.failure";
        Option<String> useType = contentType.flatMap(this.filterContentType);
        try {
            Iterator<PluginInstallHandler> iterator = this.getInstallHandler(artifactFile, useType).iterator();
            if (iterator.hasNext()) {
                PluginInstallHandler handler = iterator.next();
                if (this.safeMode.isSafeMode() && !(handler instanceof ObrPluginInstallHandler)) {
                    this.auditLogger.logI18nMessage(i18nFailureMessage, artifactFile.getName(), source);
                    throw new SafeModeException("Install plugin is not allowed when system is in safe mode");
                }
                PluginInstallResult result = handler.installPlugin(artifactFile, useType);
                this.logResult(result, this.pluginFactory.createPlugin(result.getPlugin()));
                return PluginWithDependenciesInstallResult.from(result, this.pluginFactory);
            }
        }
        catch (SafeModeException e) {
            this.auditLogger.logI18nMessage(i18nFailureMessage, artifactFile.getName(), source);
            throw e;
        }
        catch (PluginInstallException e) {
            this.auditLogger.logI18nMessage(i18nFailureMessage, artifactFile.getName(), source);
            logger.warn("Plugin installation failed: " + e.getMessage());
            if (e.isStackTraceSignificant()) {
                logger.warn(e.toString(), (Throwable)e);
            } else {
                logger.debug(e.toString(), (Throwable)e);
            }
            if (this.isMySQLMaxAllowedPacketError(e)) {
                throw new PluginInstallException("Plugin installation failed: MySQL max_allowed_packet is too low and plugin could not be inserted to the database", Option.some("upm.pluginInstall.error.install.failed.max_allowed_packet"), e.getCause(), true);
            }
            throw e;
        }
        catch (Exception e) {
            logger.warn("Plugin installation failed: " + e.getMessage());
            logger.warn(e.toString(), (Throwable)e);
            this.auditLogger.logI18nMessage(i18nFailureMessage, artifactFile.getName(), source);
            throw new PluginInstallException("Unexpected error during plugin installation failure", e);
        }
        this.auditLogger.logI18nMessage(i18nFailureMessage, artifactFile.getName(), source);
        throw new UnknownPluginTypeException("Unable to install plugin - file was not a supported plugin artifact type");
    }

    private boolean isMySQLMaxAllowedPacketError(Throwable e) {
        if (e == null || e.getMessage() == null) {
            return false;
        }
        if (e.getMessage().contains("max_allowed_packet")) {
            return true;
        }
        return this.isMySQLMaxAllowedPacketError(e.getCause());
    }

    private Option<PluginInstallHandler> getInstallHandler(File file, Option<String> contentType) {
        for (PluginInstallHandler handler : this.pluginInstallHandlerRegistry.getHandlers()) {
            if (!handler.canInstallPlugin(file, contentType)) continue;
            return Option.some(handler);
        }
        return Option.none();
    }

    private Iterable<PluginControlHandler> getControlHandlers() {
        return this.pluginControlHandlerRegistry.getHandlers();
    }

    private void logResult(PluginInstallResult result, Plugin installedPlugin) {
        for (com.atlassian.plugin.Plugin dep : result.getDependencies()) {
            this.logInstalled(dep, installedPlugin);
        }
        if (!Iterables.isEmpty(result.getDependencies())) {
            String groupDescription = this.i18nResolver.getText("upm.auditLog.install.plugins.dependencies", new Serializable[]{result.getPlugin().getName()});
            Iterable pluginDescs = Iterables.transform(result.getDependencies(), (Function)new Function<com.atlassian.plugin.Plugin, String>(){

                public String apply(com.atlassian.plugin.Plugin plugin) {
                    return plugin.getName() + " (" + plugin.getKey() + ")";
                }
            });
            this.auditLogger.logI18nMessage("upm.auditLog.install.plugins.success.withDescription", Joiner.on((String)", ").join(pluginDescs), groupDescription);
        }
        this.logInstalled(result.getPlugin(), installedPlugin);
        if (installedPlugin.isEnabledByDefault() && !installedPlugin.isEnabled()) {
            this.analytics.log(new PluginFailedToEnableAnalyticsEvent(installedPlugin, this.hostApplicationInformation, true, LicensedPlugins.usesLicensing(installedPlugin.getPlugin(), this.licensingUsageVerifier), this.senFinder.findSen(installedPlugin)));
        }
    }

    private void logInstalled(com.atlassian.plugin.Plugin plugin, Plugin installedPlugin) {
        String name = plugin.getName();
        String key = plugin.getKey();
        String version = plugin.getPluginInformation().getVersion();
        this.auditLogger.logI18nMessage("upm.auditLog.install.plugin.success", name, key, version);
        this.analytics.log(new PluginInstalledAnalyticsEvent(plugin, this.hostApplicationInformation, this.senFinder.findSen(installedPlugin)));
    }

    @Override
    public void uninstall(Plugin plugin) {
        this.uninstallInternal(plugin);
    }

    protected boolean uninstallInternal(final Plugin plugin) {
        final String pluginKey = plugin.getKey();
        final String pluginName = plugin.getName();
        final String pluginVersion = plugin.getVersion();
        boolean connect = plugin.isConnect();
        boolean uninstalled = (Boolean)this.txTemplate.execute((TransactionCallback)new TransactionCallback<Boolean>(){

            public Boolean doInTransaction() {
                try {
                    boolean handled = false;
                    for (PluginControlHandler handler : DefaultPluginInstallationService.this.getControlHandlers()) {
                        if (!handler.canControl(pluginKey)) continue;
                        handler.uninstall(plugin.getPlugin());
                        handled = true;
                        break;
                    }
                    if (!handled) {
                        DefaultPluginInstallationService.this.pluginController.uninstall(plugin.getPlugin());
                    }
                }
                catch (RuntimeException re) {
                    DefaultPluginInstallationService.this.auditLogger.logI18nMessage("upm.auditLog.uninstall.plugin.failure", pluginName, pluginKey, pluginVersion);
                    throw re;
                }
                Iterator<Plugin> iterator = DefaultPluginInstallationService.this.pluginRetriever.getPlugin(pluginKey).iterator();
                if (iterator.hasNext()) {
                    Plugin result = iterator.next();
                    PluginState pluginState = result.getPluginState();
                    PluginRestartState restartState = result.getRestartState();
                    if (PluginState.UNINSTALLED.equals((Object)pluginState)) {
                        DefaultPluginInstallationService.this.auditLogger.logI18nMessage("upm.auditLog.uninstall.plugin.success", pluginName, pluginKey, pluginVersion);
                        return true;
                    }
                    if (PluginRestartState.REMOVE.equals((Object)restartState)) {
                        DefaultPluginInstallationService.this.auditLogger.logI18nMessage("upm.auditLog.uninstall.plugin.requires.restart", pluginName, pluginKey, pluginVersion);
                        return true;
                    }
                    DefaultPluginInstallationService.this.auditLogger.logI18nMessage("upm.auditLog.uninstall.plugin.failure", pluginName, pluginKey, pluginVersion);
                    return false;
                }
                DefaultPluginInstallationService.this.auditLogger.logI18nMessage("upm.auditLog.uninstall.plugin.success", pluginName, pluginKey, pluginVersion);
                return true;
            }
        });
        if (uninstalled) {
            this.analytics.log(new PluginUninstalledAnalyticsEvent(plugin, this.hostApplicationInformation, this.senFinder.findSen(plugin)));
        }
        return uninstalled;
    }

    protected AnalyticsLogger getAnalyticsLogger() {
        return this.analytics;
    }

    private static enum ExecutionType {
        INSTALL("install"),
        UPDATE("update");

        private final String name;

        private ExecutionType(String name) {
            this.name = name;
        }
    }
}

