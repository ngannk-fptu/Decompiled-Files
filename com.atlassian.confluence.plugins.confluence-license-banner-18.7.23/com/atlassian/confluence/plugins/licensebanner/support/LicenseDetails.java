/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.licensebanner.support;

public class LicenseDetails {
    private final int daysBeforeLicenseExpiry;
    private final int daysBeforeMaintenanceExpiry;
    private boolean showLicenseExpiryBanner;
    private boolean showMaintenanceExpiryBanner;
    private String renewUrl;
    private String salesUrl;

    public LicenseDetails(int daysBeforeLicenseExpiry, int daysBeforeMaintenanceExpiry) {
        this.daysBeforeLicenseExpiry = daysBeforeLicenseExpiry;
        this.daysBeforeMaintenanceExpiry = daysBeforeMaintenanceExpiry;
    }

    public int getDaysBeforeLicenseExpiry() {
        return this.daysBeforeLicenseExpiry;
    }

    public int getDaysBeforeMaintenanceExpiry() {
        return this.daysBeforeMaintenanceExpiry;
    }

    public boolean isShowLicenseExpiryBanner() {
        return this.showLicenseExpiryBanner;
    }

    public void setShowLicenseExpiryBanner(boolean showLicenseExpiryBanner) {
        this.showLicenseExpiryBanner = showLicenseExpiryBanner;
    }

    public boolean isShowMaintenanceExpiryBanner() {
        return this.showMaintenanceExpiryBanner;
    }

    public void setShowMaintenanceExpiryBanner(boolean showMaintenanceExpiryBanner) {
        this.showMaintenanceExpiryBanner = showMaintenanceExpiryBanner;
    }

    public String getRenewUrl() {
        return this.renewUrl;
    }

    public void setRenewUrl(String renewUrl) {
        this.renewUrl = renewUrl;
    }

    public String getSalesUrl() {
        return this.salesUrl;
    }

    public void setSalesUrl(String salesUrl) {
        this.salesUrl = salesUrl;
    }
}

