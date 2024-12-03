/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.util.Map;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;
import org.osgi.service.resolver.HostedCapability;

class SimpleHostedCapability
implements HostedCapability {
    private final Resource m_host;
    private final Capability m_cap;

    SimpleHostedCapability(Resource host, Capability cap) {
        this.m_host = host;
        this.m_cap = cap;
    }

    @Override
    public Resource getResource() {
        return this.m_host;
    }

    @Override
    public Capability getDeclaredCapability() {
        return this.m_cap;
    }

    @Override
    public String getNamespace() {
        return this.m_cap.getNamespace();
    }

    @Override
    public Map<String, String> getDirectives() {
        return this.m_cap.getDirectives();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.m_cap.getAttributes();
    }
}

