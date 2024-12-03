/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.perforceplugin;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import com.atlassian.license.applications.jira.JiraLicenseTypeStore;
import java.util.Collection;

@Deprecated
public class PerforcePluginLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType PERFORCE_ACADEMIC = new DefaultLicenseType(307, "Perforce: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType PERFORCE_EVALUATION = new DefaultLicenseType(311, "Perforce: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType PERFORCE_DEMONSTRATION = new DefaultLicenseType(312, "Perforce: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static LicenseType PERFORCE_NON_PROFIT = new DefaultLicenseType(313, "Perforce: Non-Profit / Open Source", false, false, com.atlassian.extras.api.LicenseType.NON_PROFIT.name());
    public static LicenseType PERFORCE_COMMUNITY = new DefaultLicenseType(314, "Perforce: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType PERFORCE_DEVELOPER = new DefaultLicenseType(315, "Perforce: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType PERFORCE_OPEN_SOURCE = new DefaultLicenseType(316, "Perforce: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static LicenseType PERFORCE_FULL_LICENSE = new DefaultLicenseType(317, "Perforce: Commercial", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static String publicKeyFileName = JiraLicenseTypeStore.publicKeyFileName;
    public static String privateKeyFileName = JiraLicenseTypeStore.privateKeyFileName;

    public PerforcePluginLicenseTypeStore() {
        this.applicationLicenseTypes.add(PERFORCE_ACADEMIC);
        this.applicationLicenseTypes.add(PERFORCE_EVALUATION);
        this.applicationLicenseTypes.add(PERFORCE_NON_PROFIT);
        this.applicationLicenseTypes.add(PERFORCE_FULL_LICENSE);
        this.applicationLicenseTypes.add(PERFORCE_COMMUNITY);
        this.applicationLicenseTypes.add(PERFORCE_DEMONSTRATION);
        this.applicationLicenseTypes.add(PERFORCE_DEVELOPER);
        this.applicationLicenseTypes.add(PERFORCE_OPEN_SOURCE);
    }

    @Override
    public Collection getAllLicenses() {
        return this.applicationLicenseTypes;
    }

    @Override
    public String getPublicKeyFileName() {
        return publicKeyFileName;
    }

    @Override
    public String getPrivateKeyFileName() {
        return privateKeyFileName;
    }
}

