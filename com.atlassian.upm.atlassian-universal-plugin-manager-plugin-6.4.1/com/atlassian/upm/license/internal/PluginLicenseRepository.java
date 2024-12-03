/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseError;
import javax.annotation.Nonnull;

public interface PluginLicenseRepository {
    @Nonnull
    public Option<PluginLicense> getPluginLicense(String var1);

    public Iterable<PluginLicense> getPluginLicenses();

    public Either<PluginLicenseError, Option<String>> setPluginLicense(String var1, String var2);

    public Option<String> removePluginLicense(String var1);

    public void invalidateCache();

    public void invalidateCacheForPlugin(String var1);
}

