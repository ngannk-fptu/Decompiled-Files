/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.vssplugin;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import com.atlassian.license.applications.jira.JiraLicenseTypeStore;
import java.util.Collection;

@Deprecated
public class VSSPluginLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType VSS_ACADEMIC = new DefaultLicenseType(320, "VSS: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType VSS_EVALUATION = new DefaultLicenseType(321, "VSS: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType VSS_NON_PROFIT = new DefaultLicenseType(322, "VSS: Non-Profit / Open Source", false, false, com.atlassian.extras.api.LicenseType.NON_PROFIT.name());
    public static LicenseType VSS_FULL_LICENSE = new DefaultLicenseType(323, "VSS: Commercial", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType VSS_COMMUNITY = new DefaultLicenseType(324, "VSS: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType VSS_DEVELOPER = new DefaultLicenseType(325, "VSS: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType VSS_DEMONSTRATION = new DefaultLicenseType(326, "VSS: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static LicenseType VSS_OPEN_SOURCE = new DefaultLicenseType(327, "VSS: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static String publicKeyFileName = JiraLicenseTypeStore.publicKeyFileName;
    public static String privateKeyFileName = JiraLicenseTypeStore.privateKeyFileName;

    public VSSPluginLicenseTypeStore() {
        this.applicationLicenseTypes.add(VSS_ACADEMIC);
        this.applicationLicenseTypes.add(VSS_EVALUATION);
        this.applicationLicenseTypes.add(VSS_NON_PROFIT);
        this.applicationLicenseTypes.add(VSS_FULL_LICENSE);
        this.applicationLicenseTypes.add(VSS_COMMUNITY);
        this.applicationLicenseTypes.add(VSS_DEVELOPER);
        this.applicationLicenseTypes.add(VSS_DEMONSTRATION);
        this.applicationLicenseTypes.add(VSS_OPEN_SOURCE);
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

