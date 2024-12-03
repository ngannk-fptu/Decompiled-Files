/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.hooks.resolver;

import java.util.Collection;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

@ConsumerType
public interface ResolverHook {
    public void filterResolvable(Collection<BundleRevision> var1);

    public void filterSingletonCollisions(BundleCapability var1, Collection<BundleCapability> var2);

    public void filterMatches(BundleRequirement var1, Collection<BundleCapability> var2);

    public void end();
}

