/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.support;

import java.util.Dictionary;
import java.util.Map;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.UnregistrationNotifier;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.util.Assert;

public class ServiceRegistrationDecorator
implements ServiceRegistration {
    private final ServiceRegistration delegate;
    private volatile UnregistrationNotifier notifier;

    public ServiceRegistrationDecorator(ServiceRegistration registration) {
        Assert.notNull((Object)registration);
        this.delegate = registration;
    }

    void setNotifier(UnregistrationNotifier notifier) {
        this.notifier = notifier;
    }

    public ServiceReference getReference() {
        return this.delegate.getReference();
    }

    public void setProperties(Dictionary properties) {
        this.delegate.setProperties(properties);
    }

    public void unregister() {
        Map properties;
        ServiceReference reference = this.delegate.getReference();
        Map map = properties = reference != null ? (Map)((Object)OsgiServiceReferenceUtils.getServicePropertiesSnapshot(reference)) : null;
        if (this.notifier != null) {
            this.notifier.unregister(properties);
        }
        this.delegate.unregister();
    }

    public String toString() {
        return "ServiceRegistrationWrapper for " + this.delegate.toString();
    }
}

