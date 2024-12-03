/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class OsgiListenerUtils {
    private static final Log log = LogFactory.getLog(OsgiListenerUtils.class);

    public static void addServiceListener(BundleContext context, ServiceListener listener, Filter filter) {
        String toStringFilter = filter == null ? null : filter.toString();
        OsgiListenerUtils.addServiceListener(context, listener, toStringFilter);
    }

    public static void addServiceListener(BundleContext context, ServiceListener listener, String filter) {
        OsgiListenerUtils.registerListener(context, listener, filter);
        OsgiListenerUtils.dispatchServiceRegistrationEvents(OsgiServiceReferenceUtils.getServiceReferences(context, filter), listener);
    }

    private static void registerListener(BundleContext context, ServiceListener listener, String filter) {
        Assert.notNull((Object)context);
        Assert.notNull((Object)listener);
        try {
            context.addServiceListener(listener, filter);
        }
        catch (InvalidSyntaxException isex) {
            throw (RuntimeException)new IllegalArgumentException("Invalid filter").initCause(isex);
        }
    }

    private static void dispatchServiceRegistrationEvents(ServiceReference[] alreadyRegistered, ServiceListener listener) {
        if (log.isTraceEnabled()) {
            log.trace((Object)("Calling listener for already registered services: " + ObjectUtils.nullSafeToString((Object[])alreadyRegistered)));
        }
        if (alreadyRegistered != null) {
            for (int i = 0; i < alreadyRegistered.length; ++i) {
                listener.serviceChanged(new ServiceEvent(1, alreadyRegistered[i]));
            }
        }
    }

    public static void addSingleServiceListener(BundleContext context, ServiceListener listener, Filter filter) {
        String toStringFilter = filter == null ? null : filter.toString();
        OsgiListenerUtils.addSingleServiceListener(context, listener, toStringFilter);
    }

    public static void addSingleServiceListener(BundleContext context, ServiceListener listener, String filter) {
        ServiceReference[] serviceReferenceArray;
        OsgiListenerUtils.registerListener(context, listener, filter);
        ServiceReference ref = OsgiServiceReferenceUtils.getServiceReference(context, filter);
        if (ref == null) {
            serviceReferenceArray = null;
        } else {
            ServiceReference[] serviceReferenceArray2 = new ServiceReference[1];
            serviceReferenceArray = serviceReferenceArray2;
            serviceReferenceArray2[0] = ref;
        }
        ServiceReference[] refs = serviceReferenceArray;
        OsgiListenerUtils.dispatchServiceRegistrationEvents(refs, listener);
    }

    public static boolean removeServiceListener(BundleContext context, ServiceListener listener) {
        if (context == null || listener == null) {
            return false;
        }
        try {
            context.removeServiceListener(listener);
            return true;
        }
        catch (IllegalStateException illegalStateException) {
            return false;
        }
    }
}

