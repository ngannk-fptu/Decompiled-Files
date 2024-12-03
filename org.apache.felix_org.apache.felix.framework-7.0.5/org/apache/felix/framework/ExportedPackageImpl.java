/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.Set;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.service.packageadmin.ExportedPackage;

class ExportedPackageImpl
implements ExportedPackage {
    private final Felix m_felix;
    private final BundleImpl m_exportingBundle;
    private final BundleRevision m_exportingRevision;
    private final BundleCapability m_export;
    private final String m_pkgName;
    private final Version m_version;

    public ExportedPackageImpl(Felix felix, BundleImpl exporter, BundleRevision revision, BundleCapability export) {
        this.m_felix = felix;
        this.m_exportingBundle = exporter;
        this.m_exportingRevision = revision;
        this.m_export = export;
        this.m_pkgName = (String)this.m_export.getAttributes().get("osgi.wiring.package");
        this.m_version = !this.m_export.getAttributes().containsKey("version") ? Version.emptyVersion : (Version)this.m_export.getAttributes().get("version");
    }

    @Override
    public Bundle getExportingBundle() {
        if (this.m_exportingBundle.isStale()) {
            return null;
        }
        return this.m_exportingBundle;
    }

    @Override
    public Bundle[] getImportingBundles() {
        if (this.m_exportingBundle.isStale()) {
            return null;
        }
        Set<Bundle> set = this.m_felix.getImportingBundles(this.m_exportingBundle, this.m_export);
        return set.toArray(new Bundle[set.size()]);
    }

    @Override
    public String getName() {
        return this.m_pkgName;
    }

    @Override
    public String getSpecificationVersion() {
        return this.m_version.toString();
    }

    @Override
    public Version getVersion() {
        return this.m_version;
    }

    @Override
    public boolean isRemovalPending() {
        return this.m_exportingBundle.isRemovalPending();
    }

    public String toString() {
        return this.m_pkgName + "; version=" + this.m_version;
    }
}

