/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.osgi;

import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.VersionRange;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PackageAccessor {
    public Iterable<Package> getPackages();

    @Nullable
    public Package getExportedPackage(long var1, String var3, Version var4);

    @Nonnull
    public Iterable<Package> getExportedPackages(long var1, String var3);

    @Nullable
    public Package getImportedPackage(long var1, String var3, VersionRange var4);
}

