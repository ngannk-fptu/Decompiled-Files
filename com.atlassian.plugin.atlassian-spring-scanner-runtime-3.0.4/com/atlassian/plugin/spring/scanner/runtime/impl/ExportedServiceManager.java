/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener
 *  org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector
 *  org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum
 *  org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector
 *  org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.spring.scanner.runtime.impl;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportedServiceManager {
    private static final Logger log = LoggerFactory.getLogger(ExportedServiceManager.class);
    private final Map<Object, OsgiServiceFactoryBean> exporters = Collections.synchronizedMap(new IdentityHashMap());

    public ServiceRegistration registerService(BundleContext bundleContext, Object bean, String beanName, Map<String, Object> serviceProps, Class<?> ... interfaces) throws Exception {
        OsgiServiceFactoryBean osgiExporter = this.createExporter(bundleContext, bean, beanName, serviceProps, interfaces);
        OsgiServiceFactoryBean replacedBean = this.exporters.put(bean, osgiExporter);
        if (replacedBean != null) {
            log.warn("Tried to register the same instance of {} twice", replacedBean.getClass());
        }
        return osgiExporter.getObject();
    }

    public boolean hasService(Object bean) {
        return this.exporters.containsKey(bean);
    }

    public void unregisterService(BundleContext bundleContext, Object bean) {
        OsgiServiceFactoryBean exporter = this.exporters.remove(bean);
        if (exporter != null) {
            exporter.destroy();
        }
    }

    private OsgiServiceFactoryBean createExporter(BundleContext bundleContext, Object bean, String beanName, Map<String, Object> serviceProps, Class<?>[] interfaces) throws Exception {
        serviceProps.put("org.eclipse.gemini.blueprint.bean.name", beanName);
        OsgiServiceFactoryBean exporter = new OsgiServiceFactoryBean();
        exporter.setInterfaceDetector((InterfaceDetector)DefaultInterfaceDetector.DISABLED);
        exporter.setBeanClassLoader(bean.getClass().getClassLoader());
        exporter.setBeanName(beanName);
        exporter.setBundleContext(bundleContext);
        exporter.setExportContextClassLoader(ExportContextClassLoaderEnum.UNMANAGED);
        exporter.setInterfaces((Class[])interfaces);
        exporter.setServiceProperties(serviceProps);
        exporter.setTarget(bean);
        exporter.setListeners(new OsgiServiceRegistrationListener[0]);
        exporter.afterPropertiesSet();
        return exporter;
    }
}

