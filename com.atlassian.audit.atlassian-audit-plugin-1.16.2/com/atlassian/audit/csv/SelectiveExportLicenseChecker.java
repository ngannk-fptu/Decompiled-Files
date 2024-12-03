/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.audit.csv;

import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.sal.api.ApplicationProperties;

public class SelectiveExportLicenseChecker {
    private final ProductLicenseChecker productLicenseChecker;
    private final ApplicationProperties applicationProperties;

    public SelectiveExportLicenseChecker(ProductLicenseChecker productLicenseChecker, ApplicationProperties applicationProperties) {
        this.productLicenseChecker = productLicenseChecker;
        this.applicationProperties = applicationProperties;
    }

    public boolean allowSelectiveExport() {
        return this.productLicenseChecker.isDcLicenseOrExempt() || "conf".equals(this.applicationProperties.getPlatformId());
    }
}

