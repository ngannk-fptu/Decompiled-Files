/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.crowd;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class CrowdLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType CROWD_ACADEMIC = new DefaultLicenseType(601, "Crowd: Academic", false, true, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType CROWD_COMMERCIAL = new DefaultLicenseType(609, "Crowd: Commercial", false, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType CROWD_COMMUNITY = new DefaultLicenseType(617, "Crowd: Community", false, true, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType CROWD_EVALUATION = new DefaultLicenseType(625, "Crowd: Evaluation", true, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType CROWD_OPEN_SOURCE = new DefaultLicenseType(633, "Crowd: Open Source", false, true, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static LicenseType CROWD_DEVELOPER = new DefaultLicenseType(641, "Crowd: Developer", false, true, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType CROWD_DEMONSTRATION = new DefaultLicenseType(649, "Crowd: Demonstration", false, true, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static String publicKeyFileName = "com/atlassian/crowd/leaf.key";
    public static String privateKeyFileName = "crowd/crowd.byte";

    public CrowdLicenseTypeStore() {
        this.applicationLicenseTypes.add(CROWD_ACADEMIC);
        this.applicationLicenseTypes.add(CROWD_COMMERCIAL);
        this.applicationLicenseTypes.add(CROWD_COMMUNITY);
        this.applicationLicenseTypes.add(CROWD_EVALUATION);
        this.applicationLicenseTypes.add(CROWD_OPEN_SOURCE);
        this.applicationLicenseTypes.add(CROWD_DEVELOPER);
        this.applicationLicenseTypes.add(CROWD_DEMONSTRATION);
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

