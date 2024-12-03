/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.upm.core.impl;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.HostingType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class DefaultHostApplicationInformationImpl
implements DefaultHostApplicationInformation {
    private final ApplicationProperties applicationProperties;
    private final LicenseHandler licenseHandler;

    public DefaultHostApplicationInformationImpl(ApplicationProperties applicationProperties, LicenseHandler licenseHandler) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.licenseHandler = Objects.requireNonNull(licenseHandler, "licenseHandler");
    }

    @Override
    public boolean canInstallLegacyPlugins() {
        return this.applicationProperties.getDisplayName().equalsIgnoreCase("confluence");
    }

    @Override
    public boolean canInstallXmlPlugins() {
        return Arrays.asList("jira", "confluence", "bamboo").contains(this.applicationProperties.getDisplayName().toLowerCase());
    }

    @Override
    public String getServerId() {
        return this.licenseHandler.getServerId();
    }

    @Override
    public HostingType getHostingType() {
        try {
            if (this.licenseHandler.hostAllowsMultipleLicenses()) {
                return Optional.ofNullable(this.licenseHandler.getAllProductLicenses()).filter(apl -> apl.stream().anyMatch(BaseLicenseDetails::isDataCenter)).map(pl -> HostingType.DATA_CENTER).orElse(HostingType.SERVER);
            }
            return Optional.ofNullable(this.licenseHandler.getProductLicenseDetails(this.applicationProperties.getPlatformId())).filter(BaseLicenseDetails::isDataCenter).map(pl -> HostingType.DATA_CENTER).orElse(HostingType.SERVER);
        }
        catch (UnsupportedOperationException e) {
            return HostingType.SERVER;
        }
    }

    protected ApplicationProperties getApplicationProperties() {
        return this.applicationProperties;
    }
}

