/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.RequiredBundleImpl;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;

public class PackageAdminImpl
implements PackageAdmin {
    private static final Comparator COMPARATOR = new Comparator(){

        public int compare(Object o1, Object o2) {
            return ((ExportedPackage)o2).getVersion().compareTo(((ExportedPackage)o1).getVersion());
        }
    };
    private Felix m_felix = null;

    PackageAdminImpl(Felix felix) {
        this.m_felix = felix;
    }

    public Bundle getBundle(Class clazz) {
        return this.m_felix.getBundle(clazz);
    }

    @Override
    public Bundle[] getBundles(String symbolicName, String versionRange) {
        VersionRange vr = versionRange == null ? null : new VersionRange(versionRange);
        Bundle[] bundles = this.m_felix.getBundles();
        ArrayList<Bundle> list = new ArrayList<Bundle>();
        for (int i = 0; bundles != null && i < bundles.length; ++i) {
            String sym = bundles[i].getSymbolicName();
            if (sym == null || !sym.equals(symbolicName)) continue;
            Version v = bundles[i].adapt(BundleRevision.class).getVersion();
            if (vr != null && !vr.includes(v)) continue;
            list.add(bundles[i]);
        }
        if (list.isEmpty()) {
            return null;
        }
        bundles = list.toArray(new Bundle[list.size()]);
        Arrays.sort(bundles, new Comparator(){

            public int compare(Object o1, Object o2) {
                Version v1 = ((Bundle)o1).adapt(BundleRevision.class).getVersion();
                Version v2 = ((Bundle)o2).adapt(BundleRevision.class).getVersion();
                return v2.compareTo(v1);
            }
        });
        return bundles;
    }

    @Override
    public int getBundleType(Bundle bundle) {
        Map<String, Object> headerMap = bundle.adapt(BundleRevisionImpl.class).getHeaders();
        if (headerMap.containsKey("Fragment-Host")) {
            return 1;
        }
        return 0;
    }

    @Override
    public ExportedPackage getExportedPackage(String name) {
        ExportedPackage[] pkgs = this.m_felix.getExportedPackages(name);
        if (pkgs == null || pkgs.length == 0) {
            return null;
        }
        Arrays.sort(pkgs, COMPARATOR);
        return pkgs[0];
    }

    @Override
    public ExportedPackage[] getExportedPackages(String name) {
        ExportedPackage[] pkgs = this.m_felix.getExportedPackages(name);
        return pkgs == null || pkgs.length == 0 ? null : pkgs;
    }

    @Override
    public ExportedPackage[] getExportedPackages(Bundle bundle) {
        ExportedPackage[] pkgs = this.m_felix.getExportedPackages(bundle);
        return pkgs == null || pkgs.length == 0 ? null : pkgs;
    }

    @Override
    public Bundle[] getFragments(Bundle bundle) {
        if ((this.getBundleType(bundle) & 1) == 0) {
            ArrayList<Bundle> list = new ArrayList<Bundle>();
            for (BundleRevision revision : bundle.adapt(BundleRevisions.class).getRevisions()) {
                if (revision.getWiring() == null) continue;
                List<BundleRevision> fragments = Util.getFragments(revision.getWiring());
                for (int i = 0; i < fragments.size(); ++i) {
                    Bundle b = fragments.get(i).getBundle();
                    if (b == null) continue;
                    list.add(b);
                }
            }
            return list.isEmpty() ? null : list.toArray(new Bundle[list.size()]);
        }
        return null;
    }

    @Override
    public Bundle[] getHosts(Bundle bundle) {
        if ((this.getBundleType(bundle) & 1) != 0) {
            ArrayList<Bundle> list = new ArrayList<Bundle>();
            for (BundleRevision revision : bundle.adapt(BundleRevisions.class).getRevisions()) {
                if (revision.getWiring() == null) continue;
                List<BundleWire> hostWires = revision.getWiring().getRequiredWires(null);
                for (int i = 0; hostWires != null && i < hostWires.size(); ++i) {
                    Bundle b;
                    BundleWire wire = hostWires.get(i);
                    if (!wire.getCapability().getNamespace().equals("osgi.wiring.host") || (b = wire.getProviderWiring().getBundle()) == null) continue;
                    list.add(b);
                }
            }
            return list.isEmpty() ? null : list.toArray(new Bundle[list.size()]);
        }
        return null;
    }

    @Override
    public RequiredBundle[] getRequiredBundles(String symbolicName) {
        ArrayList<RequiredBundleImpl> list = new ArrayList<RequiredBundleImpl>();
        for (Bundle bundle : this.m_felix.getBundles()) {
            if (symbolicName != null && !symbolicName.equals(bundle.getSymbolicName())) continue;
            list.add(new RequiredBundleImpl(this.m_felix, (BundleImpl)bundle));
        }
        return list.isEmpty() ? null : list.toArray(new RequiredBundle[list.size()]);
    }

    @Override
    public void refreshPackages(Bundle[] bundles) throws SecurityException {
        List<Bundle> list = bundles == null ? null : Arrays.asList(bundles);
        this.m_felix.adapt(FrameworkWiring.class).refreshBundles(list, new FrameworkListener[0]);
    }

    @Override
    public boolean resolveBundles(Bundle[] bundles) {
        List<Bundle> list = bundles == null ? null : Arrays.asList(bundles);
        return this.m_felix.adapt(FrameworkWiring.class).resolveBundles(list);
    }
}

