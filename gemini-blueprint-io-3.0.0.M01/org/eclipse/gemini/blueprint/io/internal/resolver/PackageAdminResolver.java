/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.packageadmin.ExportedPackage
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.io.internal.resolver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.io.internal.OsgiHeaderUtils;
import org.eclipse.gemini.blueprint.io.internal.resolver.DependencyResolver;
import org.eclipse.gemini.blueprint.io.internal.resolver.ImportedBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class PackageAdminResolver
implements DependencyResolver {
    private static final Log log = LogFactory.getLog(PackageAdminResolver.class);
    private final BundleContext bundleContext;

    public PackageAdminResolver(BundleContext bundleContext) {
        Assert.notNull((Object)bundleContext);
        this.bundleContext = bundleContext;
    }

    @Override
    public ImportedBundle[] getImportedBundles(Bundle bundle) {
        boolean trace = log.isTraceEnabled();
        PackageAdmin pa = this.getPackageAdmin();
        LinkedHashMap<Bundle, List<String>> importedBundles = new LinkedHashMap<Bundle, List<String>>(8);
        String[] entries = OsgiHeaderUtils.getRequireBundle(bundle);
        for (int i = 0; i < entries.length; ++i) {
            String versionRange;
            String[] parsed = OsgiHeaderUtils.parseRequiredBundleString(entries[i]);
            String symName = parsed[0].trim();
            Object[] foundBundles = pa.getBundles(symName, versionRange = parsed[1].trim());
            if (!ObjectUtils.isEmpty((Object[])foundBundles)) {
                Object requiredBundle = foundBundles[0];
                ExportedPackage[] exportedPackages = pa.getExportedPackages((Bundle)requiredBundle);
                if (exportedPackages == null) continue;
                this.addExportedPackages(importedBundles, (Bundle)requiredBundle, exportedPackages);
                continue;
            }
            if (!trace) continue;
            log.trace((Object)("Cannot find required bundle " + symName + "|" + versionRange));
        }
        Bundle[] bundles = this.bundleContext.getBundles();
        for (int i = 0; i < bundles.length; ++i) {
            ExportedPackage[] epa;
            Bundle analyzedBundle = bundles[i];
            if (importedBundles.containsKey(analyzedBundle) || (epa = pa.getExportedPackages(analyzedBundle)) == null) continue;
            for (int j = 0; j < epa.length; ++j) {
                ExportedPackage exportedPackage = epa[j];
                Bundle[] importingBundles = exportedPackage.getImportingBundles();
                if (importingBundles == null) continue;
                for (int k = 0; k < importingBundles.length; ++k) {
                    if (!bundle.equals(importingBundles[k])) continue;
                    this.addImportedBundle(importedBundles, exportedPackage);
                }
            }
        }
        ArrayList<ImportedBundle> importedBundlesList = new ArrayList<ImportedBundle>(importedBundles.size());
        for (Map.Entry entry : importedBundles.entrySet()) {
            Bundle importedBundle = (Bundle)entry.getKey();
            List packages = (List)entry.getValue();
            importedBundlesList.add(new ImportedBundle(importedBundle, packages.toArray(new String[packages.size()])));
        }
        return importedBundlesList.toArray(new ImportedBundle[importedBundlesList.size()]);
    }

    private void addImportedBundle(Map<Bundle, List<String>> map, ExportedPackage expPackage) {
        Bundle bnd = expPackage.getExportingBundle();
        List<String> packages = map.get(bnd);
        if (packages == null) {
            packages = new ArrayList<String>(4);
            map.put(bnd, packages);
        }
        packages.add(new String(expPackage.getName()));
    }

    private void addExportedPackages(Map<Bundle, List<String>> map, Bundle bundle, ExportedPackage[] pkgs) {
        List<String> packages = map.get(bundle);
        if (packages == null) {
            packages = new ArrayList<String>(pkgs.length);
            map.put(bundle, packages);
        }
        for (int i = 0; i < pkgs.length; ++i) {
            packages.add(pkgs[i].getName());
        }
    }

    private PackageAdmin getPackageAdmin() {
        return AccessController.doPrivileged(new PrivilegedAction<PackageAdmin>(){

            @Override
            public PackageAdmin run() {
                ServiceReference ref = PackageAdminResolver.this.bundleContext.getServiceReference(PackageAdmin.class.getName());
                if (ref == null) {
                    throw new IllegalStateException(PackageAdmin.class.getName() + " service is required");
                }
                return (PackageAdmin)PackageAdminResolver.this.bundleContext.getService(ref);
            }
        });
    }
}

