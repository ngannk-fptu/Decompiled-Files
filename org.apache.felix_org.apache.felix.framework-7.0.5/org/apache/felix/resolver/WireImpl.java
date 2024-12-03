/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.resource.Wire;

class WireImpl
implements Wire {
    private final Resource m_requirer;
    private final Requirement m_req;
    private final Resource m_provider;
    private final Capability m_cap;

    public WireImpl(Resource requirer, Requirement req, Resource provider, Capability cap) {
        this.m_requirer = requirer;
        this.m_req = req;
        this.m_provider = provider;
        this.m_cap = cap;
    }

    @Override
    public Resource getRequirer() {
        return this.m_requirer;
    }

    @Override
    public Requirement getRequirement() {
        return this.m_req;
    }

    @Override
    public Resource getProvider() {
        return this.m_provider;
    }

    @Override
    public Capability getCapability() {
        return this.m_cap;
    }

    public String toString() {
        return this.m_req + " -> [" + this.m_provider + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Wire)) {
            return false;
        }
        Wire other = (Wire)obj;
        if (!(this.m_requirer == other.getRequirer() || this.m_requirer != null && this.m_requirer.equals(other.getRequirer()))) {
            return false;
        }
        if (!(this.m_req == other.getRequirement() || this.m_req != null && this.m_req.equals(other.getRequirement()))) {
            return false;
        }
        if (!(this.m_provider == other.getProvider() || this.m_provider != null && this.m_provider.equals(other.getProvider()))) {
            return false;
        }
        return this.m_cap == other.getCapability() || this.m_cap != null && this.m_cap.equals(other.getCapability());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.m_requirer != null ? this.m_requirer.hashCode() : 0);
        hash = 29 * hash + (this.m_req != null ? this.m_req.hashCode() : 0);
        hash = 29 * hash + (this.m_provider != null ? this.m_provider.hashCode() : 0);
        hash = 29 * hash + (this.m_cap != null ? this.m_cap.hashCode() : 0);
        return hash;
    }
}

