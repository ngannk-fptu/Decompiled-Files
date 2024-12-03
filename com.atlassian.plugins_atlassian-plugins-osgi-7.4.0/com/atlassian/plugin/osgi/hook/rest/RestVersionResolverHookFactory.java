/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.hooks.resolver.ResolverHook
 *  org.osgi.framework.hooks.resolver.ResolverHookFactory
 *  org.osgi.framework.wiring.BundleRevision
 */
package com.atlassian.plugin.osgi.hook.rest;

import com.atlassian.plugin.osgi.hook.rest.JaxRsFilterFactory;
import com.atlassian.plugin.osgi.hook.rest.RestVersionResolverHook;
import java.util.Collection;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.wiring.BundleRevision;

public class RestVersionResolverHookFactory
implements ResolverHookFactory {
    public ResolverHook begin(Collection<BundleRevision> collection) {
        return new RestVersionResolverHook(new JaxRsFilterFactory());
    }
}

