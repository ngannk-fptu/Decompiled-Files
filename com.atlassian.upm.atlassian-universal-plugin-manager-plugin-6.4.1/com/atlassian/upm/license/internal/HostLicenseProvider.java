/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;

public interface HostLicenseProvider {
    public Iterable<HostApplicationLicense> getHostApplicationLicenses();

    public HostApplicationLicenseAttributes getHostApplicationLicenseAttributes();

    public Option<HostApplicationEmbeddedAddonLicense> getPluginLicenseDetails(String var1);

    public void invalidateCache();
}

