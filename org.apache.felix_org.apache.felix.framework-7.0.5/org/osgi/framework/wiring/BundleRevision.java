/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.wiring;

import java.util.List;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

@ProviderType
public interface BundleRevision
extends BundleReference,
Resource {
    public static final String PACKAGE_NAMESPACE = "osgi.wiring.package";
    public static final String BUNDLE_NAMESPACE = "osgi.wiring.bundle";
    public static final String HOST_NAMESPACE = "osgi.wiring.host";
    public static final int TYPE_FRAGMENT = 1;

    public String getSymbolicName();

    public Version getVersion();

    public List<BundleCapability> getDeclaredCapabilities(String var1);

    public List<BundleRequirement> getDeclaredRequirements(String var1);

    public int getTypes();

    public BundleWiring getWiring();

    @Override
    public List<Capability> getCapabilities(String var1);

    @Override
    public List<Requirement> getRequirements(String var1);
}

