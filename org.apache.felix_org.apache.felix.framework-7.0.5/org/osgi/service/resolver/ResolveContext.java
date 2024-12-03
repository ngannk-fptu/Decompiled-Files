/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.service.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;

@ConsumerType
public abstract class ResolveContext {
    public Collection<Resource> getMandatoryResources() {
        return ResolveContext.emptyCollection();
    }

    public Collection<Resource> getOptionalResources() {
        return ResolveContext.emptyCollection();
    }

    private static <T> Collection<T> emptyCollection() {
        return Collections.EMPTY_LIST;
    }

    public abstract List<Capability> findProviders(Requirement var1);

    public abstract int insertHostedCapability(List<Capability> var1, HostedCapability var2);

    public abstract boolean isEffective(Requirement var1);

    public abstract Map<Resource, Wiring> getWirings();

    public Collection<Resource> findRelatedResources(Resource resource) {
        return Collections.emptyList();
    }

    public void onCancel(Runnable callback) {
    }

    public List<Wire> getSubstitutionWires(Wiring wiring) {
        HashSet<String> exportNames = new HashSet<String>();
        for (Capability cap : wiring.getResource().getCapabilities(null)) {
            if (!"osgi.wiring.package".equals(cap.getNamespace())) continue;
            exportNames.add((String)cap.getAttributes().get("osgi.wiring.package"));
        }
        for (Wire wire : wiring.getProvidedResourceWires(null)) {
            if (!"osgi.wiring.host".equals(wire.getCapability().getNamespace())) continue;
            Resource fragment = wire.getRequirement().getResource();
            for (Capability cap : fragment.getCapabilities(null)) {
                if (!"osgi.wiring.package".equals(cap.getNamespace())) continue;
                exportNames.add((String)cap.getAttributes().get("osgi.wiring.package"));
            }
        }
        ArrayList<Wire> substitutionWires = new ArrayList<Wire>();
        for (Wire wire : wiring.getRequiredResourceWires(null)) {
            if (!"osgi.wiring.package".equals(wire.getCapability().getNamespace()) || !exportNames.contains(wire.getCapability().getAttributes().get("osgi.wiring.package"))) continue;
            substitutionWires.add(wire);
        }
        return substitutionWires;
    }
}

