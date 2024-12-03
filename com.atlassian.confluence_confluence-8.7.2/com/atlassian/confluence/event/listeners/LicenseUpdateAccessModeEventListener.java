/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.event.events.analytics.MaintenanceReadOnlyEvent;
import com.atlassian.confluence.event.events.cluster.ClusterAccessModeEvent;
import com.atlassian.confluence.event.listeners.ClusterAccessModeEventListener;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseUpdateAccessModeEventListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterAccessModeEventListener.class);
    private final AccessModeManager accessModeManager;
    private final EventPublisher eventPublisher;
    private final SettingsManager settingsManager;
    private static final String BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY = "confluence.bypass.data.center.check";

    public LicenseUpdateAccessModeEventListener(AccessModeManager accessModeManager, EventPublisher eventPublisher, SettingsManager settingsManager) {
        this.accessModeManager = accessModeManager;
        this.eventPublisher = eventPublisher;
        this.settingsManager = settingsManager;
    }

    @EventListener
    public void onLicenseChanged(LicenceUpdatedEvent event) {
        ProductLicense license = event.getLicense().getProductLicense(Product.CONFLUENCE);
        if (!license.isClusteringEnabled() && !"true".equals(license.getProperty(BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY))) {
            try {
                log.info("Force disabling read-only mode and the maintenance banner, as the license applied does not support maintenance features.");
                if (AccessMode.READ_ONLY.equals((Object)this.accessModeManager.getAccessMode())) {
                    this.accessModeManager.updateAccessMode(AccessMode.READ_WRITE);
                    this.eventPublisher.publish((Object)new ClusterAccessModeEvent(this, AccessMode.READ_WRITE));
                }
                this.eventPublisher.publish((Object)new MaintenanceReadOnlyEvent(false));
                Settings settings = new Settings(this.settingsManager.getGlobalSettings());
                settings.setMaintenanceBannerMessageOn(false);
                this.settingsManager.updateGlobalSettings(settings);
            }
            catch (ConfigurationException e) {
                log.error("Cannot update the access mode", e.getCause());
            }
        }
    }
}

