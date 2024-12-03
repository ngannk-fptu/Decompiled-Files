/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

class ReferenceHolder {
    private final ServiceReference reference;
    private final BundleContext bundleContext;
    private final long id;
    private final int ranking;
    private volatile Object service;

    public ReferenceHolder(ServiceReference reference, BundleContext bundleContext) {
        this.reference = reference;
        this.bundleContext = bundleContext;
        this.id = OsgiServiceReferenceUtils.getServiceId(reference);
        this.ranking = OsgiServiceReferenceUtils.getServiceRanking(reference);
    }

    public Object getService() {
        if (this.service != null) {
            return this.service;
        }
        if (this.reference != null) {
            this.service = this.bundleContext.getService(this.reference);
            return this.service;
        }
        return null;
    }

    public long getId() {
        return this.id;
    }

    public int getRanking() {
        return this.ranking;
    }

    public ServiceReference getReference() {
        return this.reference;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReferenceHolder) {
            return ((ReferenceHolder)obj).id == this.id;
        }
        if (obj instanceof ServiceReference) {
            return this.id == OsgiServiceReferenceUtils.getServiceId(this.reference);
        }
        return false;
    }

    public boolean isWorseThen(ServiceReference ref) {
        long otherId;
        int otherRanking = OsgiServiceReferenceUtils.getServiceRanking(ref);
        if (otherRanking > this.ranking) {
            return true;
        }
        return otherRanking == this.ranking && (otherId = OsgiServiceReferenceUtils.getServiceId(ref)) < this.id;
    }
}

