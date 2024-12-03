/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.internal.license;

import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.confluence.internal.license.EnterpriseFeatureFlag;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class LicenseBasedCachingEnterpriseFeatureFlag
implements EnterpriseFeatureFlag {
    private final LicenseService licenseService;
    private final EventPublisher eventPublisher;
    private final ResettableLazyReference<Boolean> isDcOrExempt = new ResettableLazyReference<Boolean>(){

        protected Boolean create() throws Exception {
            return LicenseBasedCachingEnterpriseFeatureFlag.this.licenseService.isLicensedForDataCenterOrExempt();
        }
    };

    public LicenseBasedCachingEnterpriseFeatureFlag(LicenseService licenseService, EventPublisher eventPublisher) {
        this.licenseService = Objects.requireNonNull(licenseService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @PostConstruct
    public void register() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregister() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public boolean isEnabled() {
        return (Boolean)this.isDcOrExempt.get();
    }

    @EventListener
    public void onLicenseUpdatedEvent(LicenceUpdatedEvent ignored) {
        this.isDcOrExempt.reset();
    }
}

