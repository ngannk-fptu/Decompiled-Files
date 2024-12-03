/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.ServiceReference
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.service.importer.support.AbstractServiceImporterProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.AbstractServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.eclipse.gemini.blueprint.service.importer.support.DisposableBeanRunnableAdapter;
import org.eclipse.gemini.blueprint.service.importer.support.ImportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ProxyPlusCallback;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceDynamicInterceptor;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceInvoker;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProviderTCCLInterceptor;
import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterController;
import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterInternalActions;
import org.eclipse.gemini.blueprint.service.importer.support.internal.dependency.ImporterStateListener;
import org.eclipse.gemini.blueprint.service.importer.support.internal.support.RetryTemplate;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.ObjectUtils;

public final class OsgiServiceProxyFactoryBean
extends AbstractServiceImporterProxyFactoryBean
implements ApplicationEventPublisherAware {
    private static final Log log = LogFactory.getLog(OsgiServiceProxyFactoryBean.class);
    private long retryTimeout;
    private RetryTemplate retryTemplate;
    private ImportedOsgiServiceProxy proxy;
    private Runnable destructionCallback;
    private Runnable initializationCallback;
    private ApplicationEventPublisher applicationEventPublisher;
    private final List<ImporterStateListener> stateListeners = Collections.synchronizedList(new ArrayList(4));
    private final ImporterInternalActions controller;
    private volatile boolean mandatory = true;
    private volatile boolean sticky = true;
    private final Object monitor = new Object();

    public OsgiServiceProxyFactoryBean() {
        this.controller = new ImporterController(new Executor());
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.mandatory = Availability.MANDATORY.equals((Object)this.getAvailability());
    }

    @Override
    public Object getObject() {
        return super.getObject();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    Object createProxy(boolean lazyProxy) {
        boolean serviceTccl;
        ServiceProviderTCCLInterceptor tcclAdvice;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Creating a single service proxy ...");
        }
        ServiceProviderTCCLInterceptor serviceProviderTCCLInterceptor = tcclAdvice = (serviceTccl = ImportContextClassLoaderEnum.SERVICE_PROVIDER.equals((Object)this.getImportContextClassLoader())) ? new ServiceProviderTCCLInterceptor() : null;
        ServiceProviderTCCLInterceptor.ServiceProviderTCCLListener tcclListener = serviceTccl ? new ServiceProviderTCCLInterceptor.ServiceProviderTCCLListener(tcclAdvice) : null;
        Class<?> filterClass = ClassUtils.getParticularClass(this.getInterfaces());
        String filterClassName = filterClass != null ? filterClass.getName() : null;
        final ServiceDynamicInterceptor lookupAdvice = new ServiceDynamicInterceptor(this.getBundleContext(), filterClassName, this.getUnifiedFilter(), this.getAopClassLoader());
        lookupAdvice.setMandatoryService(Availability.MANDATORY.equals((Object)this.getAvailability()));
        lookupAdvice.setUseBlueprintExceptions(this.isUseBlueprintExceptions());
        lookupAdvice.setSticky(this.sticky);
        OsgiServiceLifecycleListener[] listeners = serviceTccl ? (OsgiServiceLifecycleListener[])ObjectUtils.addObjectToArray((Object[])this.getListeners(), (Object)tcclListener) : this.getListeners();
        lookupAdvice.setListeners(listeners);
        Object object = this.monitor;
        synchronized (object) {
            lookupAdvice.setRetryTimeout(this.retryTimeout);
            this.retryTemplate = lookupAdvice.getRetryTemplate();
        }
        lookupAdvice.setApplicationEventPublisher(this.applicationEventPublisher);
        lookupAdvice.setStateListeners(this.stateListeners);
        lookupAdvice.setServiceImporter(this);
        lookupAdvice.setServiceImporterName(this.getBeanName());
        AbstractServiceProxyCreator creator = new AbstractServiceProxyCreator(this.getInterfaces(), this.getAopClassLoader(), this.getBeanClassLoader(), this.getBundleContext(), this.getImportContextClassLoader()){

            @Override
            ServiceInvoker createDispatcherInterceptor(ServiceReference reference) {
                return lookupAdvice;
            }

            @Override
            Advice createServiceProviderTCCLAdvice(ServiceReference reference) {
                return tcclAdvice;
            }
        };
        ProxyPlusCallback proxyPlusCallback = creator.createServiceProxy(lookupAdvice.getServiceReference());
        Object object2 = this.monitor;
        synchronized (object2) {
            this.proxy = proxyPlusCallback.proxy;
            this.destructionCallback = new DisposableBeanRunnableAdapter(proxyPlusCallback.destructionCallback);
        }
        lookupAdvice.setProxy(this.proxy);
        if (!lazyProxy) {
            lookupAdvice.afterPropertiesSet();
        } else {
            this.initializationCallback = new Runnable(){

                @Override
                public void run() {
                    lookupAdvice.afterPropertiesSet();
                }
            };
        }
        return this.proxy;
    }

    @Override
    Runnable getProxyInitializer() {
        return this.initializationCallback;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    Runnable getProxyDestructionCallback() {
        Object object = this.monitor;
        synchronized (object) {
            return this.destructionCallback;
        }
    }

    private OsgiServiceLifecycleListener[] addListener(OsgiServiceLifecycleListener[] listeners, OsgiServiceLifecycleListener listener) {
        int size = listeners == null ? 1 : listeners.length + 1;
        OsgiServiceLifecycleListener[] list = new OsgiServiceLifecycleListener[size];
        list[0] = listener;
        if (listeners != null) {
            System.arraycopy(listeners, 0, list, 1, listeners.length);
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setTimeout(long timeoutInMillis) {
        RetryTemplate rt;
        Object object = this.monitor;
        synchronized (object) {
            this.retryTimeout = timeoutInMillis;
            rt = this.retryTemplate;
        }
        if (rt != null) {
            rt.reset(timeoutInMillis);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getTimeout() {
        Object object = this.monitor;
        synchronized (object) {
            return this.retryTimeout;
        }
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        Object object = this.monitor;
        synchronized (object) {
            this.applicationEventPublisher = applicationEventPublisher;
        }
    }

    private class Executor
    implements ImporterInternalActions {
        private Executor() {
        }

        @Override
        public void addStateListener(ImporterStateListener stateListener) {
            OsgiServiceProxyFactoryBean.this.stateListeners.add(stateListener);
        }

        @Override
        public void removeStateListener(ImporterStateListener stateListener) {
            OsgiServiceProxyFactoryBean.this.stateListeners.remove(stateListener);
        }

        @Override
        public boolean isSatisfied() {
            return !OsgiServiceProxyFactoryBean.this.mandatory || OsgiServiceProxyFactoryBean.this.proxy == null || OsgiServiceProxyFactoryBean.this.proxy.getServiceReference().getBundle() != null;
        }
    }
}

