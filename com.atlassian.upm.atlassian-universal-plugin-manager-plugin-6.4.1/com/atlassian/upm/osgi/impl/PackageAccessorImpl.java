/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSortedSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.Bundle
 *  org.osgi.service.packageadmin.ExportedPackage
 *  org.osgi.service.packageadmin.PackageAdmin
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.VersionRange;
import com.atlassian.upm.osgi.impl.PackageImpl;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

public final class PackageAccessorImpl
implements PackageAccessor {
    private final PackageAdmin admin;
    private static final Comparator<Package> versionComparator = (lhs, rhs) -> lhs.getVersion().compareTo(rhs.getVersion());

    public PackageAccessorImpl(PackageAdmin admin) {
        this.admin = Objects.requireNonNull(admin, "admin");
    }

    @Override
    public Iterable<Package> getPackages() {
        return this.getPackages(null, (Predicate<Package>)Predicates.alwaysTrue());
    }

    @Override
    @Nullable
    public Package getExportedPackage(long bundleId, String name, Version version) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(version, "version");
        Iterable<Package> packages = this.getPackages(name, pkg -> bundleId == pkg.getExportingBundle().getId() && version.compareTo(pkg.getVersion()) == 0);
        Iterator<Package> it = packages.iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    @Nonnull
    public Iterable<Package> getExportedPackages(long bundleId, String name) {
        Objects.requireNonNull(name, "name");
        Iterable<Package> packages = this.getPackages(name, pkg -> bundleId == pkg.getExportingBundle().getId());
        return ImmutableSortedSet.copyOf(versionComparator, packages);
    }

    @Override
    @Nullable
    public Package getImportedPackage(long bundleId, String name, VersionRange versionRange) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(versionRange, "versionRange");
        if (name.equals("*")) {
            return null;
        }
        Iterable<Package> packages = this.getPackages(name, pkg -> versionRange.contains(pkg.getVersion()) && StreamSupport.stream(pkg.getImportingBundles().spliterator(), false).anyMatch(bundle -> bundle.getId() == bundleId));
        ImmutableSortedSet sortedPackages = ImmutableSortedSet.copyOf(versionComparator, packages);
        return sortedPackages.size() == 0 ? null : (Package)sortedPackages.last();
    }

    private Iterable<Package> getPackages(@Nullable String name, @Nullable Predicate<Package> filterFn) {
        ExportedPackage[] packages = name == null ? this.admin.getExportedPackages((Bundle)null) : this.admin.getExportedPackages(name);
        return ImmutableList.copyOf((Collection)StreamSupport.stream(PackageImpl.wrap(this).fromArray((ExportedPackage[])packages).spliterator(), false).filter(filterFn).collect(Collectors.toList()));
    }
}

