/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.ServiceRegistration
 */
package org.apache.axiom.locator;

import org.apache.axiom.locator.Implementation;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

class RegisteredImplementation {
    private final Implementation implementation;
    private final ServiceRegistration registration;
    private final ServiceReference reference;

    RegisteredImplementation(Implementation implementation, ServiceRegistration registration, ServiceReference reference) {
        this.implementation = implementation;
        this.registration = registration;
        this.reference = reference;
    }

    Implementation getImplementation() {
        return this.implementation;
    }

    ServiceRegistration getRegistration() {
        return this.registration;
    }

    ServiceReference getReference() {
        return this.reference;
    }
}

