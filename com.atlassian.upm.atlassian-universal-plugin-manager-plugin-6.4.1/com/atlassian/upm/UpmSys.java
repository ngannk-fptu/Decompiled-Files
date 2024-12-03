/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm;

import com.atlassian.upm.core.Sys;
import com.atlassian.upm.test.rest.resources.UpmSysResource;
import org.apache.commons.lang3.StringUtils;

public abstract class UpmSys
extends Sys {
    public static final String CHECK_LICENSE_FEATURE_ENABLED = "atlassian.upm.check.license.feature";
    public static final String PURCHASED_ADDONS_FEATURE_ENABLED = "atlassian.upm.purchased.addons.feature";

    public static String getMpacWebsiteBaseUrl() {
        String ret = System.getProperty("mpac.website");
        if (StringUtils.isBlank((CharSequence)ret)) {
            ret = UpmSys.getMpacBaseUrl();
        }
        return ret.endsWith("/") ? ret.substring(0, ret.length() - 1) : ret;
    }

    public static boolean isPurchasedAddonsEnabled() {
        return UpmSysResource.isPurchasedAddonsFeatureEnabled().getOrElse(Boolean.getBoolean(PURCHASED_ADDONS_FEATURE_ENABLED));
    }

    public static boolean isCheckLicenseFeatureEnabled() {
        return UpmSysResource.isCheckLicenseFeatureEnabled().getOrElse(Boolean.getBoolean(CHECK_LICENSE_FEATURE_ENABLED));
    }
}

