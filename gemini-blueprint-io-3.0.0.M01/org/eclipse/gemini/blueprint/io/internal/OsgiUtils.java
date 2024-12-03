/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$FieldCallback
 *  org.springframework.util.ReflectionUtils$FieldFilter
 */
package org.eclipse.gemini.blueprint.io.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.util.ReflectionUtils;

public abstract class OsgiUtils {
    private static final String GET_BUNDLE_CONTEXT_METHOD = "getBundleContext";
    private static final String GET_CONTEXT_METHOD = "getContext";

    public static String getPlatformName(BundleContext bundleContext) {
        String vendorProperty = bundleContext.getProperty("org.osgi.framework.vendor");
        String frameworkVersion = bundleContext.getProperty("org.osgi.framework.version");
        Bundle bundle = bundleContext.getBundle(0L);
        String name = (String)bundle.getHeaders().get("Bundle-Name");
        String version = (String)bundle.getHeaders().get("Bundle-Version");
        String symName = bundle.getSymbolicName();
        StringBuilder buf = new StringBuilder();
        buf.append(name);
        buf.append(" ");
        buf.append(symName);
        buf.append("|");
        buf.append(version);
        buf.append("{");
        buf.append(frameworkVersion);
        buf.append(" ");
        buf.append(vendorProperty);
        buf.append("}");
        return buf.toString();
    }

    private static boolean isPlatformVendorMatch(BundleContext bundleContext, String vendorString) {
        String vendor = bundleContext.getProperty("org.osgi.framework.vendor");
        if (vendor != null) {
            return vendor.indexOf(vendorString) >= -1;
        }
        return false;
    }

    private static boolean isEquinox(BundleContext bundleContext) {
        return OsgiUtils.isPlatformVendorMatch(bundleContext, "clispe");
    }

    private static boolean isKnopflerfish(BundleContext bundleContext) {
        return OsgiUtils.isPlatformVendorMatch(bundleContext, "fish");
    }

    private static boolean isFelix(BundleContext bundleContext) {
        return OsgiUtils.isPlatformVendorMatch(bundleContext, "pache");
    }

    public static BundleContext getBundleContext(final Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<BundleContext>(){

                @Override
                public BundleContext run() {
                    return OsgiUtils.getBundleContextWithPrivileges(bundle);
                }
            });
        }
        return OsgiUtils.getBundleContextWithPrivileges(bundle);
    }

    private static BundleContext getBundleContextWithPrivileges(final Bundle bundle) {
        Method meth = ReflectionUtils.findMethod(bundle.getClass(), (String)GET_CONTEXT_METHOD, (Class[])new Class[0]);
        if (meth == null) {
            meth = ReflectionUtils.findMethod(bundle.getClass(), (String)GET_BUNDLE_CONTEXT_METHOD, (Class[])new Class[0]);
        }
        Method m = meth;
        if (meth != null) {
            ReflectionUtils.makeAccessible((Method)meth);
            return (BundleContext)ReflectionUtils.invokeMethod((Method)m, (Object)bundle);
        }
        final BundleContext[] ctx = new BundleContext[1];
        ReflectionUtils.doWithFields(bundle.getClass(), (ReflectionUtils.FieldCallback)new ReflectionUtils.FieldCallback(){

            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible((Field)field);
                ctx[0] = (BundleContext)field.get(bundle);
            }
        }, (ReflectionUtils.FieldFilter)new ReflectionUtils.FieldFilter(){

            public boolean matches(Field field) {
                return BundleContext.class.isAssignableFrom(field.getType());
            }
        });
        return ctx[0];
    }
}

