/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.mac;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import com.atlassian.upm.notification.PluginLicenseNotificationChecker;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.DisposableBean;

public class HostLicenseUpdatedEventListener
implements UpmProductDataStartupComponent,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final HostLicenseEventReader hostLicenseEventReader;
    private final PluginLicenseNotificationChecker notificationChecker;
    private final PluginLicenseRepository licenseRepository;
    private final HostLicenseProvider hostLicenseProvider;
    private final AtomicBoolean initialized;

    public HostLicenseUpdatedEventListener(EventPublisher eventPublisher, HostLicenseEventReader hostLicenseEventReader, PluginLicenseNotificationChecker notificationChecker, PluginLicenseRepository licenseRepository, HostLicenseProvider hostLicenseProvider) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.hostLicenseEventReader = hostLicenseEventReader;
        this.notificationChecker = Objects.requireNonNull(notificationChecker, "notificationChecker");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        this.initialized = new AtomicBoolean(false);
    }

    @Override
    public void onStartupWithProductData() {
        this.eventPublisher.register((Object)this);
        this.initialized.set(true);
        this.invalidateCaches();
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onAnyEvent(Object event) {
        if (this.hostLicenseEventReader.isHostLicenseUpdated(event)) {
            this.updateLicense();
        }
    }

    private void updateLicense() {
        this.invalidateCaches();
        this.notificationChecker.updateLocalPluginLicenseNotifications();
    }

    private void invalidateCaches() {
        this.licenseRepository.invalidateCache();
        this.hostLicenseProvider.invalidateCache();
    }
}

