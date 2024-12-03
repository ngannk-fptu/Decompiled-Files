/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.wiring;

import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;

@ProviderType
public interface BundleCapability
extends Capability {
    public BundleRevision getRevision();

    @Override
    public String getNamespace();

    @Override
    public Map<String, String> getDirectives();

    @Override
    public Map<String, Object> getAttributes();

    @Override
    public BundleRevision getResource();
}

