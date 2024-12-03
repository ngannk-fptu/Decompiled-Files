/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.config.Scope
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.support.internal.scope;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

public class OsgiBundleScope
implements Scope,
DisposableBean {
    public static final String SCOPE_NAME = "bundle";
    private static final Log log = LogFactory.getLog(OsgiBundleScope.class);
    public static final ThreadLocal<Object> EXTERNAL_BUNDLE = new NamedThreadLocal("Current in-creation scoped bean");
    private final Map<String, Object> localBeans = new LinkedHashMap<String, Object>(4);
    private final Map<String, Runnable> destructionCallbacks = new LinkedHashMap<String, Runnable>(8);

    private boolean isExternalBundleCalling() {
        return EXTERNAL_BUNDLE.get() != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (this.isExternalBundleCalling()) {
            Object bean = objectFactory.getObject();
            return bean;
        }
        Map<String, Object> map = this.localBeans;
        synchronized (map) {
            Object bean = this.localBeans.get(name);
            if (bean == null) {
                bean = objectFactory.getObject();
                this.localBeans.put(name, bean);
            }
            return bean;
        }
    }

    public String getConversationId() {
        return null;
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        if (this.isExternalBundleCalling()) {
            EXTERNAL_BUNDLE.set(callback);
        } else {
            this.destructionCallbacks.put(name, callback);
        }
    }

    public Object remove(String name) {
        throw new UnsupportedOperationException();
    }

    public Object resolveContextualObject(String key) {
        return null;
    }

    public void destroy() {
        boolean debug = log.isDebugEnabled();
        for (Map.Entry<String, Runnable> entry : this.destructionCallbacks.entrySet()) {
            Runnable callback = entry.getValue();
            if (debug) {
                log.debug((Object)("destroying local bundle scoped bean [" + entry.getKey() + "]"));
            }
            callback.run();
        }
        this.destructionCallbacks.clear();
        this.localBeans.clear();
    }

    public static class BundleScopeServiceFactory
    implements ServiceFactory {
        private ServiceFactory decoratedServiceFactory;
        private final Map<Bundle, Runnable> callbacks = new ConcurrentHashMap<Bundle, Runnable>(4);

        public BundleScopeServiceFactory(ServiceFactory serviceFactory) {
            Assert.notNull((Object)serviceFactory);
            this.decoratedServiceFactory = serviceFactory;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object getService(Bundle bundle, ServiceRegistration registration) {
            try {
                Runnable callback;
                EXTERNAL_BUNDLE.set(Boolean.TRUE);
                Object obj = this.decoratedServiceFactory.getService(bundle, registration);
                Object passedObject = EXTERNAL_BUNDLE.get();
                if (passedObject != null && passedObject instanceof Runnable && (callback = (Runnable)EXTERNAL_BUNDLE.get()) != null) {
                    this.callbacks.put(bundle, callback);
                }
                Object object = obj;
                return object;
            }
            finally {
                EXTERNAL_BUNDLE.set(null);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            try {
                EXTERNAL_BUNDLE.set(Boolean.TRUE);
                this.decoratedServiceFactory.ungetService(bundle, registration, service);
                Runnable callback = this.callbacks.remove(bundle);
                if (callback != null) {
                    callback.run();
                }
            }
            finally {
                EXTERNAL_BUNDLE.set(null);
            }
        }
    }
}

