/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.sharepoint;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class SharePointPluginLicenseTypeStore
extends LicenseTypeStore {
    public static final String SP_PLUGIN_APPNAME = "SharePoint Plugin";
    public static final String APPLICATION_NAME = "SharePoint Plugin";
    public static LicenseType SHAREPOINT_ACADEMIC = new DefaultLicenseType(1300, "SharePoint: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType SHAREPOINT_EVALUATION = new DefaultLicenseType(1310, "SharePoint: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType SHAREPOINT_DEMONSTRATION = new DefaultLicenseType(1320, "SharePoint: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static LicenseType SHAREPOINT_NON_PROFIT = new DefaultLicenseType(1330, "SharePoint: Non-Profit / Open Source", false, false, com.atlassian.extras.api.LicenseType.NON_PROFIT.name());
    public static LicenseType SHAREPOINT_COMMUNITY = new DefaultLicenseType(1340, "SharePoint: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType SHAREPOINT_DEVELOPER = new DefaultLicenseType(1350, "SharePoint: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType SHAREPOINT_OPEN_SOURCE = new DefaultLicenseType(1360, "SharePoint: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static LicenseType SHAREPOINT_FULL_LICENSE = new DefaultLicenseType(1370, "SharePoint: Commercial", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static String publicKeyFileName = "com/atlassian/confluence/page/Page.key";
    public static String privateKeyFileName = "confluence/confluence.byte";

    public SharePointPluginLicenseTypeStore() {
        this.applicationLicenseTypes.add(SHAREPOINT_ACADEMIC);
        this.applicationLicenseTypes.add(SHAREPOINT_EVALUATION);
        this.applicationLicenseTypes.add(SHAREPOINT_NON_PROFIT);
        this.applicationLicenseTypes.add(SHAREPOINT_FULL_LICENSE);
        this.applicationLicenseTypes.add(SHAREPOINT_COMMUNITY);
        this.applicationLicenseTypes.add(SHAREPOINT_DEMONSTRATION);
        this.applicationLicenseTypes.add(SHAREPOINT_DEVELOPER);
        this.applicationLicenseTypes.add(SHAREPOINT_OPEN_SOURCE);
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

