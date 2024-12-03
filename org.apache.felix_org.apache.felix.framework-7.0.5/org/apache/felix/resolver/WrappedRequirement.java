/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import java.util.Map;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class WrappedRequirement
implements Requirement {
    private final Resource m_host;
    private final Requirement m_req;

    public WrappedRequirement(Resource host, Requirement req) {
        this.m_host = host;
        this.m_req = req;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        WrappedRequirement other = (WrappedRequirement)obj;
        if (!(this.m_host == other.m_host || this.m_host != null && this.m_host.equals(other.m_host))) {
            return false;
        }
        return this.m_req == other.m_req || this.m_req != null && this.m_req.equals(other.m_req);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.m_host != null ? this.m_host.hashCode() : 0);
        hash = 37 * hash + (this.m_req != null ? this.m_req.hashCode() : 0);
        return hash;
    }

    public Requirement getDeclaredRequirement() {
        return this.m_req;
    }

    @Override
    public Resource getResource() {
        return this.m_host;
    }

    @Override
    public String getNamespace() {
        return this.m_req.getNamespace();
    }

    @Override
    public Map<String, String> getDirectives() {
        return this.m_req.getDirectives();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.m_req.getAttributes();
    }

    public String toString() {
        return "[" + this.m_host + "] " + this.getNamespace() + "; " + this.getDirectives().get("filter");
    }
}

