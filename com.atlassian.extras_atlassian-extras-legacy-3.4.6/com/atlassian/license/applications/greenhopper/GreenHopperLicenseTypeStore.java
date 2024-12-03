/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.greenhopper;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;

@Deprecated
public class GreenHopperLicenseTypeStore
extends LicenseTypeStore {
    public static final String NAME = "GreenHopper";
    public static final LicenseType GREENHOPPER_STANDARD_FULL_LICENSE = new DefaultLicenseType(1400, "GreenHopper Standard: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.STANDARD);
    public static final LicenseType GREENHOPPER_PROFESSIONAL_FULL_LICENSE = new DefaultLicenseType(1401, "GreenHopper Professional: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.PROFESSIONAL);
    public static final LicenseType GREENHOPPER_ENTERPRISE_FULL_LICENSE = new DefaultLicenseType(1402, "GreenHopper Enterprise: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.ENTERPRISE);
    public static final LicenseType GREENHOPPER_ENTERPRISE_EVALUATION = new DefaultLicenseType(1403, "GreenHopper Enterprise: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.ENTERPRISE);
    public static final LicenseType GREENHOPPER_ENTERPRISE_ACADEMIC = new DefaultLicenseType(1404, "GreenHopper Enterprise: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.ENTERPRISE);
    public static final LicenseType GREENHOPPER_ENTERPRISE_OPEN_SOURCE = new DefaultLicenseType(1405, "GreenHopper Enterprise: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.ENTERPRISE);
    public static final LicenseType GREENHOPPER_ENTERPRISE_PERSONAL = new DefaultLicenseType(1406, "GreenHopper Enterprise: Personal", false, true, com.atlassian.extras.api.LicenseType.PERSONAL.name(), LicenseEdition.ENTERPRISE);

    public GreenHopperLicenseTypeStore() {
        this.applicationLicenseTypes.add(GREENHOPPER_STANDARD_FULL_LICENSE);
        this.applicationLicenseTypes.add(GREENHOPPER_PROFESSIONAL_FULL_LICENSE);
        this.applicationLicenseTypes.add(GREENHOPPER_ENTERPRISE_FULL_LICENSE);
        this.applicationLicenseTypes.add(GREENHOPPER_ENTERPRISE_EVALUATION);
        this.applicationLicenseTypes.add(GREENHOPPER_ENTERPRISE_ACADEMIC);
        this.applicationLicenseTypes.add(GREENHOPPER_ENTERPRISE_OPEN_SOURCE);
        this.applicationLicenseTypes.add(GREENHOPPER_ENTERPRISE_PERSONAL);
    }

    @Override
    public String getPublicKeyFileName() {
        return "com/atlassian/greenhopper/leaf.key";
    }

    @Override
    public String getPrivateKeyFileName() {
        return "greenhopper/greenhopper.byte";
    }
}

