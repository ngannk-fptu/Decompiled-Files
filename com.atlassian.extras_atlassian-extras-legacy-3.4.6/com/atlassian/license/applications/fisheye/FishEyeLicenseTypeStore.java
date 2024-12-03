/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.fisheye;

import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class FishEyeLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType FISHEYE_ACADEMIC = new DefaultLicenseType(1200, "FishEye: Academic", false, true, com.atlassian.extras.api.LicenseType.ACADEMIC.name());
    public static LicenseType FISHEYE_COMMERCIAL = new DefaultLicenseType(1210, "FishEye: Commercial", false, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType FISHEYE_COMMUNITY = new DefaultLicenseType(1220, "FishEye: Community", false, true, com.atlassian.extras.api.LicenseType.COMMUNITY.name());
    public static LicenseType FISHEYE_EVALUATION = new DefaultLicenseType(1230, "FishEye: Evaluation", true, true, com.atlassian.extras.api.LicenseType.COMMERCIAL.name());
    public static LicenseType FISHEYE_OPEN_SOURCE = new DefaultLicenseType(1240, "FishEye: Open Source", false, true, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name());
    public static LicenseType FISHEYE_DEVELOPER = new DefaultLicenseType(1250, "FishEye: Developer", false, true, com.atlassian.extras.api.LicenseType.DEVELOPER.name());
    public static LicenseType FISHEYE_STARTER = new DefaultLicenseType(1280, "FishEye: Starter", false, true, com.atlassian.extras.api.LicenseType.STARTER.name());
    public static LicenseType FISHEYE_DEMONSTRATION = new DefaultLicenseType(1260, "FishEye: Demonstration", false, true, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name());
    public static LicenseType FISHEYE_TESTING = new DefaultLicenseType(1270, "FishEye: Testing", false, true, true, com.atlassian.extras.api.LicenseType.TESTING.name());
    public static String publicKeyFileName = "com/atlassian/fisheye/leaf.key";
    public static String privateKeyFileName = "fisheye/fisheye.byte";

    public FishEyeLicenseTypeStore() {
        this.applicationLicenseTypes.add(FISHEYE_ACADEMIC);
        this.applicationLicenseTypes.add(FISHEYE_COMMERCIAL);
        this.applicationLicenseTypes.add(FISHEYE_COMMUNITY);
        this.applicationLicenseTypes.add(FISHEYE_EVALUATION);
        this.applicationLicenseTypes.add(FISHEYE_OPEN_SOURCE);
        this.applicationLicenseTypes.add(FISHEYE_DEVELOPER);
        this.applicationLicenseTypes.add(FISHEYE_STARTER);
        this.applicationLicenseTypes.add(FISHEYE_DEMONSTRATION);
        this.applicationLicenseTypes.add(FISHEYE_TESTING);
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

