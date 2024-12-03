/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.felix.resolver.WrappedCapability;
import org.apache.felix.resolver.WrappedRequirement;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

class WrappedResource
implements Resource {
    private final Resource m_host;
    private final List<Resource> m_fragments;
    private final List<Capability> m_cachedCapabilities;
    private final List<Requirement> m_cachedRequirements;

    public WrappedResource(Resource host, List<Resource> fragments) {
        this.m_host = host;
        this.m_fragments = fragments;
        ArrayList<WrappedCapability> caps = new ArrayList<WrappedCapability>();
        for (Capability cap : this.m_host.getCapabilities(null)) {
            caps.add(new WrappedCapability(this, cap));
        }
        if (this.m_fragments != null) {
            for (Resource fragment : this.m_fragments) {
                for (Capability cap : fragment.getCapabilities(null)) {
                    caps.add(new WrappedCapability(this, cap));
                }
            }
        }
        this.m_cachedCapabilities = Collections.unmodifiableList(caps);
        ArrayList<WrappedRequirement> reqs = new ArrayList<WrappedRequirement>();
        for (Requirement req : this.m_host.getRequirements(null)) {
            reqs.add(new WrappedRequirement(this, req));
        }
        if (this.m_fragments != null) {
            for (Resource fragment : this.m_fragments) {
                for (Requirement req : fragment.getRequirements(null)) {
                    if (req.getNamespace().equals("osgi.wiring.host") || req.getNamespace().equals("osgi.ee")) continue;
                    reqs.add(new WrappedRequirement(this, req));
                }
            }
        }
        this.m_cachedRequirements = Collections.unmodifiableList(reqs);
    }

    public Resource getDeclaredResource() {
        return this.m_host;
    }

    public List<Resource> getFragments() {
        return this.m_fragments;
    }

    @Override
    public List<Capability> getCapabilities(String namespace) {
        if (namespace != null) {
            ArrayList<Capability> filtered = new ArrayList<Capability>();
            for (Capability capability : this.m_cachedCapabilities) {
                if (!namespace.equals(capability.getNamespace())) continue;
                filtered.add(capability);
            }
            return Collections.unmodifiableList(filtered);
        }
        return this.m_cachedCapabilities;
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        if (namespace != null) {
            ArrayList<Requirement> filtered = new ArrayList<Requirement>();
            for (Requirement requirement : this.m_cachedRequirements) {
                if (!namespace.equals(requirement.getNamespace())) continue;
                filtered.add(requirement);
            }
            return Collections.unmodifiableList(filtered);
        }
        return this.m_cachedRequirements;
    }

    public String toString() {
        return this.m_host.toString();
    }
}

