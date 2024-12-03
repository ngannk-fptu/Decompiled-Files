/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.LicenseType
 */
package com.atlassian.license.applications.jira;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.license.DefaultLicenseType;
import com.atlassian.license.LicenseType;
import com.atlassian.license.LicenseTypeStore;
import java.util.Collection;

@Deprecated
public class JiraLicenseTypeStore
extends LicenseTypeStore {
    public static final String APPLICATION_NAME = "JIRA";
    public static LicenseType JIRA_STANDARD_ACADEMIC = new DefaultLicenseType(197, "JIRA Standard: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_EVALUATION = new DefaultLicenseType(109, "JIRA Standard: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_NON_PROFIT = new DefaultLicenseType(157, "JIRA Standard: Non-Profit / Open Source", false, false, com.atlassian.extras.api.LicenseType.NON_PROFIT.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_FULL_LICENSE = new DefaultLicenseType(179, "JIRA Standard: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_COMMUNITY = new DefaultLicenseType(107, "JIRA Standard: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_OPEN_SOURCE = new DefaultLicenseType(122, "JIRA Standard: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_DEVELOPER = new DefaultLicenseType(139, "JIRA Standard: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_DEMONSTRATION = new DefaultLicenseType(155, "JIRA Standard: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_PERSONAL = new DefaultLicenseType(189, "JIRA Standard: Personal", false, true, com.atlassian.extras.api.LicenseType.PERSONAL.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_STANDARD_STARTER = new DefaultLicenseType(190, "JIRA Standard: Starter", false, true, com.atlassian.extras.api.LicenseType.STARTER.name(), LicenseEdition.STANDARD);
    public static LicenseType JIRA_PROFESSIONAL_ACADEMIC = new DefaultLicenseType(91, "JIRA Professional: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_EVALUATION = new DefaultLicenseType(47, "JIRA Professional: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_NON_PROFIT = new DefaultLicenseType(76, "JIRA Professional: Non-Profit / Open Source", false, false, com.atlassian.extras.api.LicenseType.NON_PROFIT.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_FULL_LICENSE = new DefaultLicenseType(87, "JIRA Professional: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_COMMUNITY = new DefaultLicenseType(42, "JIRA Professional: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_OPEN_SOURCE = new DefaultLicenseType(39, "JIRA Professional: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_DEVELOPER = new DefaultLicenseType(82, "JIRA Professional: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_DEMONSTRATION = new DefaultLicenseType(99, "JIRA Professional: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_PERSONAL = new DefaultLicenseType(89, "JIRA Professional: Personal", false, true, com.atlassian.extras.api.LicenseType.PERSONAL.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_PROFESSIONAL_STARTER = new DefaultLicenseType(90, "JIRA Professional: Starter", false, true, com.atlassian.extras.api.LicenseType.STARTER.name(), LicenseEdition.PROFESSIONAL);
    public static LicenseType JIRA_ENTERPRISE_ACADEMIC = new DefaultLicenseType(269, "JIRA Enterprise: Academic", false, false, com.atlassian.extras.api.LicenseType.ACADEMIC.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_EVALUATION = new DefaultLicenseType(201, "JIRA Enterprise: Evaluation", true, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_NON_PROFIT = new DefaultLicenseType(213, "JIRA Enterprise: Non-Profit / Open Source", false, false, com.atlassian.extras.api.LicenseType.NON_PROFIT.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_FULL_LICENSE = new DefaultLicenseType(267, "JIRA Enterprise: Commercial Server", false, false, com.atlassian.extras.api.LicenseType.COMMERCIAL.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_HOSTED = new DefaultLicenseType(261, "JIRA Enterprise: Hosted", false, true, com.atlassian.extras.api.LicenseType.HOSTED.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_COMMUNITY = new DefaultLicenseType(207, "JIRA Enterprise: Community", false, false, com.atlassian.extras.api.LicenseType.COMMUNITY.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_OPEN_SOURCE = new DefaultLicenseType(222, "JIRA Enterprise: Open Source", false, false, com.atlassian.extras.api.LicenseType.OPEN_SOURCE.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_DEVELOPER = new DefaultLicenseType(239, "JIRA Enterprise: Developer", false, false, com.atlassian.extras.api.LicenseType.DEVELOPER.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_DEMONSTRATION = new DefaultLicenseType(255, "JIRA Enterprise: Demonstration", false, false, com.atlassian.extras.api.LicenseType.DEMONSTRATION.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_TESTING = new DefaultLicenseType(240, "JIRA Enterprise: Testing", false, false, true, com.atlassian.extras.api.LicenseType.TESTING.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_PERSONAL = new DefaultLicenseType(289, "JIRA Enterprise: Personal", false, true, com.atlassian.extras.api.LicenseType.PERSONAL.name(), LicenseEdition.ENTERPRISE);
    public static LicenseType JIRA_ENTERPRISE_STARTER = new DefaultLicenseType(290, "JIRA Enterprise: Starter", false, true, com.atlassian.extras.api.LicenseType.STARTER.name(), LicenseEdition.ENTERPRISE);
    public static String publicKeyFileName = "com/atlassian/jira/leaf.key";
    public static String privateKeyFileName = "jira/jira.byte";

    public JiraLicenseTypeStore() {
        this.applicationLicenseTypes.add(JIRA_STANDARD_ACADEMIC);
        this.applicationLicenseTypes.add(JIRA_STANDARD_EVALUATION);
        this.applicationLicenseTypes.add(JIRA_STANDARD_NON_PROFIT);
        this.applicationLicenseTypes.add(JIRA_STANDARD_FULL_LICENSE);
        this.applicationLicenseTypes.add(JIRA_STANDARD_COMMUNITY);
        this.applicationLicenseTypes.add(JIRA_STANDARD_OPEN_SOURCE);
        this.applicationLicenseTypes.add(JIRA_STANDARD_DEVELOPER);
        this.applicationLicenseTypes.add(JIRA_STANDARD_DEMONSTRATION);
        this.applicationLicenseTypes.add(JIRA_STANDARD_PERSONAL);
        this.applicationLicenseTypes.add(JIRA_STANDARD_STARTER);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_ACADEMIC);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_EVALUATION);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_NON_PROFIT);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_FULL_LICENSE);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_COMMUNITY);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_OPEN_SOURCE);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_DEVELOPER);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_DEMONSTRATION);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_PERSONAL);
        this.applicationLicenseTypes.add(JIRA_PROFESSIONAL_STARTER);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_ACADEMIC);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_EVALUATION);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_NON_PROFIT);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_FULL_LICENSE);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_HOSTED);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_COMMUNITY);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_OPEN_SOURCE);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_DEVELOPER);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_DEMONSTRATION);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_TESTING);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_PERSONAL);
        this.applicationLicenseTypes.add(JIRA_ENTERPRISE_STARTER);
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

