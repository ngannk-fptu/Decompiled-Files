/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.clover;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class CloverLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType CLOVER_ACADEMIC = new DefaultLicenseType(1000, "Clover: Academic", false, true, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType CLOVER_COMMERCIAL = new DefaultLicenseType(1010, "Clover: Commercial", false, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType CLOVER_COMMUNITY = new DefaultLicenseType(1020, "Clover: Community", false, true, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType CLOVER_EVALUATION = new DefaultLicenseType(1030, "Clover: Evaluation", true, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType CLOVER_OPEN_SOURCE = new DefaultLicenseType(1040, "Clover: Open Source", false, true, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static LicenseType CLOVER_DEVELOPER = new DefaultLicenseType(1050, "Clover: Developer", false, true, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType CLOVER_DEMONSTRATION = new DefaultLicenseType(1060, "Clover: Demonstration", false, true, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static String publicKeyFileName = "com/atlassian/clover/leaf.key";
    public static String privateKeyFileName = "clover/clover.byte";

    public CloverLicenseTypeStore() {
        this.applicationLicenseTypes.add(CLOVER_ACADEMIC);
        this.applicationLicenseTypes.add(CLOVER_COMMERCIAL);
        this.applicationLicenseTypes.add(CLOVER_COMMUNITY);
        this.applicationLicenseTypes.add(CLOVER_EVALUATION);
        this.applicationLicenseTypes.add(CLOVER_OPEN_SOURCE);
        this.applicationLicenseTypes.add(CLOVER_DEVELOPER);
        this.applicationLicenseTypes.add(CLOVER_DEMONSTRATION);
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

