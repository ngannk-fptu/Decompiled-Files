/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.mirroring.mirror.AnalyticsService
 *  com.atlassian.bitbucket.mirroring.mirror.AnalyticsSettings
 *  com.atlassian.bitbucket.server.ApplicationMode
 *  com.atlassian.bitbucket.server.ApplicationPropertiesService
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.osgi.context.BundleContextAware
 */
package com.atlassian.analytics.client.configuration;

import com.atlassian.bitbucket.mirroring.mirror.AnalyticsService;
import com.atlassian.bitbucket.mirroring.mirror.AnalyticsSettings;
import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.osgi.context.BundleContextAware;

public class BitbucketAnalyticsSettings
implements BundleContextAware,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(BitbucketAnalyticsSettings.class);
    private PluginSettingsFactory pluginSettingsFactory;
    private ServiceTracker serviceTracker;
    private Supplier<AnalyticsSettings> settingSupplier;

    public BitbucketAnalyticsSettings(ApplicationPropertiesService applicationPropertiesService, PluginSettingsFactory pluginSettingsFactory) {
        this(applicationPropertiesService, pluginSettingsFactory, null);
    }

    @VisibleForTesting
    BitbucketAnalyticsSettings(ApplicationPropertiesService applicationPropertiesService, PluginSettingsFactory pluginSettingsFactory, @Nullable ServiceTracker serviceTracker) {
        if (applicationPropertiesService.getMode() == ApplicationMode.MIRROR) {
            this.pluginSettingsFactory = pluginSettingsFactory;
            this.serviceTracker = serviceTracker;
            this.settingSupplier = Suppliers.memoizeWithExpiration((Supplier)new AnalyticsSettingSupplier(), (long)1L, (TimeUnit)TimeUnit.MINUTES);
        }
    }

    @Nonnull
    public Boolean canCollectAnalytics() {
        return ((AnalyticsSettings)this.settingSupplier.get()).canCollectAnalytics();
    }

    @Nonnull
    public String getSupportEntitlementNumber() {
        return ((AnalyticsSettings)this.settingSupplier.get()).getSupportEntitlementNumber();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.serviceTracker = new ServiceTracker(bundleContext, "com.atlassian.bitbucket.mirroring.mirror.AnalyticsService", null);
        this.serviceTracker.open();
    }

    public void destroy() {
        if (this.serviceTracker != null) {
            this.serviceTracker.close();
        }
    }

    @Nonnull
    private String getSetting(Key key) {
        try {
            String setting = (String)this.pluginSettingsFactory.createGlobalSettings().get(key.getKey());
            return setting == null ? "" : setting;
        }
        catch (RuntimeException e) {
            LOG.warn("Couldn't check the analytics settings. This can safely be ignored during plugin shutdown. Detail: {}", (Object)e.getMessage());
            return "";
        }
    }

    private void putSetting(Key key, String newValue) {
        try {
            this.pluginSettingsFactory.createGlobalSettings().put(key.getKey(), (Object)newValue);
        }
        catch (RuntimeException e) {
            LOG.warn("Couldn't change the analytics settings. This can safely be ignored during plugin shutdown. Detail: {}", (Object)e.getMessage());
        }
    }

    private final class AnalyticsSettingSupplier
    implements Supplier<AnalyticsSettings> {
        private AnalyticsSettingSupplier() {
        }

        public AnalyticsSettings get() {
            AnalyticsService service;
            AnalyticsService analyticsService = service = BitbucketAnalyticsSettings.this.serviceTracker == null ? null : (AnalyticsService)BitbucketAnalyticsSettings.this.serviceTracker.getService();
            if (service != null) {
                try {
                    AnalyticsSettings analyticsSettings = service.getAnalyticsSettings();
                    this.saveSettings(analyticsSettings);
                    return analyticsSettings;
                }
                catch (Exception e) {
                    LOG.debug("Could not retrieve analytics settings", (Throwable)e);
                }
            }
            return this.loadSettings();
        }

        private AnalyticsSettings loadSettings() {
            boolean canCollectAnalytics = Boolean.parseBoolean(BitbucketAnalyticsSettings.this.getSetting(Key.CAN_COLLECT));
            String serviceEntitlementNumber = BitbucketAnalyticsSettings.this.getSetting(Key.SEN);
            return new SimpleAnalyticsSettings(canCollectAnalytics, serviceEntitlementNumber);
        }

        private void saveSettings(AnalyticsSettings analyticsSettings) {
            BitbucketAnalyticsSettings.this.putSetting(Key.CAN_COLLECT, String.valueOf(analyticsSettings.canCollectAnalytics()));
            BitbucketAnalyticsSettings.this.putSetting(Key.SEN, analyticsSettings.getSupportEntitlementNumber());
        }
    }

    @VisibleForTesting
    protected static class SimpleAnalyticsSettings
    implements AnalyticsSettings {
        private final boolean canCollect;
        private final String sen;

        public SimpleAnalyticsSettings(boolean canCollect, @Nonnull String sen) {
            this.canCollect = canCollect;
            this.sen = Objects.requireNonNull(sen);
        }

        public boolean canCollectAnalytics() {
            return this.canCollect;
        }

        @Nonnull
        public String getSupportEntitlementNumber() {
            return this.sen;
        }
    }

    public static enum Key {
        SEN("service_entitlement_number"),
        CAN_COLLECT("can_collect_analytics");

        private final String key;

        private Key(String suffix) {
            this.key = "com.atlassian.analytics.client.configuration.." + suffix;
        }

        public String getKey() {
            return this.key;
        }
    }
}

