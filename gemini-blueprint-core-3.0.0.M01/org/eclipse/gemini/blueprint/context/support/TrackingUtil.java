/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.context.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

abstract class TrackingUtil {
    TrackingUtil() {
    }

    static Object getService(Class<?>[] classes, String filter, ClassLoader classLoader, BundleContext context, Object fallbackObject) {
        String flt = OsgiFilterUtils.unifyFilter(classes, filter);
        return Proxy.newProxyInstance(classLoader, classes, (InvocationHandler)new OsgiServiceHandler(fallbackObject, context, ClassUtils.getParticularClass(classes).getName(), flt));
    }

    private static class OsgiServiceHandler
    implements InvocationHandler {
        private final Object fallbackObject;
        private final BundleContext context;
        private final String filterClassName;
        private final String filter;
        private final boolean securityOn;
        private volatile boolean bundleContextInvalidated = false;

        public OsgiServiceHandler(Object fallbackObject, BundleContext bundleContext, String filterClass, String filter) {
            this.fallbackObject = fallbackObject;
            this.context = bundleContext;
            this.filterClassName = filterClass;
            this.filter = filter;
            this.securityOn = System.getSecurityManager() != null;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("equals")) {
                return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
            }
            if (method.getName().equals("hashCode")) {
                return new Integer(System.identityHashCode(proxy));
            }
            Object target = null;
            if (!this.bundleContextInvalidated) {
                try {
                    target = this.securityOn ? AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            return this.getTarget(context, filter);
                        }
                    }) : this.getTarget(this.context, this.filter);
                }
                catch (IllegalStateException ise) {
                    this.bundleContextInvalidated = true;
                }
            }
            if (target == null) {
                target = this.fallbackObject;
            }
            try {
                Object result = method.invoke(target, args);
                return result;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

        private Object getTarget(BundleContext context, String filter) {
            ServiceReference ref = OsgiServiceReferenceUtils.getServiceReference(context, this.filterClassName, filter);
            return ref != null ? context.getService(ref) : null;
        }
    }
}

