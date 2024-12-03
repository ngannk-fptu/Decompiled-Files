/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.licensebanner.provider;

import com.atlassian.confluence.plugins.licensebanner.support.LicenseDetails;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="licenseDetails")
public class LicenseDetailsModel {
    @XmlElement(name="daysBeforeLicenseExpiry", nillable=false)
    private int daysBeforeLicenseExpiry;
    @XmlElement(name="daysBeforeMaintenanceExpiry", nillable=false)
    private int daysBeforeMaintenanceExpiry;
    @XmlElement(name="showLicenseExpiryBanner", nillable=false)
    private boolean showLicenseExpiryBanner;
    @XmlElement(name="showMaintenanceExpiryBanner", nillable=false)
    private boolean showMaintenanceExpiryBanner;
    @XmlElement(name="renewUrl", nillable=false)
    private String renewUrl;
    @XmlElement(name="salesUrl", nillable=false)
    private String salesUrl;

    public LicenseDetailsModel() {
    }

    public LicenseDetailsModel(LicenseDetails from) {
        this.daysBeforeLicenseExpiry = from.getDaysBeforeLicenseExpiry();
        this.daysBeforeMaintenanceExpiry = from.getDaysBeforeMaintenanceExpiry();
        this.showLicenseExpiryBanner = from.isShowLicenseExpiryBanner();
        this.showMaintenanceExpiryBanner = from.isShowMaintenanceExpiryBanner();
        this.renewUrl = from.getRenewUrl();
        this.salesUrl = from.getSalesUrl();
    }

    public int getDaysBeforeLicenseExpiry() {
        return this.daysBeforeLicenseExpiry;
    }

    public void setDaysBeforeLicenseExpiry(int daysBeforeLicenseExpiry) {
        this.daysBeforeLicenseExpiry = daysBeforeLicenseExpiry;
    }

    public int getDaysBeforeMaintenanceExpiry() {
        return this.daysBeforeMaintenanceExpiry;
    }

    public void setDaysBeforeMaintenanceExpiry(int daysBeforeMaintenanceExpiry) {
        this.daysBeforeMaintenanceExpiry = daysBeforeMaintenanceExpiry;
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

