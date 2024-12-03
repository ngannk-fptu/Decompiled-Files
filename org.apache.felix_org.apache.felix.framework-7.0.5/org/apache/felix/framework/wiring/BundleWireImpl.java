/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.wiring;

import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

public class BundleWireImpl
implements BundleWire {
    private final BundleRevision m_requirer;
    private final BundleRequirement m_req;
    private final BundleRevision m_provider;
    private final BundleCapability m_cap;

    public BundleWireImpl(BundleRevision requirer, BundleRequirement req, BundleRevision provider, BundleCapability cap) {
        this.m_requirer = requirer;
        this.m_req = req;
        this.m_provider = provider;
        this.m_cap = cap;
    }

    @Override
    public BundleRevision getRequirer() {
        return this.m_requirer;
    }

    @Override
    public BundleWiring getRequirerWiring() {
        return this.m_requirer.getWiring();
    }

    @Override
    public BundleRequirement getRequirement() {
        return this.m_req;
    }

    @Override
    public BundleRevision getProvider() {
        return this.m_provider;
    }

    @Override
    public BundleWiring getProviderWiring() {
        return this.m_provider.getWiring();
    }

    @Override
    public BundleCapability getCapability() {
        return this.m_cap;
    }

    public String toString() {
        return this.m_req + " -> [" + this.m_provider + "]";
    }
}

