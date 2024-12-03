/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.resolver;

import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.service.resolver.ResolutionException;

public class ResolveException
extends ResolutionException {
    private final BundleRevision m_revision;
    private final BundleRequirement m_req;

    public ResolveException(String msg, BundleRevision revision, BundleRequirement req) {
        super(msg);
        this.m_revision = revision;
        this.m_req = req;
    }

    public BundleRevision getRevision() {
        return this.m_revision;
    }

    public BundleRequirement getRequirement() {
        return this.m_req;
    }
}

