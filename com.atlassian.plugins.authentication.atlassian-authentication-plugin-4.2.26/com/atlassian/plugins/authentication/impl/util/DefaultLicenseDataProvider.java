/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.StashComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.license.MultiProductLicenseDetails
 *  javax.inject.Inject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.StashComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.util.ProductLicenseDataProvider;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JiraComponent
@BitbucketComponent
@StashComponent
@ConfluenceComponent
public class DefaultLicenseDataProvider
implements ProductLicenseDataProvider {
    public static final String JSM_LEGACY_ACTIVE_PROPERTY_KEY = "com.atlassian.servicedesk.active";
    public static final String JSM_RBP_ACTIVE_PROPERTY_KEY = "jira.product.jira-servicedesk.active";
    private static final Logger log = LoggerFactory.getLogger(DefaultLicenseDataProvider.class);
    private final Predicate<MultiProductLicenseDetails> dataCenterPredicate = BaseLicenseDetails::isDataCenter;
    private final Predicate<MultiProductLicenseDetails> serviceManagementPredicate = license -> Boolean.parseBoolean(license.getProperty(JSM_LEGACY_ACTIVE_PROPERTY_KEY)) || Boolean.parseBoolean(license.getProperty(JSM_RBP_ACTIVE_PROPERTY_KEY));
    private LicenseHandler licenseHandler;

    @Inject
    public DefaultLicenseDataProvider(@ComponentImport LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    @Override
    public boolean isDataCenterProduct() {
        return this.licenseHandler.getAllProductLicenses().stream().allMatch(this.dataCenterPredicate);
    }

    @Override
    public boolean isServiceManagementProduct() {
        try {
            return this.licenseHandler.getAllProductLicenses().stream().anyMatch(this.serviceManagementPredicate);
        }
        catch (Exception e) {
            log.debug("Exception caught when checking if JSM license exists", (Throwable)e);
            return false;
        }
    }
}

