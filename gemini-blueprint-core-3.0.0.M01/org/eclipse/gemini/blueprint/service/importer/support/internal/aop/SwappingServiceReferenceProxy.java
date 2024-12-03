/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import org.eclipse.gemini.blueprint.service.importer.ServiceReferenceProxy;
import org.eclipse.gemini.blueprint.service.importer.support.internal.util.ServiceComparatorUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;

class SwappingServiceReferenceProxy
implements ServiceReferenceProxy {
    private static final int HASH_CODE = SwappingServiceReferenceProxy.class.hashCode() * 13;
    private static final Object TIE_MONITOR = new Object();
    private ServiceReference delegate;

    SwappingServiceReferenceProxy() {
    }

    synchronized ServiceReference swapDelegates(ServiceReference newDelegate) {
        Assert.notNull((Object)newDelegate);
        ServiceReference old = this.delegate;
        this.delegate = newDelegate;
        return old;
    }

    public synchronized Bundle getBundle() {
        return this.delegate == null ? null : this.delegate.getBundle();
    }

    public synchronized Object getProperty(String key) {
        return this.delegate == null ? null : this.delegate.getProperty(key);
    }

    public synchronized String[] getPropertyKeys() {
        return this.delegate == null ? new String[]{} : this.delegate.getPropertyKeys();
    }

    public synchronized Bundle[] getUsingBundles() {
        return this.delegate == null ? new Bundle[]{} : this.delegate.getUsingBundles();
    }

    public synchronized boolean isAssignableTo(Bundle bundle, String className) {
        return this.delegate == null ? false : this.delegate.isAssignableTo(bundle, className);
    }

    @Override
    public synchronized ServiceReference getTargetServiceReference() {
        return this.delegate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object obj) {
        int otherHash;
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        if (!(obj instanceof SwappingServiceReferenceProxy)) {
            return false;
        }
        SwappingServiceReferenceProxy other = (SwappingServiceReferenceProxy)obj;
        int thisHash = System.identityHashCode(this);
        if (thisHash > (otherHash = System.identityHashCode(other))) {
            SwappingServiceReferenceProxy swappingServiceReferenceProxy = this;
            synchronized (swappingServiceReferenceProxy) {
                Object object = obj;
                synchronized (object) {
                    return this.delegateEquals(other);
                }
            }
        }
        if (thisHash < otherHash) {
            Object object = obj;
            synchronized (object) {
                SwappingServiceReferenceProxy swappingServiceReferenceProxy = this;
                synchronized (swappingServiceReferenceProxy) {
                    return this.delegateEquals(other);
                }
            }
        }
        Object object = TIE_MONITOR;
        synchronized (object) {
            SwappingServiceReferenceProxy swappingServiceReferenceProxy = this;
            synchronized (swappingServiceReferenceProxy) {
                Object object2 = obj;
                synchronized (object2) {
                    return this.delegateEquals(other);
                }
            }
        }
    }

    public boolean delegateEquals(SwappingServiceReferenceProxy other) {
        return this.delegate == null ? other.delegate == null : this.delegate.equals(other.delegate);
    }

    public synchronized int hashCode() {
        return HASH_CODE + (this.delegate == null ? 0 : this.delegate.hashCode());
    }

    public synchronized int compareTo(Object other) {
        if (this == other) {
            return 0;
        }
        return ServiceComparatorUtil.compare(this.delegate, other);
    }
}

