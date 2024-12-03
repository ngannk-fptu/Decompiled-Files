/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.upm.api.license.event.PluginLicenseEvent
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.license;

import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.plugins.gatekeeper.license.LicenseInfo;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={LifecycleAware.class})
public class AddonLicenseManagerImpl
implements AddonLicenseManager,
LifecycleAware {
    private static final Logger logger = LoggerFactory.getLogger(AddonLicenseManagerImpl.class);
    private static final String UPM_LICENSE_PREFIX = "com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseRepository:licenses:";
    private static final String OLD_PLUGIN_KEY = "hu.metainf.plugin.confluence.ultimate-permission-manager";
    private final LicenseService licenseService;
    private final EventPublisher eventPublisher;
    private final PluginAccessor pluginAccessor;
    private final PluginController pluginController;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ResettableLazyReference<LicenseInfo> licenseCache;

    @Autowired
    public AddonLicenseManagerImpl(LicenseService licenseService, EventPublisher eventPublisher, PluginAccessor pluginAccessor, PluginController pluginController, PluginSettingsFactory pluginSettingsFactory) {
        this.licenseService = licenseService;
        this.eventPublisher = eventPublisher;
        this.pluginAccessor = pluginAccessor;
        this.pluginController = pluginController;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.licenseCache = new ResettableLazyReference<LicenseInfo>(){

            protected LicenseInfo create() throws Exception {
                return AddonLicenseManagerImpl.this.createLicenseInfo();
            }
        };
    }

    private static String hash(String key) {
        int maxKeyLength = 100;
        if (key.length() > maxKeyLength) {
            String keyHash = DigestUtils.md5Hex((String)key);
            String keptOriginalKey = key.substring(0, maxKeyLength - keyHash.length());
            return keptOriginalKey + keyHash;
        }
        return key;
    }

    private LicenseInfo createLicenseInfo() {
        Plugin oldPlugin = this.pluginAccessor.getPlugin(OLD_PLUGIN_KEY);
        if (this.licenseService.isLicensedForDataCenterOrExempt()) {
            logger.trace("DC Licensed", (Object)OLD_PLUGIN_KEY);
            if (oldPlugin != null) {
                logger.trace("Found existing plugin [{}]...disabling in favour of gatekeeper", (Object)OLD_PLUGIN_KEY);
                this.pluginController.disablePlugin(oldPlugin.getKey());
            }
            return LicenseInfo.create(true, true);
        }
        String oldPluginLicense = (String)this.pluginSettingsFactory.createGlobalSettings().get(AddonLicenseManagerImpl.hash("com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseRepository:licenses:hu.metainf.plugin.confluence.ultimate-permission-manager"));
        if (oldPlugin != null && oldPluginLicense != null) {
            PluginState oldPluginState = oldPlugin.getPluginState();
            logger.trace("Found existing plugin [{}] with existing plugin license and state [{}]", (Object)OLD_PLUGIN_KEY, (Object)oldPluginState);
            if (oldPluginState.equals((Object)PluginState.ENABLED) || oldPluginState.equals((Object)PluginState.ENABLING)) {
                logger.trace("Using existing plugin", (Object)OLD_PLUGIN_KEY);
                return LicenseInfo.create(false, false);
            }
        }
        if (oldPlugin != null) {
            logger.trace("Found existing plugin [{}] with no plugin license...disabling in favour of gatekeeper", (Object)OLD_PLUGIN_KEY);
            this.pluginController.disablePlugin(oldPlugin.getKey());
        }
        return LicenseInfo.create(true, false);
    }

    @PostConstruct
    public void postconstruct() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void predestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void onStart() {
        this.licenseCache.get();
    }

    public void onStop() {
    }

    private void resetLicenseIf(Supplier<Boolean> condition) {
        if (condition.get().booleanValue()) {
            logger.trace("Resetting gatekeeper license cache");
            this.licenseCache.reset();
        }
    }

    @EventListener
    public void onLicenseEvent(PluginLicenseEvent pluginLicenseEvent) {
        logger.trace("PluginLicenseEvent detected");
        this.resetLicenseIf(() -> OLD_PLUGIN_KEY.equals(pluginLicenseEvent.getPluginKey()));
    }

    @EventListener
    public void onLicenseEvent(LicenceUpdatedEvent licenseEvent) {
        logger.trace("LicenceUpdatedEvent detected");
        this.resetLicenseIf(() -> true);
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent pluginEvent) {
        logger.trace("PluginEnabledEvent detected");
        this.resetLicenseIf(() -> OLD_PLUGIN_KEY.equals(pluginEvent.getPlugin().getKey()));
    }

    @EventListener
    public void onPluginDisabled(PluginDisabledEvent pluginEvent) {
        logger.trace("PluginDisabledEvent detected");
        this.resetLicenseIf(() -> OLD_PLUGIN_KEY.equals(pluginEvent.getPlugin().getKey()));
    }

    @Override
    public LicenseInfo getLicenseInfo() {
        try {
            return (LicenseInfo)this.licenseCache.get();
        }
        catch (Exception e) {
            logger.error("Error checking license, turn on debug logging to see full stacktrace: {}", (Object)e.getMessage());
            logger.debug("", (Throwable)e);
            return LicenseInfo.invalid(e);
        }
    }
}

