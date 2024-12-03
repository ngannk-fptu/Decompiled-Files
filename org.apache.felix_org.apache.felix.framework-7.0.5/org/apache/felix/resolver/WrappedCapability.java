/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;
import org.osgi.service.resolver.HostedCapability;

public class WrappedCapability
implements HostedCapability {
    private final Resource m_host;
    private final Capability m_cap;
    private final Map<String, Object> m_augmentedAttrs;

    public WrappedCapability(Resource host, Capability cap) {
        this.m_host = host;
        this.m_cap = cap;
        if ("osgi.content".equals(this.m_cap.getNamespace())) {
            HashMap<String, Object> augmentedDirs = new HashMap<String, Object>(this.m_cap.getAttributes());
            Object wrapperUrl = augmentedDirs.get("url");
            wrapperUrl = "wrapper:" + wrapperUrl;
            augmentedDirs.put("url", wrapperUrl);
            this.m_augmentedAttrs = Collections.unmodifiableMap(augmentedDirs);
        } else {
            this.m_augmentedAttrs = this.m_cap.getAttributes();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        WrappedCapability other = (WrappedCapability)obj;
        if (!(this.m_host == other.m_host || this.m_host != null && this.m_host.equals(other.m_host))) {
            return false;
        }
        return this.m_cap == other.m_cap || this.m_cap != null && this.m_cap.equals(other.m_cap);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.m_host != null ? this.m_host.hashCode() : 0);
        hash = 37 * hash + (this.m_cap != null ? this.m_cap.hashCode() : 0);
        return hash;
    }

    @Override
    public Capability getDeclaredCapability() {
        return this.m_cap;
    }

    @Override
    public Resource getResource() {
        return this.m_host;
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
        return this.m_augmentedAttrs;
    }

    public String toString() {
        if (this.m_host == null) {
            return this.getAttributes().toString();
        }
        if (this.getNamespace().equals("osgi.wiring.package")) {
            return "[" + this.m_host + "] " + this.getNamespace() + "; " + this.getAttributes().get("osgi.wiring.package");
        }
        return "[" + this.m_host + "] " + this.getNamespace() + "; " + this.getAttributes();
    }
}

