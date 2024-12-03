/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.audit.file;

import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.sal.api.ApplicationProperties;

public class AuditRetentionFileConfigLicenseChecker {
    private final ProductLicenseChecker productLicenseChecker;
    private final ApplicationProperties applicationProperties;

    public AuditRetentionFileConfigLicenseChecker(ProductLicenseChecker productLicenseChecker, ApplicationProperties applicationProperties) {
        this.productLicenseChecker = productLicenseChecker;
        this.applicationProperties = applicationProperties;
    }

    public boolean allowUpdate() {
        return this.productLicenseChecker.isDcLicenseOrExempt() || "bitbucket".equals(this.applicationProperties.getPlatformId());
    }
}

