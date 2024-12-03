/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.hooks.resolver.ResolverHook
 *  org.osgi.framework.wiring.BundleCapability
 *  org.osgi.framework.wiring.BundleRequirement
 *  org.osgi.framework.wiring.BundleRevision
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.hook.dmz;

import com.atlassian.plugin.osgi.hook.dmz.InternalPackageDetector;
import com.atlassian.plugin.osgi.hook.dmz.InternalPluginDetector;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmzResolverHook
implements ResolverHook {
    private static final Logger LOG = LoggerFactory.getLogger(DmzResolverHook.class);
    static final String ATTR_WIRING_PACKAGE = "osgi.wiring.package";
    private final InternalPluginDetector pluginDetector;
    private final boolean treatDeprecatedPackagesAsPublic;

    DmzResolverHook(InternalPluginDetector internalPluginDetector, boolean treatDeprecatedPackagesAsPublic) {
        this.pluginDetector = Objects.requireNonNull(internalPluginDetector);
        this.treatDeprecatedPackagesAsPublic = treatDeprecatedPackagesAsPublic;
    }

    public DmzResolverHook(Set<String> bundledPluginKeys, Set<String> publicPackages, Set<String> publicPackagesExcludes) {
        this(new InternalPluginDetector(bundledPluginKeys, new InternalPackageDetector(publicPackages, publicPackagesExcludes)), false);
    }

    public void filterMatches(BundleRequirement requirement, Collection<BundleCapability> possibleExports) {
        Bundle importingPlugin = requirement.getRevision().getBundle();
        this.filterMatches(importingPlugin, possibleExports);
    }

    public void filterMatches(Bundle importingPlugin, Collection<BundleCapability> possibleExports) {
        boolean isExternalPlugin = !this.pluginDetector.isInternalPlugin(importingPlugin);
        String pluginKey = this.pluginDetector.getPluginKeyOrSymbolicName(importingPlugin);
        if (isExternalPlugin) {
            LOG.debug("Filtering package exports to non-internal plugin {}", (Object)pluginKey);
            Iterator<BundleCapability> possibleExportsItr = possibleExports.iterator();
            while (possibleExportsItr.hasNext()) {
                BundleCapability bundleCapability = possibleExportsItr.next();
                if (this.pluginDetector.isInternalCapability(bundleCapability)) {
                    possibleExportsItr.remove();
                    LOG.warn("Package {} is internal and is not available for export to plugin {}", DmzResolverHook.getPackage(bundleCapability), (Object)pluginKey);
                    continue;
                }
                if (this.pluginDetector.isDeprecatedCapability(bundleCapability)) {
                    if (this.treatDeprecatedPackagesAsPublic) {
                        LOG.warn("Package {} is deprecated and will be made unavailable for export to plugin {} in a future release", DmzResolverHook.getPackage(bundleCapability), (Object)pluginKey);
                        continue;
                    }
                    possibleExportsItr.remove();
                    LOG.warn("Package {} is deprecated and is not available for export to plugin {}", DmzResolverHook.getPackage(bundleCapability), (Object)pluginKey);
                    continue;
                }
                LOG.debug("Package {} is not internal and can be exported to plugin {}", DmzResolverHook.getPackage(bundleCapability), (Object)pluginKey);
            }
        } else {
            LOG.debug("Skipping package export filtering for internal plugin {}", (Object)pluginKey);
        }
    }

    private static Object getPackage(BundleCapability bundleCapability) {
        return bundleCapability.getAttributes().get(ATTR_WIRING_PACKAGE);
    }

    public void end() {
    }

    public void filterResolvable(Collection<BundleRevision> candidates) {
    }

    public void filterSingletonCollisions(BundleCapability singleton, Collection<BundleCapability> collisions) {
    }
}

