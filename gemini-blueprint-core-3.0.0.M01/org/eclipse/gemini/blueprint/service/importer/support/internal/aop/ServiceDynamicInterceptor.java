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
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.ServiceUnavailableException;
import org.eclipse.gemini.blueprint.service.importer.DefaultOsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.service.importer.ServiceProxyDestroyedException;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitEndedEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitStartingEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitTimedOutEvent;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ReferenceHolder;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceInvoker;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.SwappingServiceReferenceProxy;
import org.eclipse.gemini.blueprint.service.importer.support.internal.dependency.ImporterStateListener;
import org.eclipse.gemini.blueprint.service.importer.support.internal.exception.BlueprintExceptionFactory;
import org.eclipse.gemini.blueprint.service.importer.support.internal.support.DefaultRetryCallback;
import org.eclipse.gemini.blueprint.service.importer.support.internal.support.RetryCallback;
import org.eclipse.gemini.blueprint.service.importer.support.internal.support.RetryTemplate;
import org.eclipse.gemini.blueprint.service.importer.support.internal.util.OsgiServiceBindingUtils;
import org.eclipse.gemini.blueprint.util.OsgiListenerUtils;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ServiceDynamicInterceptor
extends ServiceInvoker
implements InitializingBean,
ApplicationEventPublisherAware {
    private static final int hashCode = ServiceDynamicInterceptor.class.hashCode() * 13;
    private static final Log PUBLIC_LOGGER = LogFactory.getLog((String)"org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean");
    private final BundleContext bundleContext;
    private final String filterClassName;
    private final Filter filter;
    private final ClassLoader classLoader;
    private final SwappingServiceReferenceProxy referenceDelegate;
    private final ServiceListener listener;
    private boolean mandatoryService = true;
    private boolean isDuringDestruction = false;
    private volatile boolean destroyed = false;
    private final Object lock = new Object();
    private volatile ReferenceHolder holder;
    private final RetryTemplate retryTemplate = new EventSenderRetryTemplate();
    private final RetryCallback<Object> retryCallback = new ServiceLookUpCallback();
    private Object eventSource;
    private String sourceName;
    private OsgiServiceLifecycleListener[] listeners = new OsgiServiceLifecycleListener[0];
    private Object proxy;
    private ApplicationEventPublisher applicationEventPublisher;
    private OsgiServiceDependency dependency;
    private List<ImporterStateListener> stateListeners = Collections.emptyList();
    private boolean useBlueprintExceptions = false;
    private boolean sticky = false;

    public ServiceDynamicInterceptor(BundleContext context, String filterClassName, Filter filter, ClassLoader classLoader) {
        this.bundleContext = context;
        this.filterClassName = filterClassName;
        this.filter = filter;
        this.classLoader = classLoader;
        this.referenceDelegate = new SwappingServiceReferenceProxy();
        this.listener = new Listener();
    }

    @Override
    public Object getTarget() {
        Object target = this.lookupService();
        if (target == null) {
            throw this.useBlueprintExceptions ? BlueprintExceptionFactory.createServiceUnavailableException(this.filter) : new ServiceUnavailableException(this.filter);
        }
        return target;
    }

    public ServiceReference getTargetReference() {
        ServiceReference reference = this.lookupServiceReference();
        if (reference == null) {
            throw this.useBlueprintExceptions ? BlueprintExceptionFactory.createServiceUnavailableException(this.filter) : new ServiceUnavailableException(this.filter);
        }
        return reference;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object lookupService() {
        Object object = this.lock;
        synchronized (object) {
            return this.retryTemplate.execute(this.retryCallback);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ServiceReference lookupServiceReference() {
        Object object = this.lock;
        synchronized (object) {
            return this.retryTemplate.execute(new ServiceReferenceLookUpCallback());
        }
    }

    private void publishEvent(ApplicationEvent event) {
        if (this.applicationEventPublisher != null) {
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)("Publishing event through publisher " + this.applicationEventPublisher));
            }
            try {
                this.applicationEventPublisher.publishEvent(event);
            }
            catch (IllegalStateException ise) {
                this.log.debug((Object)("Event " + event + " not published as the publisher is not initialized - usually this is caused by eager initialization of the importers by post processing"), (Throwable)ise);
            }
        } else if (this.log.isTraceEnabled()) {
            this.log.trace((Object)"No application event publisher set; no events will be published");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterPropertiesSet() {
        Assert.notNull((Object)this.proxy);
        Assert.notNull((Object)this.eventSource);
        boolean debug = this.log.isDebugEnabled();
        this.dependency = new DefaultOsgiServiceDependency(this.sourceName, this.filter, this.mandatoryService);
        if (debug) {
            this.log.debug((Object)("Adding OSGi mandatoryListeners for services matching [" + this.filter + "]"));
        }
        OsgiListenerUtils.addSingleServiceListener(this.bundleContext, this.listener, this.filter);
        Object object = this.lock;
        synchronized (object) {
            if (this.referenceDelegate.getTargetServiceReference() == null) {
                OsgiServiceBindingUtils.callListenersUnbind(null, null, this.listeners);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void destroy() {
        OsgiListenerUtils.removeServiceListener(this.bundleContext, this.listener);
        ServiceReference ref = null;
        Object object = this.lock;
        synchronized (object) {
            this.destroyed = true;
            this.isDuringDestruction = true;
            if (this.holder != null) {
                ref = this.holder.getReference();
                this.listener.serviceChanged(new ServiceEvent(4, ref));
            }
            this.isDuringDestruction = false;
            this.lock.notifyAll();
        }
        if (ref != null) {
            try {
                this.bundleContext.ungetService(ref);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
    }

    @Override
    public ServiceReference getServiceReference() {
        return this.referenceDelegate;
    }

    public void setRetryTimeout(long timeout) {
        this.retryTemplate.reset(timeout);
    }

    public RetryTemplate getRetryTemplate() {
        return this.retryTemplate;
    }

    public OsgiServiceLifecycleListener[] getListeners() {
        return this.listeners;
    }

    public void setListeners(OsgiServiceLifecycleListener[] listeners) {
        this.listeners = listeners;
    }

    public void setServiceImporter(Object importer) {
        this.eventSource = importer;
    }

    public void setServiceImporterName(String name) {
        this.sourceName = name;
    }

    public void setMandatoryService(boolean mandatoryService) {
        this.mandatoryService = mandatoryService;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setStateListeners(List stateListeners) {
        this.stateListeners = stateListeners;
    }

    public void setUseBlueprintExceptions(boolean useBlueprintExceptions) {
        this.useBlueprintExceptions = useBlueprintExceptions;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ServiceDynamicInterceptor) {
            ServiceDynamicInterceptor oth = (ServiceDynamicInterceptor)other;
            return this.mandatoryService == oth.mandatoryService && ObjectUtils.nullSafeEquals((Object)this.holder, (Object)oth.holder) && ObjectUtils.nullSafeEquals((Object)this.filter, (Object)oth.filter) && ObjectUtils.nullSafeEquals((Object)this.retryTemplate, (Object)oth.retryTemplate);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    private class Listener
    implements ServiceListener {
        private Listener() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void serviceChanged(ServiceEvent event) {
            block19: {
                ClassLoader finalTccl;
                block17: {
                    boolean hasSecurity = System.getSecurityManager() != null;
                    ClassLoader tccl = null;
                    if (hasSecurity) {
                        tccl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                            @Override
                            public ClassLoader run() {
                                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                                Thread.currentThread().setContextClassLoader(ServiceDynamicInterceptor.this.classLoader);
                                return cl;
                            }
                        });
                    } else {
                        tccl = Thread.currentThread().getContextClassLoader();
                        Thread.currentThread().setContextClassLoader(ServiceDynamicInterceptor.this.classLoader);
                    }
                    try {
                        ServiceReference ref = event.getServiceReference();
                        boolean debug = ServiceDynamicInterceptor.this.log.isDebugEnabled();
                        boolean publicDebug = PUBLIC_LOGGER.isDebugEnabled();
                        switch (event.getType()) {
                            case 1: 
                            case 2: {
                                boolean servicePresent;
                                boolean bl = servicePresent = ServiceDynamicInterceptor.this.holder != null;
                                if (!this.updateWrapperIfNecessary(ref)) break;
                                OsgiServiceBindingUtils.callListenersBind(ServiceDynamicInterceptor.this.proxy, ref, ServiceDynamicInterceptor.this.listeners);
                                if (servicePresent) break;
                                this.notifySatisfiedStateListeners();
                                break;
                            }
                            case 4: {
                                boolean serviceRemoved = false;
                                ReferenceHolder oldHolder = ServiceDynamicInterceptor.this.holder;
                                if (ServiceDynamicInterceptor.this.holder != null && ServiceDynamicInterceptor.this.holder.equals(ref)) {
                                    serviceRemoved = true;
                                    ServiceDynamicInterceptor.this.holder = null;
                                }
                                ServiceReference newReference = null;
                                boolean isDestroyed = ServiceDynamicInterceptor.this.destroyed;
                                if (!isDestroyed && (newReference = OsgiServiceReferenceUtils.getServiceReference(ServiceDynamicInterceptor.this.bundleContext, ServiceDynamicInterceptor.this.filterClassName, ServiceDynamicInterceptor.this.filter == null ? null : ServiceDynamicInterceptor.this.filter.toString())) != null) {
                                    this.serviceChanged(new ServiceEvent(2, newReference));
                                }
                                if (newReference != null || !serviceRemoved) break;
                                ServiceDynamicInterceptor.this.holder = oldHolder;
                                OsgiServiceBindingUtils.callListenersUnbind(ServiceDynamicInterceptor.this.proxy, ref, ServiceDynamicInterceptor.this.listeners);
                                ServiceDynamicInterceptor.this.holder = null;
                                if (debug || publicDebug) {
                                    String message = "Service reference [" + ref + "] was unregistered";
                                    message = serviceRemoved ? message + " and unbound from the service proxy" : message + " but did not affect the service proxy";
                                    if (debug) {
                                        ServiceDynamicInterceptor.this.log.debug((Object)message);
                                    }
                                    if (publicDebug) {
                                        PUBLIC_LOGGER.debug((Object)message);
                                    }
                                }
                                this.notifyUnsatisfiedStateListeners();
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("unsupported event type");
                            }
                        }
                        finalTccl = tccl;
                        if (!hasSecurity) break block17;
                    }
                    catch (Throwable e) {
                        ClassLoader finalTccl2;
                        block18: {
                            try {
                                ServiceDynamicInterceptor.this.log.fatal((Object)"Exception during service event handling", e);
                                finalTccl2 = tccl;
                                if (!hasSecurity) break block18;
                            }
                            catch (Throwable throwable) {
                                ClassLoader finalTccl3 = tccl;
                                if (hasSecurity) {
                                    AccessController.doPrivileged(new PrivilegedAction<Object>(finalTccl3){
                                        final /* synthetic */ ClassLoader val$finalTccl;
                                        {
                                            this.val$finalTccl = classLoader;
                                        }

                                        @Override
                                        public Object run() {
                                            Thread.currentThread().setContextClassLoader(this.val$finalTccl);
                                            return null;
                                        }
                                    });
                                } else {
                                    Thread.currentThread().setContextClassLoader(finalTccl3);
                                }
                                throw throwable;
                            }
                            AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */);
                        }
                        Thread.currentThread().setContextClassLoader(finalTccl2);
                    }
                    AccessController.doPrivileged(new /* invalid duplicate definition of identical inner class */);
                    break block19;
                }
                Thread.currentThread().setContextClassLoader(finalTccl);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void notifySatisfiedStateListeners() {
            List list = ServiceDynamicInterceptor.this.stateListeners;
            synchronized (list) {
                for (ImporterStateListener stateListener : ServiceDynamicInterceptor.this.stateListeners) {
                    stateListener.importerSatisfied(ServiceDynamicInterceptor.this.eventSource, ServiceDynamicInterceptor.this.dependency);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void notifyUnsatisfiedStateListeners() {
            List list = ServiceDynamicInterceptor.this.stateListeners;
            synchronized (list) {
                for (ImporterStateListener stateListener : ServiceDynamicInterceptor.this.stateListeners) {
                    stateListener.importerUnsatisfied(ServiceDynamicInterceptor.this.eventSource, ServiceDynamicInterceptor.this.dependency);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean updateWrapperIfNecessary(ServiceReference ref) {
            boolean updated = false;
            try {
                if (ServiceDynamicInterceptor.this.holder == null || !ServiceDynamicInterceptor.this.sticky && ServiceDynamicInterceptor.this.holder.isWorseThen(ref)) {
                    updated = true;
                    this.updateReferenceHolders(ref);
                }
                Object object = ServiceDynamicInterceptor.this.lock;
                synchronized (object) {
                    ServiceDynamicInterceptor.this.lock.notifyAll();
                }
                boolean bl = updated;
                return bl;
            }
            finally {
                boolean debug = ServiceDynamicInterceptor.this.log.isDebugEnabled();
                boolean publicDebug = PUBLIC_LOGGER.isDebugEnabled();
                if (debug || publicDebug) {
                    String message = "Service reference [" + ref + "]";
                    message = updated ? message + " bound to proxy" : message + " not bound to proxy";
                    if (debug) {
                        ServiceDynamicInterceptor.this.log.debug((Object)message);
                    }
                    if (publicDebug) {
                        PUBLIC_LOGGER.debug((Object)message);
                    }
                }
            }
        }

        private void updateReferenceHolders(ServiceReference ref) {
            ServiceDynamicInterceptor.this.holder = new ReferenceHolder(ref, ServiceDynamicInterceptor.this.bundleContext);
            ServiceDynamicInterceptor.this.referenceDelegate.swapDelegates(ref);
        }
    }

    private class ServiceReferenceLookUpCallback
    extends DefaultRetryCallback<ServiceReference> {
        private ServiceReferenceLookUpCallback() {
        }

        @Override
        public ServiceReference doWithRetry() {
            if (ServiceDynamicInterceptor.this.destroyed && !ServiceDynamicInterceptor.this.isDuringDestruction) {
                throw new ServiceProxyDestroyedException();
            }
            return ServiceDynamicInterceptor.this.holder != null ? ServiceDynamicInterceptor.this.holder.getReference() : null;
        }
    }

    private class ServiceLookUpCallback
    extends DefaultRetryCallback<Object> {
        private ServiceLookUpCallback() {
        }

        @Override
        public Object doWithRetry() {
            if (ServiceDynamicInterceptor.this.destroyed && !ServiceDynamicInterceptor.this.isDuringDestruction) {
                throw new ServiceProxyDestroyedException();
            }
            return ServiceDynamicInterceptor.this.holder != null ? ServiceDynamicInterceptor.this.holder.getService() : null;
        }
    }

    private class EventSenderRetryTemplate
    extends RetryTemplate {
        public EventSenderRetryTemplate(long waitTime) {
            super(waitTime, ServiceDynamicInterceptor.this.lock);
        }

        public EventSenderRetryTemplate() {
            super(ServiceDynamicInterceptor.this.lock);
        }

        @Override
        protected void callbackFailed(long stop) {
            ServiceDynamicInterceptor.this.publishEvent(new OsgiServiceDependencyWaitTimedOutEvent(ServiceDynamicInterceptor.this.eventSource, ServiceDynamicInterceptor.this.dependency, stop));
        }

        @Override
        protected void callbackSucceeded(long stop) {
            ServiceDynamicInterceptor.this.publishEvent(new OsgiServiceDependencyWaitEndedEvent(ServiceDynamicInterceptor.this.eventSource, ServiceDynamicInterceptor.this.dependency, stop));
        }

        @Override
        protected void onMissingTarget() {
            ServiceDynamicInterceptor.this.publishEvent(new OsgiServiceDependencyWaitStartingEvent(ServiceDynamicInterceptor.this.eventSource, ServiceDynamicInterceptor.this.dependency, this.getWaitTime()));
        }
    }
}

