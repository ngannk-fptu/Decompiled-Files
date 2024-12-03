/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.ServiceUnavailableException;
import org.eclipse.gemini.blueprint.service.importer.DefaultOsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ProxyPlusCallback;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.CollectionProxy;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;
import org.eclipse.gemini.blueprint.service.importer.support.internal.dependency.ImporterStateListener;
import org.eclipse.gemini.blueprint.service.importer.support.internal.exception.BlueprintExceptionFactory;
import org.eclipse.gemini.blueprint.service.importer.support.internal.util.OsgiServiceBindingUtils;
import org.eclipse.gemini.blueprint.util.OsgiListenerUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class OsgiServiceCollection
implements Collection,
InitializingBean,
CollectionProxy,
DisposableBean {
    private static final Log log = LogFactory.getLog(OsgiServiceCollection.class);
    private static final Log PUBLIC_LOGGER = LogFactory.getLog((String)"org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean");
    protected final Map<Long, ProxyPlusCallback> servicesIdMap = new LinkedHashMap<Long, ProxyPlusCallback>(8);
    protected DynamicCollection<Object> services;
    private volatile boolean serviceRequiredAtStartup = true;
    private final Filter filter;
    private final BundleContext context;
    private final ClassLoader classLoader;
    private final ServiceProxyCreator proxyCreator;
    private OsgiServiceLifecycleListener[] listeners = new OsgiServiceLifecycleListener[0];
    private final ServiceListener listener;
    private List<ImporterStateListener> stateListeners = Collections.emptyList();
    private final Object lock = new Object();
    private OsgiServiceDependency dependency;
    private Object eventSource;
    private String sourceName;
    private final boolean useServiceReferences;
    private volatile boolean useBlueprintExceptions = false;

    public OsgiServiceCollection(Filter filter, BundleContext context, ClassLoader classLoader, ServiceProxyCreator proxyCreator, boolean useServiceReference) {
        Assert.notNull((Object)classLoader, (String)"ClassLoader is required");
        Assert.notNull((Object)context, (String)"context is required");
        this.filter = filter;
        this.context = context;
        this.classLoader = classLoader;
        this.proxyCreator = proxyCreator;
        this.useServiceReferences = useServiceReference;
        this.listener = new ServiceInstanceListener();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterPropertiesSet() {
        this.services = this.createInternalDynamicStorage();
        this.dependency = new DefaultOsgiServiceDependency(this.sourceName, this.filter, this.serviceRequiredAtStartup);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Adding osgi listener for services matching [" + this.filter + "]"));
        }
        OsgiListenerUtils.addServiceListener(this.context, this.listener, this.filter);
        Object object = this.lock;
        synchronized (object) {
            if (this.services.isEmpty()) {
                OsgiServiceBindingUtils.callListenersUnbind(null, null, this.listeners);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        OsgiListenerUtils.removeServiceListener(this.context, this.listener);
        DynamicCollection<Object> dynamicCollection = this.services;
        synchronized (dynamicCollection) {
            for (Object item : this.services) {
                ServiceReference ref;
                if (!this.useServiceReferences) {
                    ImportedOsgiServiceProxy serviceProxy = (ImportedOsgiServiceProxy)item;
                    ref = serviceProxy.getServiceReference().getTargetServiceReference();
                } else {
                    ref = (ServiceReference)item;
                }
                ProxyPlusCallback ppc = this.servicesIdMap.get((Long)ref.getProperty("service.id"));
                this.listener.serviceChanged(new ServiceEvent(4, ref));
                try {
                    ppc.destructionCallback.destroy();
                }
                catch (Exception ex) {
                    log.error((Object)("Exception occurred while destroying proxy " + ppc.proxy), (Throwable)ex);
                }
            }
            this.services.clear();
            this.servicesIdMap.clear();
        }
    }

    protected void mandatoryServiceCheck() {
        if (this.serviceRequiredAtStartup && this.services.isEmpty()) {
            throw this.useBlueprintExceptions ? BlueprintExceptionFactory.createServiceUnavailableException(this.filter) : new ServiceUnavailableException(this.filter);
        }
    }

    @Override
    public boolean isSatisfied() {
        if (this.serviceRequiredAtStartup) {
            return !this.services.isEmpty();
        }
        return true;
    }

    protected DynamicCollection<Object> createInternalDynamicStorage() {
        return new DynamicCollection<Object>();
    }

    private void invalidateProxy(ProxyPlusCallback ppc) {
    }

    public void setServiceImporter(Object importer) {
        this.eventSource = importer;
    }

    public void setServiceImporterName(String name) {
        this.sourceName = name;
    }

    @Override
    public Iterator<Object> iterator() {
        return new OsgiServiceIterator();
    }

    @Override
    public int size() {
        return this.services.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        DynamicCollection<Object> dynamicCollection = this.services;
        synchronized (dynamicCollection) {
            return this.services.toString();
        }
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        return this.services.contains(o);
    }

    public boolean containsAll(Collection c) {
        return this.services.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public Object[] toArray() {
        return this.services.toArray();
    }

    public Object[] toArray(Object[] array) {
        return this.services.toArray(array);
    }

    public void setListeners(OsgiServiceLifecycleListener[] listeners) {
        Assert.notNull((Object)listeners);
        this.listeners = listeners;
    }

    public void setRequiredAtStartup(boolean serviceRequiredAtStartup) {
        this.serviceRequiredAtStartup = serviceRequiredAtStartup;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setStateListeners(List<ImporterStateListener> stateListeners) {
        Object object = this.lock;
        synchronized (object) {
            this.stateListeners = stateListeners;
        }
    }

    public void setUseBlueprintExceptions(boolean useBlueprintExceptions) {
        this.useBlueprintExceptions = useBlueprintExceptions;
    }

    protected class OsgiServiceIterator
    implements Iterator<Object> {
        private final Iterator<Object> iter;

        protected OsgiServiceIterator() {
            this.iter = OsgiServiceCollection.this.services.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Object next() {
            return this.iter.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class ServiceInstanceListener
    extends BaseListener {
        private ServiceInstanceListener() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected EventResult addService(Long serviceId, ServiceReference ref) {
            DynamicCollection<Object> dynamicCollection = OsgiServiceCollection.this.services;
            synchronized (dynamicCollection) {
                if (!OsgiServiceCollection.this.servicesIdMap.containsKey(serviceId)) {
                    ImportedOsgiServiceProxy value;
                    ProxyPlusCallback ppc = OsgiServiceCollection.this.proxyCreator.createServiceProxy(ref);
                    ImportedOsgiServiceProxy proxy = ppc.proxy;
                    EventResult state = new EventResult();
                    state.proxy = proxy;
                    ImportedOsgiServiceProxy importedOsgiServiceProxy = value = OsgiServiceCollection.this.useServiceReferences ? proxy.getServiceReference().getTargetServiceReference() : proxy;
                    if (OsgiServiceCollection.this.services.add(value)) {
                        state.collectionModified = true;
                        state.shouldInformStateListeners = OsgiServiceCollection.this.services.size() == 1;
                        OsgiServiceCollection.this.servicesIdMap.put(serviceId, ppc);
                    }
                    return state;
                }
            }
            return EventResult.DEFAULT;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected EventResult canRemoveService(Long serviceId, ServiceReference ref) {
            DynamicCollection<Object> dynamicCollection = OsgiServiceCollection.this.services;
            synchronized (dynamicCollection) {
                ProxyPlusCallback ppc = OsgiServiceCollection.this.servicesIdMap.get(serviceId);
                if (ppc != null) {
                    EventResult state = new EventResult();
                    state.proxy = ppc.proxy;
                    ImportedOsgiServiceProxy value = OsgiServiceCollection.this.useServiceReferences ? ppc.proxy.getServiceReference().getTargetServiceReference() : ppc.proxy;
                    state.collectionModified = OsgiServiceCollection.this.services.contains(value);
                    return state;
                }
            }
            return EventResult.DEFAULT;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected EventResult removeService(Long serviceId, ServiceReference ref) {
            DynamicCollection<Object> dynamicCollection = OsgiServiceCollection.this.services;
            synchronized (dynamicCollection) {
                ProxyPlusCallback ppc = OsgiServiceCollection.this.servicesIdMap.remove(serviceId);
                if (ppc != null) {
                    EventResult state = new EventResult();
                    state.proxy = ppc.proxy;
                    ImportedOsgiServiceProxy value = OsgiServiceCollection.this.useServiceReferences ? ppc.proxy.getServiceReference().getTargetServiceReference() : ppc.proxy;
                    state.collectionModified = OsgiServiceCollection.this.services.remove(value);
                    OsgiServiceCollection.this.invalidateProxy(ppc);
                    state.shouldInformStateListeners = OsgiServiceCollection.this.services.isEmpty();
                    return state;
                }
            }
            return EventResult.DEFAULT;
        }
    }

    private abstract class BaseListener
    implements ServiceListener {
        private BaseListener() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public void serviceChanged(ServiceEvent event) {
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(OsgiServiceCollection.this.classLoader);
                ServiceReference ref = event.getServiceReference();
                Long serviceId = (Long)ref.getProperty("service.id");
                EventResult state = null;
                switch (event.getType()) {
                    case 1: 
                    case 2: {
                        state = this.addService(serviceId, ref);
                        if (!state.collectionModified) return;
                        OsgiServiceBindingUtils.callListenersBind(state.proxy, ref, OsgiServiceCollection.this.listeners);
                        if (!OsgiServiceCollection.this.serviceRequiredAtStartup || !state.shouldInformStateListeners) return;
                        this.notifySatisfiedStateListeners();
                        return;
                    }
                    case 4: {
                        state = this.canRemoveService(serviceId, ref);
                        if (!state.collectionModified) return;
                        OsgiServiceBindingUtils.callListenersUnbind(state.proxy, ref, OsgiServiceCollection.this.listeners);
                        state = this.removeService(serviceId, ref);
                        if (!OsgiServiceCollection.this.serviceRequiredAtStartup || !state.shouldInformStateListeners) return;
                        this.notifyUnsatisfiedStateListeners();
                        return;
                    }
                    default: {
                        throw new IllegalArgumentException("unsupported event type:" + event);
                    }
                }
            }
            catch (Throwable re) {
                if (!log.isWarnEnabled()) return;
                log.warn((Object)"serviceChanged() processing failed", re);
                return;
            }
            finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void notifySatisfiedStateListeners() {
            List list = OsgiServiceCollection.this.stateListeners;
            synchronized (list) {
                for (ImporterStateListener stateListener : OsgiServiceCollection.this.stateListeners) {
                    stateListener.importerSatisfied(OsgiServiceCollection.this.eventSource, OsgiServiceCollection.this.dependency);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void notifyUnsatisfiedStateListeners() {
            List list = OsgiServiceCollection.this.stateListeners;
            synchronized (list) {
                for (ImporterStateListener stateListener : OsgiServiceCollection.this.stateListeners) {
                    stateListener.importerUnsatisfied(OsgiServiceCollection.this.eventSource, OsgiServiceCollection.this.dependency);
                }
            }
        }

        protected abstract EventResult addService(Long var1, ServiceReference var2);

        protected abstract EventResult canRemoveService(Long var1, ServiceReference var2);

        protected abstract EventResult removeService(Long var1, ServiceReference var2);
    }

    private static class EventResult {
        static final EventResult DEFAULT = new EventResult();
        Object proxy = null;
        boolean shouldInformStateListeners = false;
        boolean collectionModified = false;

        private EventResult() {
        }
    }
}

