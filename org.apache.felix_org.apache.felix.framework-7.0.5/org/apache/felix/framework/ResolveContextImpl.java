/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.felix.framework.StatefulResolver;
import org.apache.felix.framework.resolver.CandidateComparator;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;
import org.osgi.resource.Wiring;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolveContext;

public class ResolveContextImpl
extends ResolveContext {
    private final StatefulResolver m_state;
    private final Map<Resource, Wiring> m_wirings;
    private final StatefulResolver.ResolverHookRecord m_resolverHookrecord;
    private final Collection<BundleRevision> m_mandatory;
    private final Collection<BundleRevision> m_optional;
    private final Collection<BundleRevision> m_ondemand;

    ResolveContextImpl(StatefulResolver state, Map<Resource, Wiring> wirings, StatefulResolver.ResolverHookRecord resolverHookRecord, Collection<BundleRevision> mandatory, Collection<BundleRevision> optional, Collection<BundleRevision> ondemand) {
        this.m_state = state;
        this.m_wirings = wirings;
        this.m_resolverHookrecord = resolverHookRecord;
        this.m_mandatory = mandatory;
        this.m_optional = optional;
        this.m_ondemand = ondemand;
    }

    @Override
    public Collection<Resource> getMandatoryResources() {
        return new ArrayList<Resource>(this.m_mandatory);
    }

    @Override
    public Collection<Resource> getOptionalResources() {
        return new ArrayList<Resource>(this.m_optional);
    }

    public Collection<Resource> getOndemandResources(Resource host) {
        ArrayList<Resource> result = new ArrayList<Resource>();
        for (BundleRevision revision : this.m_ondemand) {
            block1: for (BundleRequirement req : revision.getDeclaredRequirements(null)) {
                if (!req.getNamespace().equals("osgi.wiring.host")) continue;
                for (Capability cap : host.getCapabilities(null)) {
                    if (!cap.getNamespace().equals("osgi.wiring.host") || !req.matches((BundleCapability)cap)) continue;
                    result.add(revision);
                    continue block1;
                }
            }
        }
        return result;
    }

    @Override
    public List<Capability> findProviders(Requirement br) {
        List<Capability> result;
        if (!(br instanceof BundleRequirement)) {
            throw new IllegalStateException("Expected a BundleRequirement");
        }
        List<Capability> caps = result = this.m_state.findProvidersInternal(this.m_resolverHookrecord, br, true, true);
        return caps;
    }

    @Override
    public int insertHostedCapability(List<Capability> caps, HostedCapability hc) {
        int idx = Collections.binarySearch(caps, hc, new CandidateComparator());
        if (idx < 0) {
            idx = Math.abs(idx + 1);
        }
        caps.add(idx, hc);
        return idx;
    }

    @Override
    public boolean isEffective(Requirement br) {
        return this.m_state.isEffective(br);
    }

    @Override
    public Map<Resource, Wiring> getWirings() {
        return this.m_wirings;
    }

    @Override
    public List<Wire> getSubstitutionWires(Wiring wiring) {
        HashSet<String> exportNames = new HashSet<String>();
        for (Capability cap : wiring.getResource().getCapabilities(null)) {
            if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
            exportNames.add((String)cap.getAttributes().get("osgi.wiring.package"));
        }
        for (Wire wire : wiring.getProvidedResourceWires("osgi.wiring.host")) {
            for (Capability cap : wire.getRequirement().getResource().getCapabilities(null)) {
                if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
                exportNames.add((String)cap.getAttributes().get("osgi.wiring.package"));
            }
        }
        ArrayList<Wire> substitutionWires = new ArrayList<Wire>();
        for (Wire wire : wiring.getRequiredResourceWires(null)) {
            if (!wire.getCapability().getNamespace().equals("osgi.wiring.package") || !exportNames.contains(wire.getCapability().getAttributes().get("osgi.wiring.package"))) continue;
            substitutionWires.add(wire);
        }
        return substitutionWires;
    }

    @Override
    public Collection<Resource> findRelatedResources(Resource resource) {
        return !Util.isFragment(resource) ? this.getOndemandResources(resource) : Collections.emptyList();
    }

    @Override
    public void onCancel(Runnable callback) {
        super.onCancel(callback);
    }
}

