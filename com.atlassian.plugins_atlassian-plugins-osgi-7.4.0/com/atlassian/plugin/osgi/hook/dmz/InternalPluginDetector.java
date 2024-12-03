/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.wiring.BundleCapability
 */
package com.atlassian.plugin.osgi.hook.dmz;

import com.atlassian.plugin.osgi.hook.dmz.InternalPackageDetector;
import java.util.Set;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;

public class InternalPluginDetector {
    private final Set<String> bundledPluginKeys;
    private final InternalPackageDetector internalPackageDetector;

    InternalPluginDetector(Set<String> bundledPluginKeys, InternalPackageDetector internalPackageDetector) {
        this.bundledPluginKeys = bundledPluginKeys;
        this.internalPackageDetector = internalPackageDetector;
    }

    public InternalPluginDetector(Set<String> bundledPluginKeys, Set<String> publicPackages, Set<String> publicPackagesExcludes) {
        this(bundledPluginKeys, new InternalPackageDetector(publicPackages, publicPackagesExcludes));
    }

    public boolean isInternalCapability(BundleCapability candidate) {
        if (this.isFromInternalPluginOrSystemBundle(candidate)) {
            return this.internalPackageDetector.isInternalPackage(candidate);
        }
        return false;
    }

    public boolean isDeprecatedCapability(BundleCapability candidate) {
        if (this.isFromInternalPluginOrSystemBundle(candidate)) {
            return this.internalPackageDetector.isDeprecatedPackage(candidate);
        }
        return false;
    }

    private boolean isFromInternalPluginOrSystemBundle(BundleCapability candidate) {
        Bundle exportingBundle = candidate.getRevision().getBundle();
        return this.isInternalPluginOrSystemBundle(exportingBundle);
    }

    public boolean isInternalPluginOrSystemBundle(Bundle bundle) {
        boolean systemBundle = this.isSystemBundle(bundle);
        boolean internalPlugin = this.isInternalPlugin(bundle);
        return internalPlugin || systemBundle;
    }

    public boolean isInternalPlugin(Bundle bundle) {
        String pluginKey = this.getPluginKeyOrSymbolicName(bundle);
        return pluginKey.startsWith("com.atlassian") || this.bundledPluginKeys.contains(pluginKey);
    }

    public boolean isSystemBundle(Bundle bundle) {
        return bundle.getBundleId() == 0L;
    }

    public String getPluginKeyOrSymbolicName(Bundle bundle) {
        String atlassianPluginKey = (String)bundle.getHeaders().get("Atlassian-Plugin-Key");
        if (atlassianPluginKey == null) {
            return bundle.getSymbolicName();
        }
        return atlassianPluginKey;
    }
}

