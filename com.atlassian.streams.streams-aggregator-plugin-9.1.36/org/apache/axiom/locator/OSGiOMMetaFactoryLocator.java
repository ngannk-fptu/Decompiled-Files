/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleEvent
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.util.tracker.BundleTrackerCustomizer
 */
package org.apache.axiom.locator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.apache.axiom.locator.Implementation;
import org.apache.axiom.locator.ImplementationFactory;
import org.apache.axiom.locator.OSGiLoader;
import org.apache.axiom.locator.PriorityBasedOMMetaFactoryLocator;
import org.apache.axiom.locator.RegisteredImplementation;
import org.apache.axiom.om.OMMetaFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTrackerCustomizer;

final class OSGiOMMetaFactoryLocator
extends PriorityBasedOMMetaFactoryLocator
implements BundleTrackerCustomizer {
    private final BundleContext apiBundleContext;
    private final List implementations = new ArrayList();

    OSGiOMMetaFactoryLocator(BundleContext apiBundleContext) {
        this.apiBundleContext = apiBundleContext;
    }

    public synchronized OMMetaFactory getOMMetaFactory(String feature) {
        return super.getOMMetaFactory(feature);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object addingBundle(Bundle bundle, BundleEvent event) {
        URL descriptorUrl = bundle.getEntry("META-INF/axiom.xml");
        if (descriptorUrl != null) {
            List discoveredImplementations = ImplementationFactory.parseDescriptor(new OSGiLoader(bundle), descriptorUrl);
            ArrayList<RegisteredImplementation> registeredImplementations = new ArrayList<RegisteredImplementation>(discoveredImplementations.size());
            OSGiOMMetaFactoryLocator oSGiOMMetaFactoryLocator = this;
            synchronized (oSGiOMMetaFactoryLocator) {
                this.implementations.addAll(discoveredImplementations);
                this.loadImplementations(this.implementations);
            }
            for (Implementation implementation : discoveredImplementations) {
                Hashtable<String, String> properties = new Hashtable<String, String>();
                properties.put("implementationName", implementation.getName());
                ServiceRegistration registration = bundle.getBundleContext().registerService(OMMetaFactory.class.getName(), (Object)implementation.getMetaFactory(), properties);
                ServiceReference reference = registration.getReference();
                this.apiBundleContext.getService(reference);
                registeredImplementations.add(new RegisteredImplementation(implementation, registration, reference));
            }
            return registeredImplementations;
        }
        return null;
    }

    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        if (object != null) {
            for (RegisteredImplementation registeredImplementation : (List)object) {
                this.apiBundleContext.ungetService(registeredImplementation.getReference());
                registeredImplementation.getRegistration().unregister();
                OSGiOMMetaFactoryLocator oSGiOMMetaFactoryLocator = this;
                synchronized (oSGiOMMetaFactoryLocator) {
                    this.implementations.remove(registeredImplementation.getImplementation());
                }
            }
            OSGiOMMetaFactoryLocator oSGiOMMetaFactoryLocator = this;
            synchronized (oSGiOMMetaFactoryLocator) {
                this.loadImplementations(this.implementations);
            }
        }
    }
}

