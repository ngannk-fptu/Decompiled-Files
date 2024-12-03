/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import java.util.ArrayList;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.importer.support.ImportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.importer.support.LocalBundleContextAdvice;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ImportedOsgiServiceProxyAdvice;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.InfrastructureOsgiProxyAdvice;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ProxyPlusCallback;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceInvoker;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.util.internal.aop.ProxyUtils;
import org.eclipse.gemini.blueprint.service.util.internal.aop.ServiceTCCLInterceptor;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;

abstract class AbstractServiceProxyCreator
implements ServiceProxyCreator {
    private static final Log log = LogFactory.getLog(AbstractServiceProxyCreator.class);
    private final Advice clientTCCLAdvice;
    private final Advice invokerBundleContextAdvice;
    protected final ClassLoader classLoader;
    protected final Class<?>[] classes;
    protected final BundleContext bundleContext;
    private final ImportContextClassLoaderEnum iccl;

    AbstractServiceProxyCreator(Class<?>[] classes, ClassLoader aopClassLoader, ClassLoader bundleClassLoader, BundleContext bundleContext, ImportContextClassLoaderEnum iccl) {
        Assert.notNull((Object)bundleContext);
        Assert.notNull((Object)aopClassLoader);
        this.classes = classes;
        this.bundleContext = bundleContext;
        this.classLoader = aopClassLoader;
        this.iccl = iccl;
        this.clientTCCLAdvice = ImportContextClassLoaderEnum.CLIENT.equals((Object)iccl) ? new ServiceTCCLInterceptor(bundleClassLoader) : null;
        this.invokerBundleContextAdvice = new LocalBundleContextAdvice(bundleContext);
    }

    @Override
    public ProxyPlusCallback createServiceProxy(ServiceReference reference) {
        ArrayList<Object> advices = new ArrayList<Object>(4);
        ImportedOsgiServiceProxyAdvice mixin = new ImportedOsgiServiceProxyAdvice(reference);
        advices.add(mixin);
        advices.add(this.invokerBundleContextAdvice);
        Advice tcclAdvice = this.determineTCCLAdvice(reference);
        if (tcclAdvice != null) {
            advices.add(tcclAdvice);
        }
        ServiceInvoker dispatcherInterceptor = this.createDispatcherInterceptor(reference);
        InfrastructureOsgiProxyAdvice infrastructureMixin = new InfrastructureOsgiProxyAdvice(dispatcherInterceptor);
        advices.add((Object)infrastructureMixin);
        advices.add(dispatcherInterceptor);
        return new ProxyPlusCallback(ProxyUtils.createProxy(this.getInterfaces(reference), null, this.classLoader, this.bundleContext, advices), dispatcherInterceptor);
    }

    private Advice determineTCCLAdvice(ServiceReference reference) {
        try {
            switch (this.iccl) {
                case CLIENT: {
                    Advice advice = this.clientTCCLAdvice;
                    return advice;
                }
                case SERVICE_PROVIDER: {
                    Advice advice = this.createServiceProviderTCCLAdvice(reference);
                    return advice;
                }
                case UNMANAGED: {
                    Advice advice = null;
                    return advice;
                }
            }
            Advice advice = null;
            return advice;
        }
        finally {
            if (log.isTraceEnabled()) {
                log.trace((Object)((Object)((Object)this.iccl) + " TCCL used for invoking " + OsgiStringUtils.nullSafeToString(reference)));
            }
        }
    }

    Class<?>[] getInterfaces(ServiceReference reference) {
        return this.classes;
    }

    abstract Advice createServiceProviderTCCLAdvice(ServiceReference var1);

    abstract ServiceInvoker createDispatcherInterceptor(ServiceReference var1);
}

