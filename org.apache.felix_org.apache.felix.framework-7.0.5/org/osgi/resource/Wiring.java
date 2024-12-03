/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.resource;

import java.util.List;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;

@ConsumerType
public interface Wiring {
    public List<Capability> getResourceCapabilities(String var1);

    public List<Requirement> getResourceRequirements(String var1);

    public List<Wire> getProvidedResourceWires(String var1);

    public List<Wire> getRequiredResourceWires(String var1);

    public Resource getResource();
}

