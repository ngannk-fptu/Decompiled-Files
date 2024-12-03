/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugin.osgi.container;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;

public interface PackageScannerConfiguration {
    public List<String> getJarIncludes();

    public List<String> getJarExcludes();

    public List<String> getPackageIncludes();

    public List<String> getPackageExcludes();

    public Map<String, String> getPackageVersions();

    public String getCurrentHostVersion();

    public ServletContext getServletContext();

    public Set<String> getOsgiPublicPackages();

    public Set<String> getOsgiPublicPackagesExcludes();

    public Set<String> getOsgiDeprecatedPackages();

    default public boolean treatDeprecatedPackagesAsPublic() {
        return true;
    }

    public Set<String> getApplicationBundledInternalPlugins();
}

