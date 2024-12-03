/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.service.resolver;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

@ProviderType
public interface HostedCapability
extends Capability {
    @Override
    public Resource getResource();

    public Capability getDeclaredCapability();
}

