/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 */
package org.apache.tika.config;

import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.Detector;
import org.apache.tika.parser.Parser;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class TikaActivator
implements BundleActivator,
ServiceTrackerCustomizer {
    private ServiceTracker detectorTracker;
    private ServiceTracker parserTracker;
    private BundleContext bundleContext;

    public void start(BundleContext context) throws Exception {
        this.bundleContext = context;
        this.detectorTracker = new ServiceTracker(context, Detector.class.getName(), (ServiceTrackerCustomizer)this);
        this.parserTracker = new ServiceTracker(context, Parser.class.getName(), (ServiceTrackerCustomizer)this);
        this.detectorTracker.open();
        this.parserTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        this.parserTracker.close();
        this.detectorTracker.close();
    }

    public Object addingService(ServiceReference reference) {
        int rank = 0;
        Object property = reference.getProperty("service.ranking");
        if (property instanceof Integer) {
            rank = (Integer)property;
        }
        Object service = this.bundleContext.getService(reference);
        ServiceLoader.addService(reference, service, rank);
        return service;
    }

    public void modifiedService(ServiceReference reference, Object service) {
    }

    public void removedService(ServiceReference reference, Object service) {
        ServiceLoader.removeService(reference);
        this.bundleContext.ungetService(reference);
    }
}

