/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.service.packageadmin.ExportedPackage
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.Iterables;
import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.VersionRange;
import com.atlassian.upm.osgi.impl.BundleImpl;
import com.atlassian.upm.osgi.impl.Versions;
import com.atlassian.upm.osgi.impl.Wrapper;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.osgi.service.packageadmin.ExportedPackage;

public final class PackageImpl
implements Package {
    private final ExportedPackage pkg;
    private final PackageAccessor packageAccessor;
    private final Predicate<Bundle.HeaderClause> matchesThis = headerClause -> {
        String versionRange = headerClause.getParameters().get("version");
        return headerClause.getPath().equals(this.getName()) && VersionRange.fromString(versionRange == null ? "0" : versionRange).contains(this.getVersion());
    };

    PackageImpl(ExportedPackage pkg, PackageAccessor packageAccessor) {
        this.pkg = pkg;
        this.packageAccessor = packageAccessor;
    }

    @Override
    public String getName() {
        return this.pkg.getName();
    }

    @Override
    public Bundle getExportingBundle() {
        return BundleImpl.wrap(this.packageAccessor).fromSingleton(this.pkg.getExportingBundle());
    }

    @Override
    public Iterable<Bundle> getImportingBundles() {
        Bundle exportingBundle = this.getExportingBundle();
        Iterable<Bundle> importingBundles = BundleImpl.wrap(this.packageAccessor).fromArray((org.osgi.framework.Bundle[])this.pkg.getImportingBundles());
        Iterable<Bundle.HeaderClause> importClauses = exportingBundle.getParsedHeaders().get("Import-Package");
        if (importClauses != null && StreamSupport.stream(importClauses.spliterator(), false).anyMatch(this.matchesThis)) {
            return Collections.unmodifiableList(Stream.concat(Iterables.toStream(importingBundles), Stream.of(exportingBundle)).collect(Collectors.toList()));
        }
        return importingBundles;
    }

    @Override
    public Version getVersion() {
        return Versions.wrap.fromSingleton(this.pkg.getVersion());
    }

    protected static Wrapper<ExportedPackage, Package> wrap(final PackageAccessor packageAccessor) {
        return new Wrapper<ExportedPackage, Package>("package"){

            @Override
            protected Package wrap(ExportedPackage exportedPackage) {
                return new PackageImpl(exportedPackage, packageAccessor);
            }
        };
    }
}

