/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.CachedIntrospectionResults
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.CachedIntrospectionResults;

public class JavaBeansCacheActivator
implements BundleActivator {
    private final Object monitor = new Object();
    private boolean stopped = false;

    public void start(BundleContext extenderBundleContext) {
        this.initJavaBeansCache();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop(BundleContext extenderBundleContext) {
        Object object = this.monitor;
        synchronized (object) {
            if (this.stopped) {
                return;
            }
            this.stopped = true;
        }
        this.destroyJavaBeansCache();
    }

    private void initJavaBeansCache() {
        Class[] classes = new Class[]{OsgiServiceFactoryBean.class, OsgiServiceProxyFactoryBean.class, OsgiServiceCollectionProxyFactoryBean.class};
        CachedIntrospectionResults.acceptClassLoader((ClassLoader)OsgiStringUtils.class.getClassLoader());
        for (Class clazz : classes) {
            BeanUtils.getPropertyDescriptors((Class)clazz);
        }
    }

    private void destroyJavaBeansCache() {
        CachedIntrospectionResults.clearClassLoader((ClassLoader)OsgiStringUtils.class.getClassLoader());
    }
}

