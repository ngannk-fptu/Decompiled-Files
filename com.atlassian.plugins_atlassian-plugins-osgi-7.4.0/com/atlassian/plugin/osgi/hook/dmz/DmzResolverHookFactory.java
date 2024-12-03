/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.hooks.resolver.ResolverHook
 *  org.osgi.framework.hooks.resolver.ResolverHookFactory
 *  org.osgi.framework.wiring.BundleRevision
 */
package com.atlassian.plugin.osgi.hook.dmz;

import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.atlassian.plugin.osgi.hook.dmz.DmzResolverHook;
import com.atlassian.plugin.osgi.hook.dmz.InternalPackageDetector;
import com.atlassian.plugin.osgi.hook.dmz.InternalPluginDetector;
import java.util.Collection;
import java.util.Objects;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleRevision;

public class DmzResolverHookFactory
implements ResolverHookFactory {
    private final PackageScannerConfiguration config;

    public DmzResolverHookFactory(PackageScannerConfiguration packageScannerConfig) {
        this.config = Objects.requireNonNull(packageScannerConfig, "Configuration required.");
    }

    public ResolverHook begin(Collection<BundleRevision> triggers) {
        return this.createHook();
    }

    private ResolverHook createHook() {
        return new DmzResolverHook(new InternalPluginDetector(this.config.getApplicationBundledInternalPlugins(), new InternalPackageDetector(this.config.getOsgiPublicPackages(), this.config.getOsgiPublicPackagesExcludes(), this.config.getOsgiDeprecatedPackages())), this.config.treatDeprecatedPackagesAsPublic());
    }
}

