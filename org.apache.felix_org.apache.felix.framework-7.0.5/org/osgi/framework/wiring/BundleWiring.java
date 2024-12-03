/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.wiring;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleReference;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;

@ProviderType
public interface BundleWiring
extends BundleReference,
Wiring {
    public static final int FINDENTRIES_RECURSE = 1;
    public static final int LISTRESOURCES_RECURSE = 1;
    public static final int LISTRESOURCES_LOCAL = 2;

    public boolean isCurrent();

    public boolean isInUse();

    public List<BundleCapability> getCapabilities(String var1);

    public List<BundleRequirement> getRequirements(String var1);

    public List<BundleWire> getProvidedWires(String var1);

    public List<BundleWire> getRequiredWires(String var1);

    public BundleRevision getRevision();

    public ClassLoader getClassLoader();

    public List<URL> findEntries(String var1, String var2, int var3);

    public Collection<String> listResources(String var1, String var2, int var3);

    @Override
    public List<Capability> getResourceCapabilities(String var1);

    @Override
    public List<Requirement> getResourceRequirements(String var1);

    @Override
    public List<Wire> getProvidedResourceWires(String var1);

    @Override
    public List<Wire> getRequiredResourceWires(String var1);

    @Override
    public BundleRevision getResource();
}

