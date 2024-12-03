/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.Set;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.RequiredBundle;

class RequiredBundleImpl
implements RequiredBundle {
    private final Felix m_felix;
    private final BundleImpl m_bundle;
    private volatile String m_toString = null;
    private volatile String m_versionString = null;

    public RequiredBundleImpl(Felix felix, BundleImpl bundle) {
        this.m_felix = felix;
        this.m_bundle = bundle;
    }

    @Override
    public String getSymbolicName() {
        return this.m_bundle.getSymbolicName();
    }

    @Override
    public Bundle getBundle() {
        return this.m_bundle;
    }

    @Override
    public Bundle[] getRequiringBundles() {
        if (this.m_bundle.isStale()) {
            return null;
        }
        Set<Bundle> set = this.m_felix.getRequiringBundles(this.m_bundle);
        return set.toArray(new Bundle[set.size()]);
    }

    @Override
    public Version getVersion() {
        return this.m_bundle.getVersion();
    }

    @Override
    public boolean isRemovalPending() {
        return this.m_bundle.isRemovalPending();
    }

    public String toString() {
        if (this.m_toString == null) {
            this.m_toString = this.m_bundle.getSymbolicName() + "; version=" + this.m_bundle.getVersion();
        }
        return this.m_toString;
    }
}

