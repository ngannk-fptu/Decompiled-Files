/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.packageadmin;

import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.RequiredBundle;

public interface PackageAdmin {
    public static final int BUNDLE_TYPE_FRAGMENT = 1;

    public ExportedPackage[] getExportedPackages(Bundle var1);

    public ExportedPackage[] getExportedPackages(String var1);

    public ExportedPackage getExportedPackage(String var1);

    public void refreshPackages(Bundle[] var1);

    public boolean resolveBundles(Bundle[] var1);

    public RequiredBundle[] getRequiredBundles(String var1);

    public Bundle[] getBundles(String var1, String var2);

    public Bundle[] getFragments(Bundle var1);

    public Bundle[] getHosts(Bundle var1);

    public Bundle getBundle(Class<?> var1);

    public int getBundleType(Bundle var1);
}

