/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistryConfiguration
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdLoggingService
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.confluence.internal.diagnostics.ipd;

import com.atlassian.diagnostics.internal.ipd.IpdMainRegistryConfiguration;
import com.atlassian.diagnostics.ipd.internal.spi.IpdLoggingService;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.Objects;

public class ConfluenceIpdMainRegistryConfiguration
implements IpdMainRegistryConfiguration {
    private final DarkFeatureManager darkFeatureManager;
    private final IpdLoggingService ipdLoggingService;

    public ConfluenceIpdMainRegistryConfiguration(DarkFeatureManager darkFeatureManager, IpdLoggingService ipdLoggingService) {
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
        this.ipdLoggingService = Objects.requireNonNull(ipdLoggingService);
    }

    public String getProductPrefix() {
        return "com.atlassian.confluence";
    }

    public boolean isIpdEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("confluence.in.product.diagnostics.deny").orElse(false) == false;
    }

    public boolean isIpdWipEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("confluence.in.product.diagnostics.wip").orElse(false);
    }

    public void metricUpdated(IpdMetric ipdMetric) {
        this.ipdLoggingService.logMetric(ipdMetric, this.isIpdExtraLoggingEnabled());
    }

    private boolean isIpdExtraLoggingEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("confluence.in.product.diagnostics.extended.logging").orElse(false);
    }
}

