/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.confluence;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class ConfluenceLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType ACADEMIC = new DefaultLicenseType(16, "Confluence: Academic", false, true, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType EVALUATION = new DefaultLicenseType(32, "Confluence: Evaluation", true, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType TESTING = new DefaultLicenseType(48, "Confluence: Testing", true, true, true, com.atlassian.extras.api.LicenseType.TESTING.name());
    public static LicenseType HOSTED_EVALUATION = new DefaultLicenseType(64, "Confluence: Hosted Evaluation", true, true, com.atlassian.extras.api.LicenseType.HOSTED.name());
    public static LicenseType NON_PROFIT = new DefaultLicenseType(78, "Confluence: Non-Profit / Open Source", false, true, com.atlassian.extras.api.LicenseType.NON_PROFIT.name());
    public static LicenseType FULL_LICENSE = new DefaultLicenseType(85, "Confluence: Commercial Server", false, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType PERSONAL = new DefaultLicenseType(96, "Confluence: Personal Server", false, true, com.atlassian.extras.api.LicenseType.PERSONAL.name());
    public static LicenseType STARTER = new DefaultLicenseType(560, "Confluence: Starter", false, true, com.atlassian.extras.api.LicenseType.STARTER.name());
    public static LicenseType HOSTED = new DefaultLicenseType(128, "Confluence: Commercial Hosted", false, true, true, com.atlassian.extras.api.LicenseType.HOSTED.name());
    public static LicenseType COMMUNITY = new DefaultLicenseType(507, "Confluence: Community", false, true, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType OPEN_SOURCE = new DefaultLicenseType(522, "Confluence: Open Source", false, true, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static LicenseType DEVELOPER = new DefaultLicenseType(539, "Confluence: Developer", false, true, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType DEMONSTRATION = new DefaultLicenseType(555, "Confluence: Demonstration", false, true, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static final String publicKeyFileName = "com/atlassian/confluence/page/Page.key";
    public static final String privateKeyFileName = "confluence/confluence.byte";
    public static final String APPLICATION_NAME = "CONF";

    public ConfluenceLicenseTypeStore() {
        this.applicationLicenseTypes.add(ACADEMIC);
        this.applicationLicenseTypes.add(EVALUATION);
        this.applicationLicenseTypes.add(TESTING);
        this.applicationLicenseTypes.add(HOSTED_EVALUATION);
        this.applicationLicenseTypes.add(NON_PROFIT);
        this.applicationLicenseTypes.add(FULL_LICENSE);
        this.applicationLicenseTypes.add(PERSONAL);
        this.applicationLicenseTypes.add(HOSTED);
        this.applicationLicenseTypes.add(COMMUNITY);
        this.applicationLicenseTypes.add(OPEN_SOURCE);
        this.applicationLicenseTypes.add(DEVELOPER);
        this.applicationLicenseTypes.add(STARTER);
        this.applicationLicenseTypes.add(DEMONSTRATION);
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

