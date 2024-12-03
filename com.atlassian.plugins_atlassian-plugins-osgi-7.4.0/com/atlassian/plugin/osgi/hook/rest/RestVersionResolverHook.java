/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.hooks.resolver.ResolverHook
 *  org.osgi.framework.wiring.BundleCapability
 *  org.osgi.framework.wiring.BundleRequirement
 *  org.osgi.framework.wiring.BundleRevision
 */
package com.atlassian.plugin.osgi.hook.rest;

import com.atlassian.plugin.osgi.hook.rest.JaxRsFilterFactory;
import com.atlassian.plugin.osgi.hook.rest.RestVersionUtils;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

class RestVersionResolverHook
implements ResolverHook {
    private final JaxRsFilterFactory jaxRsFilterFactory;

    public RestVersionResolverHook(JaxRsFilterFactory jaxRsFilterFactory) {
        this.jaxRsFilterFactory = jaxRsFilterFactory;
    }

    public void filterResolvable(Collection<BundleRevision> collection) {
    }

    public void filterSingletonCollisions(BundleCapability bundleCapability, Collection<BundleCapability> collection) {
    }

    public void filterMatches(BundleRequirement bundleRequirement, Collection<BundleCapability> collection) {
        List restCapabilities = collection.stream().filter(RestVersionUtils::isJaxRsPackage).collect(Collectors.toList());
        if (restCapabilities.size() > 1) {
            Bundle bundle = bundleRequirement.getRevision().getBundle();
            restCapabilities.stream().filter(this.jaxRsFilterFactory.getFilter(bundle).negate()).forEach(collection::remove);
        }
    }

    public void end() {
    }
}

