/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.impl;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.VersionAwareHostApplicationInformation;
import com.atlassian.upm.core.impl.DefaultHostApplicationInformationImpl;
import com.atlassian.upm.core.test.rest.resources.BuildNumberResource;
import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.PackageAccessor;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionAwareHostApplicationInformationImpl
extends DefaultHostApplicationInformationImpl
implements VersionAwareHostApplicationInformation {
    private static final Logger logger = LoggerFactory.getLogger(VersionAwareHostApplicationInformationImpl.class);
    private static final String FISHEYE_BN_PREFIX = "dev-";
    private static final int JIRA_CAREBEAR_VERSION = 815000;
    private final Option<String> applicationVersionQualifier;

    public VersionAwareHostApplicationInformationImpl(ApplicationProperties applicationProperties, LicenseHandler licenseHandler, PackageAccessor packageAccessor) {
        super(applicationProperties, licenseHandler);
        String productPackage = "com.atlassian." + applicationProperties.getDisplayName().toLowerCase();
        Iterable<Package> packages = packageAccessor.getExportedPackages(0L, productPackage);
        if (Iterables.toList(packages).isEmpty()) {
            logger.warn("Could not find product package in the system bundle: " + productPackage);
            this.applicationVersionQualifier = Option.none(String.class);
        } else {
            this.applicationVersionQualifier = Option.option(((Package)com.google.common.collect.Iterables.getLast(packages)).getVersion().getQualifier());
        }
    }

    @Override
    public int getBuildNumber() {
        String buildNumber = BuildNumberResource.getBuildNumber().getOrElse(this.getApplicationProperties().getBuildNumber());
        if (buildNumber.startsWith(FISHEYE_BN_PREFIX)) {
            buildNumber = buildNumber.substring(FISHEYE_BN_PREFIX.length());
        }
        try {
            return Integer.valueOf(buildNumber);
        }
        catch (NumberFormatException e) {
            logger.error("Could not parse application build number", (Throwable)e);
            return -1;
        }
    }

    @Override
    public boolean isDevelopmentProductVersion() {
        Iterator<Boolean> iterator = BuildNumberResource.isDevelopment().iterator();
        if (iterator.hasNext()) {
            boolean development = iterator.next();
            return development;
        }
        return StringUtils.isNotBlank((CharSequence)this.applicationVersionQualifier.getOrElse(""));
    }

    @Override
    public boolean isJiraPostCarebear() {
        return "jira".equals(this.getApplicationProperties().getDisplayName().toLowerCase()) && this.getBuildNumber() >= 815000;
    }
}

