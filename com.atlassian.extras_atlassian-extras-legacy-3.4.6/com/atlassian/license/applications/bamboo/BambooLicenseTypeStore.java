/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.bamboo;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class BambooLicenseTypeStore
extends LicenseTypeStore {
    public static LicenseType BAMBOO_BASIC_EVALUATION = new DefaultLicenseType(441, "Bamboo Basic: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.BASIC);
    public static LicenseType BAMBOO_BASIC_ACADEMIC = new DefaultLicenseType(443, "Bamboo Basic: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.BASIC);
    public static LicenseType BAMBOO_BASIC_DEMONSTRATION = new DefaultLicenseType(445, "Bamboo Basic: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name(), LicenseEdition.BASIC);
    public static LicenseType BAMBOO_BASIC_DEVELOPER = new DefaultLicenseType(447, "Bamboo Basic: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name(), LicenseEdition.BASIC);
    public static LicenseType BAMBOO_BASIC_COMMUNITY = new DefaultLicenseType(449, "Bamboo Basic: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name(), LicenseEdition.BASIC);
    public static LicenseType BAMBOO_BASIC_OPEN_SOURCE = new DefaultLicenseType(451, "Bamboo Basic: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.BASIC);
    public static LicenseType BAMBOO_BASIC_COMMERCIAL_SERVER = new DefaultLicenseType(453, "Bamboo Basic: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.BASIC);
    public static LicenseType BAMBOO_EVALUATION = new DefaultLicenseType(401, "Bamboo Standard: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.STANDARD);
    public static LicenseType BAMBOO_ACADEMIC = new DefaultLicenseType(409, "Bamboo Standard: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.STANDARD);
    public static LicenseType BAMBOO_DEMONSTRATION = new DefaultLicenseType(427, "Bamboo Standard: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name(), LicenseEdition.STANDARD);
    public static LicenseType BAMBOO_DEVELOPER = new DefaultLicenseType(431, "Bamboo Standard: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name(), LicenseEdition.STANDARD);
    public static LicenseType BAMBOO_COMMUNITY = new DefaultLicenseType(435, "Bamboo Standard: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name(), LicenseEdition.STANDARD);
    public static LicenseType BAMBOO_OPEN_SOURCE = new DefaultLicenseType(419, "Bamboo Standard: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.STANDARD);
    public static LicenseType BAMBOO_COMMERCIAL_SERVER = new DefaultLicenseType(421, "Bamboo Standard: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.STANDARD);
    public static LicenseType BAMBOO_PROFESSIONAL_EVALUATION = new DefaultLicenseType(461, "Bamboo Professional: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType BAMBOO_PROFESSIONAL_ACADEMIC = new DefaultLicenseType(463, "Bamboo Professional: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType BAMBOO_PROFESSIONAL_DEMONSTRATION = new DefaultLicenseType(465, "Bamboo Professional: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType BAMBOO_PROFESSIONAL_DEVELOPER = new DefaultLicenseType(467, "Bamboo Professional: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType BAMBOO_PROFESSIONAL_COMMUNITY = new DefaultLicenseType(469, "Bamboo Professional: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType BAMBOO_PROFESSIONAL_OPEN_SOURCE = new DefaultLicenseType(471, "Bamboo Professional: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType BAMBOO_PROFESSIONAL_COMMERCIAL_SERVER = new DefaultLicenseType(473, "Bamboo Professional: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType BAMBOO_ENTERPRISE_EVALUATION = new DefaultLicenseType(481, "Bamboo Enterprise: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType BAMBOO_ENTERPRISE_ACADEMIC = new DefaultLicenseType(483, "Bamboo Enterprise: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType BAMBOO_ENTERPRISE_DEMONSTRATION = new DefaultLicenseType(485, "Bamboo Enterprise: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType BAMBOO_ENTERPRISE_DEVELOPER = new DefaultLicenseType(487, "Bamboo Enterprise: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType BAMBOO_ENTERPRISE_COMMUNITY = new DefaultLicenseType(489, "Bamboo Enterprise: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType BAMBOO_ENTERPRISE_OPEN_SOURCE = new DefaultLicenseType(491, "Bamboo Enterprise: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType BAMBOO_ENTERPRISE_COMMERCIAL_SERVER = new DefaultLicenseType(493, "Bamboo Enterprise: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType BAMBOO_TEMP_2_0_BETA = new DefaultLicenseType(432, "Bamboo: 2.0 beta", true, false, com.atlassian.extras.api.LicenseType.TESTING.name(), LicenseEdition.ENTERPRISE);
    public static String publicKeyFileName = "com/atlassian/bamboo/leaf.key";
    public static String privateKeyFileName = "bamboo/bamboo.byte";

    public BambooLicenseTypeStore() {
        this.applicationLicenseTypes.add(BAMBOO_ACADEMIC);
        this.applicationLicenseTypes.add(BAMBOO_EVALUATION);
        this.applicationLicenseTypes.add(BAMBOO_DEMONSTRATION);
        this.applicationLicenseTypes.add(BAMBOO_DEVELOPER);
        this.applicationLicenseTypes.add(BAMBOO_COMMUNITY);
        this.applicationLicenseTypes.add(BAMBOO_OPEN_SOURCE);
        this.applicationLicenseTypes.add(BAMBOO_COMMERCIAL_SERVER);
        this.applicationLicenseTypes.add(BAMBOO_BASIC_ACADEMIC);
        this.applicationLicenseTypes.add(BAMBOO_BASIC_EVALUATION);
        this.applicationLicenseTypes.add(BAMBOO_BASIC_DEMONSTRATION);
        this.applicationLicenseTypes.add(BAMBOO_BASIC_DEVELOPER);
        this.applicationLicenseTypes.add(BAMBOO_BASIC_COMMUNITY);
        this.applicationLicenseTypes.add(BAMBOO_BASIC_OPEN_SOURCE);
        this.applicationLicenseTypes.add(BAMBOO_BASIC_COMMERCIAL_SERVER);
        this.applicationLicenseTypes.add(BAMBOO_PROFESSIONAL_ACADEMIC);
        this.applicationLicenseTypes.add(BAMBOO_PROFESSIONAL_EVALUATION);
        this.applicationLicenseTypes.add(BAMBOO_PROFESSIONAL_DEMONSTRATION);
        this.applicationLicenseTypes.add(BAMBOO_PROFESSIONAL_DEVELOPER);
        this.applicationLicenseTypes.add(BAMBOO_PROFESSIONAL_COMMUNITY);
        this.applicationLicenseTypes.add(BAMBOO_PROFESSIONAL_OPEN_SOURCE);
        this.applicationLicenseTypes.add(BAMBOO_PROFESSIONAL_COMMERCIAL_SERVER);
        this.applicationLicenseTypes.add(BAMBOO_ENTERPRISE_ACADEMIC);
        this.applicationLicenseTypes.add(BAMBOO_ENTERPRISE_EVALUATION);
        this.applicationLicenseTypes.add(BAMBOO_ENTERPRISE_DEMONSTRATION);
        this.applicationLicenseTypes.add(BAMBOO_ENTERPRISE_DEVELOPER);
        this.applicationLicenseTypes.add(BAMBOO_ENTERPRISE_COMMUNITY);
        this.applicationLicenseTypes.add(BAMBOO_ENTERPRISE_OPEN_SOURCE);
        this.applicationLicenseTypes.add(BAMBOO_ENTERPRISE_COMMERCIAL_SERVER);
        this.applicationLicenseTypes.add(BAMBOO_TEMP_2_0_BETA);
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

