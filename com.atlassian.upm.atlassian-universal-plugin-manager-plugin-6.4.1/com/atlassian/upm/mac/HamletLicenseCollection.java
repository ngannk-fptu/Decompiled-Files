/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.mac;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mac.HamletLicenseInfo;
import com.atlassian.upm.mac.LicensesSummary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HamletLicenseCollection {
    private final HamletLicenseInfo productLicense;
    private final List<HamletLicenseInfo> addonLicenses;
    private final Option<LicensesSummary> summary;

    public HamletLicenseCollection(HamletLicenseInfo productLicense, List<HamletLicenseInfo> addonLicenses, Option<LicensesSummary> summary) {
        this.productLicense = Objects.requireNonNull(productLicense, "productLicense");
        this.addonLicenses = Collections.unmodifiableList(new ArrayList(Objects.requireNonNull(addonLicenses, "addonLicenses")));
        this.summary = Objects.requireNonNull(summary, "summary");
    }

    public HamletLicenseInfo getProductLicense() {
        return this.productLicense;
    }

    public List<HamletLicenseInfo> getAddonLicenses() {
        return this.addonLicenses;
    }

    public Option<LicensesSummary> getSummary() {
        return this.summary;
    }
}

