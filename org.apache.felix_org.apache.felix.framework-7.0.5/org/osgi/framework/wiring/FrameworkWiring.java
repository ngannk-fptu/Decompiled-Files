/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.wiring;

import java.util.Collection;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.resource.Requirement;

@ProviderType
public interface FrameworkWiring
extends BundleReference {
    public void refreshBundles(Collection<Bundle> var1, FrameworkListener ... var2);

    public boolean resolveBundles(Collection<Bundle> var1);

    public Collection<Bundle> getRemovalPendingBundles();

    public Collection<Bundle> getDependencyClosure(Collection<Bundle> var1);

    public Collection<BundleCapability> findProviders(Requirement var1);
}

