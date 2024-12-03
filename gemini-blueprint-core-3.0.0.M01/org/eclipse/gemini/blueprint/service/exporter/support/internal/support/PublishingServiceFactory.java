/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.support;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.LazyTargetResolver;
import org.eclipse.gemini.blueprint.service.util.internal.aop.ProxyUtils;
import org.eclipse.gemini.blueprint.service.util.internal.aop.ServiceTCCLInterceptor;
import org.eclipse.gemini.blueprint.util.DebugUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class PublishingServiceFactory
implements ServiceFactory {
    private static final Log log = LogFactory.getLog(PublishingServiceFactory.class);
    private final Map<Object, WeakReference<Object>> proxyCache;
    private final LazyTargetResolver targetResolver;
    private final Class<?>[] classes;
    private final boolean createTCCLProxy;
    private final ClassLoader classLoader;
    private final ClassLoader aopClassLoader;
    private final BundleContext bundleContext;
    private final Object lock = new Object();

    public PublishingServiceFactory(LazyTargetResolver targetResolver, Class<?>[] classes, boolean createTCCLProxy, ClassLoader classLoader, ClassLoader aopClassLoader, BundleContext bundleContext) {
        this.targetResolver = targetResolver;
        this.classes = classes;
        this.createTCCLProxy = createTCCLProxy;
        this.classLoader = classLoader;
        this.aopClassLoader = aopClassLoader;
        this.bundleContext = bundleContext;
        this.proxyCache = createTCCLProxy ? new WeakHashMap(4) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getService(Bundle bundle, ServiceRegistration serviceRegistration) {
        if (log.isTraceEnabled()) {
            log.trace((Object)("Get service called by bundle " + OsgiStringUtils.nullSafeName(bundle) + " on registration " + OsgiStringUtils.nullSafeToString(serviceRegistration.getReference())));
        }
        this.targetResolver.activate();
        Object bn = this.targetResolver.getBean();
        if (bn instanceof ServiceFactory) {
            bn = ((ServiceFactory)bn).getService(bundle, serviceRegistration);
        }
        if (this.createTCCLProxy) {
            Map<Object, WeakReference<Object>> map = this.proxyCache;
            synchronized (map) {
                WeakReference<Object> value = this.proxyCache.get(bn);
                Object proxy = null;
                if (value != null) {
                    proxy = value.get();
                }
                if (proxy == null) {
                    proxy = this.createCLLProxy(bn);
                    this.proxyCache.put(bn, new WeakReference<Object>(proxy));
                }
                bn = proxy;
            }
        }
        return bn;
    }

    private Object createCLLProxy(Object target) {
        try {
            return ProxyUtils.createProxy(this.classes, target, this.aopClassLoader, this.bundleContext, new Advice[]{new ServiceTCCLInterceptor(this.classLoader)});
        }
        catch (Throwable th) {
            log.error((Object)"Cannot create TCCL managed proxy; falling back to the naked object", th);
            if (th instanceof NoClassDefFoundError) {
                NoClassDefFoundError ncdfe = (NoClassDefFoundError)th;
                if (log.isWarnEnabled()) {
                    DebugUtils.debugClassLoadingThrowable(ncdfe, this.bundleContext.getBundle(), this.classes);
                }
                throw ncdfe;
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ungetService(Bundle bundle, ServiceRegistration serviceRegistration, Object service) {
        Class<?> type;
        if (log.isTraceEnabled()) {
            log.trace((Object)("Unget service called by bundle " + OsgiStringUtils.nullSafeName(bundle) + " on registration " + OsgiStringUtils.nullSafeToString(serviceRegistration.getReference())));
        }
        if (ServiceFactory.class.isAssignableFrom(type = this.targetResolver.getType())) {
            ServiceFactory sf = (ServiceFactory)this.targetResolver.getBean();
            sf.ungetService(bundle, serviceRegistration, service);
        }
        if (this.createTCCLProxy) {
            Map<Object, WeakReference<Object>> map = this.proxyCache;
            synchronized (map) {
                this.proxyCache.size();
            }
        }
    }
}

