/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.wiring;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Wire;

@ProviderType
public interface BundleWire
extends Wire {
    @Override
    public BundleCapability getCapability();

    @Override
    public BundleRequirement getRequirement();

    public BundleWiring getProviderWiring();

    public BundleWiring getRequirerWiring();

    @Override
    public BundleRevision getProvider();

    @Override
    public BundleRevision getRequirer();
}

