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

public class StaticServiceReferenceProxy
implements ServiceReferenceProxy {
    private static final int HASH_CODE = StaticServiceReferenceProxy.class.hashCode() * 13;
    private final ServiceReference target;

    public StaticServiceReferenceProxy(ServiceReference target) {
        Assert.notNull((Object)target);
        this.target = target;
    }

    public Bundle getBundle() {
        return this.target.getBundle();
    }

    public Object getProperty(String key) {
        return this.target.getProperty(key);
    }

    public String[] getPropertyKeys() {
        return this.target.getPropertyKeys();
    }

    public Bundle[] getUsingBundles() {
        return this.target.getUsingBundles();
    }

    public boolean isAssignableTo(Bundle bundle, String className) {
        return this.target.isAssignableTo(bundle, className);
    }

    @Override
    public ServiceReference getTargetServiceReference() {
        return this.target;
    }

    public boolean equals(Object obj) {
        if (obj instanceof StaticServiceReferenceProxy) {
            StaticServiceReferenceProxy other = (StaticServiceReferenceProxy)obj;
            return this.target.equals(other.target);
        }
        return false;
    }

    public int hashCode() {
        return HASH_CODE + this.target.hashCode();
    }

    public int compareTo(Object other) {
        return ServiceComparatorUtil.compare(this.target, other);
    }
}

