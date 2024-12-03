/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.util.tracker;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.AbstractTracked;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@ConsumerType
public class ServiceTracker<S, T>
implements ServiceTrackerCustomizer<S, T> {
    static final boolean DEBUG = false;
    protected final BundleContext context;
    protected final Filter filter;
    final ServiceTrackerCustomizer<S, T> customizer;
    final String listenerFilter;
    private final String trackClass;
    private final ServiceReference<S> trackReference;
    private volatile Tracked tracked;
    private volatile ServiceReference<S> cachedReference;
    private volatile T cachedService;

    private Tracked tracked() {
        return this.tracked;
    }

    public ServiceTracker(BundleContext context, ServiceReference<S> reference, ServiceTrackerCustomizer<S, T> customizer) {
        this.context = context;
        this.trackReference = reference;
        this.trackClass = null;
        this.customizer = customizer == null ? this : customizer;
        this.listenerFilter = "(service.id=" + reference.getProperty("service.id").toString() + ")";
        try {
            this.filter = context.createFilter(this.listenerFilter);
        }
        catch (InvalidSyntaxException e) {
            IllegalArgumentException iae = new IllegalArgumentException("unexpected InvalidSyntaxException: " + e.getMessage());
            iae.initCause(e);
            throw iae;
        }
    }

    public ServiceTracker(BundleContext context, String clazz, ServiceTrackerCustomizer<S, T> customizer) {
        this.context = context;
        this.trackReference = null;
        this.trackClass = clazz;
        this.customizer = customizer == null ? this : customizer;
        this.listenerFilter = "(objectClass=" + clazz.toString() + ")";
        try {
            this.filter = context.createFilter(this.listenerFilter);
        }
        catch (InvalidSyntaxException e) {
            IllegalArgumentException iae = new IllegalArgumentException("unexpected InvalidSyntaxException: " + e.getMessage());
            iae.initCause(e);
            throw iae;
        }
    }

    public ServiceTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer<S, T> customizer) {
        this.context = context;
        this.trackReference = null;
        this.trackClass = null;
        this.listenerFilter = filter.toString();
        this.filter = filter;
        ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer = this.customizer = customizer == null ? this : customizer;
        if (context == null || filter == null) {
            throw new NullPointerException();
        }
    }

    public ServiceTracker(BundleContext context, Class<S> clazz, ServiceTrackerCustomizer<S, T> customizer) {
        this(context, clazz.getName(), customizer);
    }

    public void open() {
        this.open(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void open(boolean trackAllServices) {
        Tracked t;
        ServiceTracker serviceTracker = this;
        synchronized (serviceTracker) {
            if (this.tracked != null) {
                return;
            }
            Tracked tracked = t = trackAllServices ? new AllTracked() : new Tracked();
            synchronized (tracked) {
                try {
                    this.context.addServiceListener(t, this.listenerFilter);
                    ServiceReference<S>[] references = null;
                    if (this.trackClass != null) {
                        references = this.getInitialReferences(trackAllServices, this.trackClass, null);
                    } else if (this.trackReference != null) {
                        if (this.trackReference.getBundle() != null) {
                            ServiceReference[] single = new ServiceReference[]{this.trackReference};
                            references = single;
                        }
                    } else {
                        references = this.getInitialReferences(trackAllServices, null, this.listenerFilter);
                    }
                    t.setInitial(references);
                }
                catch (InvalidSyntaxException e) {
                    throw new RuntimeException("unexpected InvalidSyntaxException: " + e.getMessage(), e);
                }
            }
            this.tracked = t;
        }
        t.trackInitial();
    }

    private ServiceReference<S>[] getInitialReferences(boolean trackAllServices, String className, String filterString) throws InvalidSyntaxException {
        ServiceReference<?>[] result = trackAllServices ? this.context.getAllServiceReferences(className, filterString) : this.context.getServiceReferences(className, filterString);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        ServiceReference<S>[] references;
        Tracked outgoing;
        Object object = this;
        synchronized (object) {
            outgoing = this.tracked;
            if (outgoing == null) {
                return;
            }
            outgoing.close();
            references = this.getServiceReferences();
            this.tracked = null;
            try {
                this.context.removeServiceListener(outgoing);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        this.modified();
        object = outgoing;
        synchronized (object) {
            outgoing.notifyAll();
        }
        if (references != null) {
            for (int i = 0; i < references.length; ++i) {
                outgoing.untrack(references[i], null);
            }
        }
    }

    @Override
    public T addingService(ServiceReference<S> reference) {
        S result = this.context.getService(reference);
        return (T)result;
    }

    @Override
    public void modifiedService(ServiceReference<S> reference, T service) {
    }

    @Override
    public void removedService(ServiceReference<S> reference, T service) {
        this.context.ungetService(reference);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T waitForService(long timeout) throws InterruptedException {
        long endTime;
        if (timeout < 0L) {
            throw new IllegalArgumentException("timeout value is negative");
        }
        T object = this.getService();
        if (object != null) {
            return object;
        }
        long l = endTime = timeout == 0L ? 0L : TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) + timeout;
        do {
            Tracked t;
            if ((t = this.tracked()) == null) {
                return null;
            }
            Tracked tracked = t;
            synchronized (tracked) {
                if (t.size() == 0) {
                    t.wait(timeout);
                }
            }
            object = this.getService();
        } while ((endTime <= 0L || (timeout = endTime - TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) > 0L) && object == null);
        return object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServiceReference<S>[] getServiceReferences() {
        Tracked t = this.tracked();
        if (t == null) {
            return null;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            if (t.isEmpty()) {
                return null;
            }
            ServiceReference[] result = new ServiceReference[]{};
            return t.copyKeys(result);
        }
    }

    public ServiceReference<S> getServiceReference() {
        int length;
        ServiceReference<S> reference = this.cachedReference;
        if (reference != null) {
            return reference;
        }
        ServiceReference<S>[] references = this.getServiceReferences();
        int n = length = references == null ? 0 : references.length;
        if (length == 0) {
            return null;
        }
        int index = 0;
        if (length > 1) {
            int[] rankings = new int[length];
            int count = 0;
            int maxRanking = Integer.MIN_VALUE;
            for (int i = 0; i < length; ++i) {
                int ranking;
                Object property = references[i].getProperty("service.ranking");
                rankings[i] = ranking = property instanceof Integer ? (Integer)property : 0;
                if (ranking > maxRanking) {
                    index = i;
                    maxRanking = ranking;
                    count = 1;
                    continue;
                }
                if (ranking != maxRanking) continue;
                ++count;
            }
            if (count > 1) {
                long minId = Long.MAX_VALUE;
                for (int i = 0; i < length; ++i) {
                    long id;
                    if (rankings[i] != maxRanking || (id = ((Long)references[i].getProperty("service.id")).longValue()) >= minId) continue;
                    index = i;
                    minId = id;
                }
            }
        }
        this.cachedReference = references[index];
        return this.cachedReference;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getService(ServiceReference<S> reference) {
        Tracked t = this.tracked();
        if (t == null) {
            return null;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.getCustomizedObject(reference);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] getServices() {
        Tracked t = this.tracked();
        if (t == null) {
            return null;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            int length;
            ServiceReference<S>[] references = this.getServiceReferences();
            int n = length = references == null ? 0 : references.length;
            if (length == 0) {
                return null;
            }
            Object[] objects = new Object[length];
            for (int i = 0; i < length; ++i) {
                objects[i] = this.getService(references[i]);
            }
            return objects;
        }
    }

    public T getService() {
        T service = this.cachedService;
        if (service != null) {
            return service;
        }
        ServiceReference<S> reference = this.getServiceReference();
        if (reference == null) {
            return null;
        }
        this.cachedService = this.getService(reference);
        return this.cachedService;
    }

    public void remove(ServiceReference<S> reference) {
        Tracked t = this.tracked();
        if (t == null) {
            return;
        }
        t.untrack(reference, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int size() {
        Tracked t = this.tracked();
        if (t == null) {
            return 0;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getTrackingCount() {
        Tracked t = this.tracked();
        if (t == null) {
            return -1;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.getTrackingCount();
        }
    }

    void modified() {
        this.cachedReference = null;
        this.cachedService = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SortedMap<ServiceReference<S>, T> getTracked() {
        TreeMap map = new TreeMap(Collections.reverseOrder());
        Tracked t = this.tracked();
        if (t == null) {
            return map;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.copyEntries(map);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() {
        Tracked t = this.tracked();
        if (t == null) {
            return true;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            return t.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T[] getServices(T[] array) {
        Tracked t = this.tracked();
        if (t == null) {
            if (array.length > 0) {
                array[0] = null;
            }
            return array;
        }
        Tracked tracked = t;
        synchronized (tracked) {
            int length;
            ServiceReference<S>[] references = this.getServiceReferences();
            int n = length = references == null ? 0 : references.length;
            if (length == 0) {
                if (array.length > 0) {
                    array[0] = null;
                }
                return array;
            }
            if (length > array.length) {
                Object[] newInstance = (Object[])Array.newInstance(array.getClass().getComponentType(), length);
                array = newInstance;
            }
            for (int i = 0; i < length; ++i) {
                array[i] = this.getService(references[i]);
            }
            if (array.length > length) {
                array[length] = null;
            }
            return array;
        }
    }

    private class AllTracked
    extends Tracked
    implements AllServiceListener {
        AllTracked() {
        }
    }

    private class Tracked
    extends AbstractTracked<ServiceReference<S>, T, ServiceEvent>
    implements ServiceListener {
        Tracked() {
        }

        @Override
        public final void serviceChanged(ServiceEvent event) {
            if (this.closed) {
                return;
            }
            ServiceReference<?> reference = event.getServiceReference();
            switch (event.getType()) {
                case 1: 
                case 2: {
                    this.track(reference, event);
                    break;
                }
                case 4: 
                case 8: {
                    this.untrack(reference, event);
                }
            }
        }

        @Override
        final void modified() {
            super.modified();
            ServiceTracker.this.modified();
        }

        @Override
        final T customizerAdding(ServiceReference<S> item, ServiceEvent related) {
            return ServiceTracker.this.customizer.addingService(item);
        }

        @Override
        final void customizerModified(ServiceReference<S> item, ServiceEvent related, T object) {
            ServiceTracker.this.customizer.modifiedService(item, object);
        }

        @Override
        final void customizerRemoved(ServiceReference<S> item, ServiceEvent related, T object) {
            ServiceTracker.this.customizer.removedService(item, object);
        }
    }
}

