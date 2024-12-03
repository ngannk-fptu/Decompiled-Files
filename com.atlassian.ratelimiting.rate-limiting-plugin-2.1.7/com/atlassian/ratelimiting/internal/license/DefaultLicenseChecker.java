/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginEvent
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseChangedEvent
 *  com.atlassian.sal.api.license.LicenseHandler
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.license;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginEvent;
import com.atlassian.ratelimiting.internal.plugin.PluginChecker;
import com.atlassian.ratelimiting.license.LicenseChecker;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseChangedEvent;
import com.atlassian.sal.api.license.LicenseHandler;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLicenseChecker
implements LicenseChecker {
    private static final String ATLASSIAN_DEV_MODE_PROPERTY_KEY = "atlassian.dev.mode";
    private static final Logger logger = LoggerFactory.getLogger(DefaultLicenseChecker.class);
    private final LicenseHandler licenseHandler;
    private final PluginChecker pluginChecker;
    private final EventPublisher eventPublisher;
    private final ResettableLazyReference<Boolean> isDataCenterLicensed = new ResettableLazyReference<Boolean>(){

        protected Boolean create() {
            return DefaultLicenseChecker.this.isDevMode() || DefaultLicenseChecker.this.licensesAreDataCenter();
        }
    };

    public DefaultLicenseChecker(LicenseHandler licenseHandler, PluginChecker pluginChecker, EventPublisher eventPublisher) {
        this.licenseHandler = licenseHandler;
        this.pluginChecker = pluginChecker;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    private boolean isDevMode() {
        return Boolean.getBoolean(ATLASSIAN_DEV_MODE_PROPERTY_KEY);
    }

    private boolean licensesAreDataCenter() {
        try {
            Collection licenseDetails = this.licenseHandler.getAllProductLicenses();
            return !licenseDetails.isEmpty() && licenseDetails.stream().allMatch(BaseLicenseDetails::isDataCenter);
        }
        catch (Exception e) {
            logger.warn("Failed to check licenses. Disabling rate limiting", (Throwable)e);
            return false;
        }
    }

    @Override
    public boolean isDataCenterLicensed() {
        return (Boolean)this.isDataCenterLicensed.get();
    }

    @Override
    @EventListener
    public void onLicenseChanged(LicenseChangedEvent event) {
        this.isDataCenterLicensed.reset();
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent pluginEnabledEvent) {
        if (this.pluginChecker.isRateLimitingPlugin((PluginEvent)pluginEnabledEvent)) {
            this.isDataCenterLicensed.reset();
        }
    }
}

