/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.crucible;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class CrucibleLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType CRUCIBLE_ACADEMIC = new DefaultLicenseType(1100, "Crucible: Academic", false, true, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType CRUCIBLE_COMMERCIAL = new DefaultLicenseType(1110, "Crucible: Commercial", false, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType CRUCIBLE_COMMUNITY = new DefaultLicenseType(1120, "Crucible: Community", false, true, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType CRUCIBLE_EVALUATION = new DefaultLicenseType(1130, "Crucible: Evaluation", true, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType CRUCIBLE_OPEN_SOURCE = new DefaultLicenseType(1140, "Crucible: Open Source", false, true, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static LicenseType CRUCIBLE_DEVELOPER = new DefaultLicenseType(1150, "Crucible: Developer", false, true, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType CRUCIBLE_STARTER = new DefaultLicenseType(1170, "Crucible: Starter", false, true, com.atlassian.extras.api.LicenseType.STARTER.name());
    public static LicenseType CRUCIBLE_DEMONSTRATION = new DefaultLicenseType(1160, "Crucible: Demonstration", false, true, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static String publicKeyFileName = "com/atlassian/crucible/leaf.key";
    public static String privateKeyFileName = "crucible/crucible.byte";

    public CrucibleLicenseTypeStore() {
        this.applicationLicenseTypes.add(CRUCIBLE_ACADEMIC);
        this.applicationLicenseTypes.add(CRUCIBLE_COMMERCIAL);
        this.applicationLicenseTypes.add(CRUCIBLE_COMMUNITY);
        this.applicationLicenseTypes.add(CRUCIBLE_EVALUATION);
        this.applicationLicenseTypes.add(CRUCIBLE_OPEN_SOURCE);
        this.applicationLicenseTypes.add(CRUCIBLE_DEVELOPER);
        this.applicationLicenseTypes.add(CRUCIBLE_STARTER);
        this.applicationLicenseTypes.add(CRUCIBLE_DEMONSTRATION);
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

