/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.PluginLicenseImpl;

public interface PluginLicensesInternal {
    public static boolean isRoleBasedLicense(PluginLicense license) {
        return LicenseEditionType.ROLE_COUNT.equals((Object)license.getEditionType());
    }

    public static Option<Integer> getCurrentRoleCount(PluginLicense license) {
        if (license instanceof PluginLicenseImpl) {
            return ((PluginLicenseImpl)license).getCurrentRoleCount();
        }
        return Option.none();
    }
}

