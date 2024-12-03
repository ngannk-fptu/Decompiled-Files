/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.BundleTracker
 *  org.osgi.util.tracker.BundleTrackerCustomizer
 */
package org.apache.axiom.locator;

import org.apache.axiom.locator.OSGiOMMetaFactoryLocator;
import org.apache.axiom.om.OMAbstractFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

public class Activator
implements BundleActivator {
    private BundleTracker tracker;

    public void start(BundleContext context) throws Exception {
        OSGiOMMetaFactoryLocator locator = new OSGiOMMetaFactoryLocator(context);
        OMAbstractFactory.setMetaFactoryLocator(locator);
        this.tracker = new BundleTracker(context, 32, (BundleTrackerCustomizer)locator);
        this.tracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        this.tracker.close();
        OMAbstractFactory.setMetaFactoryLocator(null);
    }
}

