/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.osgi.framework.BundleContext
 *  org.springframework.aop.framework.ProxyFactory
 */
package org.eclipse.gemini.blueprint.service.util.internal.aop;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.eclipse.gemini.blueprint.util.DebugUtils;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.osgi.framework.BundleContext;
import org.springframework.aop.framework.ProxyFactory;

public abstract class ProxyUtils {
    public static Object createProxy(Class<?>[] classes, Object target, ClassLoader classLoader, BundleContext bundleContext, List advices) {
        return ProxyUtils.createProxy(classes, target, classLoader, bundleContext, advices != null ? advices.toArray(new Advice[advices.size()]) : new Advice[]{});
    }

    public static Object createProxy(Class<?>[] classes, Object target, final ClassLoader classLoader, BundleContext bundleContext, Advice[] advices) {
        final ProxyFactory factory = new ProxyFactory();
        ClassUtils.configureFactoryForClass(factory, classes);
        for (int i = 0; i < advices.length; ++i) {
            factory.addAdvice(advices[i]);
        }
        if (target != null) {
            factory.setTarget(target);
        }
        factory.setFrozen(true);
        factory.setOpaque(true);
        boolean isSecurityOn = System.getSecurityManager() != null;
        try {
            if (isSecurityOn) {
                return AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    @Override
                    public Object run() {
                        return factory.getProxy(classLoader);
                    }
                });
            }
            return factory.getProxy(classLoader);
        }
        catch (NoClassDefFoundError ncdfe) {
            DebugUtils.debugClassLoadingThrowable(ncdfe, bundleContext.getBundle(), classes);
            throw ncdfe;
        }
    }
}

