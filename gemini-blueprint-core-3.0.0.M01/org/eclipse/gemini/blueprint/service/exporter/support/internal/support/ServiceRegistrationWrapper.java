/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.ServiceRegistration
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.support;

import java.util.Dictionary;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class ServiceRegistrationWrapper
implements ServiceRegistration {
    private volatile ServiceRegistration delegate;

    public ServiceRegistrationWrapper(ServiceRegistration delegate) {
        this.delegate = delegate;
    }

    public ServiceReference getReference() {
        return this.delegate.getReference();
    }

    public void setProperties(Dictionary properties) {
        this.delegate.setProperties(properties);
    }

    public void swap(ServiceRegistration other) {
        this.delegate = other;
    }

    public void unregister() {
        throw new UnsupportedOperationException("Sevice unregistration is not allowed");
    }
}

