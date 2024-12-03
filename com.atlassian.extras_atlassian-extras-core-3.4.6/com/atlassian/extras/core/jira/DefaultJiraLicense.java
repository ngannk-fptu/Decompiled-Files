/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.jira.JiraLicense
 *  com.atlassian.extras.common.LicenseTypeAndEditionResolver
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.jira;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.jira.JiraLicense;
import com.atlassian.extras.common.LicenseTypeAndEditionResolver;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultJiraLicense
extends DefaultProductLicense
implements JiraLicense {
    private final LicenseEdition licenseEdition;

    DefaultJiraLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
        this.licenseEdition = LicenseTypeAndEditionResolver.getLicenseEdition((String)licenseProperties.getProperty("LicenseEdition"));
    }

    public LicenseEdition getLicenseEdition() {
        return this.licenseEdition;
    }
}

