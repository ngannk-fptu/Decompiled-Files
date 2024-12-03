/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.LicenseType
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.bamboo;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.common.util.LicenseProperties;

public abstract class BambooLicenseAttributes {
    public static final int MAX_REMOTE_AGENTS_NONE = 0;
    public static final int MAX_REMOTE_AGENTS_STANDARD = 1;
    public static final int MAX_REMOTE_AGENTS_PROFESSIONAL = 10;
    public static final int MAX_REMOTE_AGENTS_ENTERPRISE = 25;
    public static final int MAX_REMOTE_AGENTS_UNLIMITED = 100;
    public static final int MAX_LOCAL_AGENTS_BASIC = 1;
    public static final int MAX_LOCAL_AGENTS_UNLIMITED = -1;
    public static final int MAX_PLANS_STARTER = 10;
    public static final int MAX_PLANS_UNLIMITED = -1;

    public static int calculateRemoteAgents(LicenseType licenseType, LicenseEdition licenseEdition) {
        if (LicenseType.STARTER.equals((Object)licenseType)) {
            return 0;
        }
        if (LicenseEdition.STANDARD.equals((Object)licenseEdition)) {
            return 1;
        }
        if (LicenseEdition.PROFESSIONAL.equals((Object)licenseEdition)) {
            return 10;
        }
        if (LicenseEdition.ENTERPRISE.equals((Object)licenseEdition)) {
            return 25;
        }
        if (LicenseEdition.UNLIMITED.equals((Object)licenseEdition)) {
            return 100;
        }
        return 0;
    }

    public static int calculateLocalAgents(LicenseType licenseType, LicenseEdition licenseEdition) {
        if (LicenseType.STARTER.equals((Object)licenseType)) {
            return -1;
        }
        if (LicenseEdition.BASIC.equals((Object)licenseEdition)) {
            return 1;
        }
        return -1;
    }

    public static int calculatePlans(LicenseType licenseType) {
        if (LicenseType.STARTER.equals((Object)licenseType)) {
            return 10;
        }
        return -1;
    }

    public static Integer extractValue(LicenseProperties licenseProperties, String param) {
        String value = licenseProperties.getProperty(param);
        if (value != null && value.length() != 0) {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return null;
    }
}

