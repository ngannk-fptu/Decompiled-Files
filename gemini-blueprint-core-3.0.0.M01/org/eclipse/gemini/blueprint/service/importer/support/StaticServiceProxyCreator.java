/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import java.util.LinkedHashSet;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ClassLoaderFactory;
import org.eclipse.gemini.blueprint.service.importer.support.AbstractServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.ImportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceInvoker;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceStaticInterceptor;
import org.eclipse.gemini.blueprint.service.util.internal.aop.ServiceTCCLInterceptor;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.util.ObjectUtils;

class StaticServiceProxyCreator
extends AbstractServiceProxyCreator {
    private static final Log log = LogFactory.getLog(StaticServiceProxyCreator.class);
    private final boolean greedyProxying;
    private final boolean interfacesOnlyProxying;
    private final boolean useBlueprintExceptions;

    StaticServiceProxyCreator(Class<?>[] classes, ClassLoader aopClassLoader, ClassLoader bundleClassLoader, BundleContext bundleContext, ImportContextClassLoaderEnum iccl, boolean greedyProxying, boolean useBlueprintExceptions) {
        super(classes, aopClassLoader, bundleClassLoader, bundleContext, iccl);
        String msg;
        this.greedyProxying = greedyProxying;
        this.useBlueprintExceptions = useBlueprintExceptions;
        boolean onlyInterfaces = true;
        for (int i = 0; i < classes.length; ++i) {
            if (classes[i].isInterface()) continue;
            onlyInterfaces = false;
        }
        this.interfacesOnlyProxying = onlyInterfaces;
        String string = msg = this.interfacesOnlyProxying ? "NOT" : "";
        if (log.isDebugEnabled()) {
            log.debug((Object)("Greedy proxying will " + msg + " consider exposed classes"));
        }
    }

    @Override
    ServiceInvoker createDispatcherInterceptor(ServiceReference reference) {
        ServiceStaticInterceptor interceptor = new ServiceStaticInterceptor(this.bundleContext, reference);
        interceptor.setUseBlueprintExceptions(this.useBlueprintExceptions);
        return interceptor;
    }

    @Override
    Advice createServiceProviderTCCLAdvice(ServiceReference reference) {
        Bundle bundle = reference.getBundle();
        if (bundle == null) {
            return null;
        }
        return new ServiceTCCLInterceptor(ClassLoaderFactory.getBundleClassLoaderFor(bundle));
    }

    Class<?>[] discoverProxyClasses(ServiceReference ref) {
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Generating greedy proxy for service " + OsgiStringUtils.nullSafeToString(ref)));
        }
        Object[] classNames = OsgiServiceReferenceUtils.getServiceObjectClasses(ref);
        if (trace) {
            log.trace((Object)("Discovered raw classes " + ObjectUtils.nullSafeToString((Object[])classNames)));
        }
        Object[] classes = ClassUtils.loadClassesIfPossible((String[])classNames, this.classLoader);
        if (trace) {
            log.trace((Object)("Visible classes are " + ObjectUtils.nullSafeToString((Object[])classes)));
        }
        classes = ClassUtils.excludeClassesWithModifier(classes, 16);
        if (trace) {
            log.trace((Object)("Filtering out final classes; left out with " + ObjectUtils.nullSafeToString((Object[])classes)));
        }
        if (this.interfacesOnlyProxying) {
            LinkedHashSet<Object> clazzes = new LinkedHashSet<Object>(classes.length);
            for (int classIndex = 0; classIndex < classes.length; ++classIndex) {
                Object clazz = classes[classIndex];
                if (!((Class)clazz).isInterface()) continue;
                clazzes.add(clazz);
            }
            if (trace) {
                log.trace((Object)("Filtering out concrete classes; left out with " + clazzes));
            }
            classes = clazzes.toArray(new Class[clazzes.size()]);
        }
        classes = ClassUtils.removeParents(classes);
        if (trace) {
            log.trace((Object)("Filtering out parent classes; left out with " + classes));
        }
        return classes;
    }

    @Override
    Class<?>[] getInterfaces(ServiceReference reference) {
        if (this.greedyProxying) {
            Object[] clazzes = this.discoverProxyClasses(reference);
            if (log.isTraceEnabled()) {
                log.trace((Object)("generating 'greedy' service proxy using classes " + ObjectUtils.nullSafeToString((Object[])clazzes) + " over " + ObjectUtils.nullSafeToString((Object[])this.classes)));
            }
            return clazzes;
        }
        return this.classes;
    }
}

