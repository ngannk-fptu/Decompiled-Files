/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.api.license;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;

public interface RemotePluginLicenseService {
    public Option<PluginLicense> getRemotePluginLicense(String var1);
}

