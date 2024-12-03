/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import com.atlassian.upm.api.license.event.PluginLicenseExpiredEvent;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEvent;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEventPublisher;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.PluginLicensesInternal;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.PluginLicenseNotificationChecker;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.DisposableBean;

public class PluginLicenseNotificationCheckerImpl
implements PluginLicenseNotificationChecker,
PluginLicenseGlobalEventPublisher,
UpmProductDataStartupComponent,
DisposableBean {
    private final NotificationCache cache;
    private final PluginLicenseRepository licenseRepository;
    private final PluginRetriever pluginRetriever;
    private final PluginLicenseEventPublisherRegistry registry;
    private final LicensingUsageVerifier licensingUsageVerifier;
    private final ApplicationPluginsManager applicationPluginsManager;

    public PluginLicenseNotificationCheckerImpl(NotificationCache cache, PluginLicenseRepository licenseRepository, PluginRetriever pluginRetriever, PluginLicenseEventPublisherRegistry registry, LicensingUsageVerifier licensingUsageVerifier, ApplicationPluginsManager applicationPluginsManager) {
        this.cache = Objects.requireNonNull(cache, "cache");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.registry = Objects.requireNonNull(registry, "registry");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "licensingUsageVerifier");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "applicationPluginsManager");
    }

    @Override
    public void onStartupWithProductData() {
        this.registry.registerGlobal(this);
    }

    public void destroy() throws Exception {
        this.registry.unregisterGlobal(this);
    }

    @Override
    public void publish(PluginLicenseEvent event) {
        if (!(event instanceof PluginLicenseExpiredEvent)) {
            this.updateLocalPluginLicenseNotifications();
        }
    }

    @Override
    public void publishGlobal(PluginLicenseGlobalEvent event) {
    }

    @Override
    public void updateLocalPluginLicenseNotifications() {
        Set<String> appPluginKeys = this.applicationPluginsManager.getApplicationRelatedPluginKeys();
        List<PluginLicense> licenses = Collections.unmodifiableList(StreamSupport.stream(this.licenseRepository.getPluginLicenses().spliterator(), false).filter(license -> !appPluginKeys.contains(license.getPluginKey())).collect(Collectors.toList()));
        this.cache.setNotifications(NotificationType.EDITION_MISMATCH_PLUGIN_LICENSE, this.pluginKeys(licenses, PluginLicensesInternal.isEditionMismatch()));
        this.cache.setNotifications(NotificationType.EXPIRED_EVALUATION_PLUGIN_LICENSE, this.pluginKeys(licenses, PluginLicensesInternal.isRecentlyExpiredEvaluation(this.pluginRetriever, this.licensingUsageVerifier)));
        this.cache.setNotifications(NotificationType.NEARLY_EXPIRED_EVALUATION_PLUGIN_LICENSE, this.pluginKeys(licenses, PluginLicensesInternal.isNearlyExpiredEvaluation(this.pluginRetriever, this.licensingUsageVerifier)));
        this.cache.setNotifications(NotificationType.MAINTENANCE_EXPIRED_PLUGIN_LICENSE, this.pluginKeys(licenses, PluginLicensesInternal.isMaintenanceRecentlyExpired(this.pluginRetriever, this.licensingUsageVerifier)));
        this.cache.setNotifications(NotificationType.MAINTENANCE_NEARLY_EXPIRED_PLUGIN_LICENSE, this.pluginKeys(licenses, PluginLicensesInternal.isMaintenanceNearlyExpired(this.pluginRetriever, this.licensingUsageVerifier)));
        this.cache.setNotifications(NotificationType.DATA_CENTER_EXPIRED_PLUGIN_LICENSE, this.pluginKeys(licenses, PluginLicensesInternal.isDataCenterLicenseRecentlyExpired(this.pluginRetriever, this.licensingUsageVerifier)));
        this.cache.setNotifications(NotificationType.DATA_CENTER_NEARLY_EXPIRED_PLUGIN_LICENSE, this.pluginKeys(licenses, PluginLicensesInternal.isDataCenterLicenseNearlyExpired(this.pluginRetriever, this.licensingUsageVerifier)));
        DateTime recentExpirationThreshold = new DateTime().minusDays(1);
        licenses.stream().filter(PluginLicenses.hasError(LicenseError.EXPIRED)).forEach(expiredLicense -> this.publishExpiredEvent((PluginLicense)expiredLicense, recentExpirationThreshold));
    }

    private void publishExpiredEvent(PluginLicense expiredLicense, DateTime recentExpirationThreshold) {
        for (DateTime expiryDate : expiredLicense.getExpiryDate()) {
            if (expiryDate.isBefore((ReadableInstant)recentExpirationThreshold)) continue;
            this.registry.publishEvent(new PluginLicenseExpiredEvent(expiredLicense, expiryDate));
        }
    }

    private List<String> pluginKeys(List<PluginLicense> licenses, Predicate<PluginLicense> condition) {
        return Collections.unmodifiableList(licenses.stream().filter(condition).map(PluginLicense::getPluginKey).collect(Collectors.toList()));
    }
}

