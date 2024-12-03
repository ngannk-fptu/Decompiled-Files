/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;

public interface PluginLicenseManager {
    public Option<PluginLicense> getLicense();

    public boolean isUserInLicenseRole(String var1);

    public Option<Integer> getCurrentUserCountInLicenseRole();

    public String getPluginKey();
}

