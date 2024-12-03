/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  javax.annotation.Nullable
 *  org.eclipse.gemini.blueprint.context.BundleContextAware
 *  org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener
 *  org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector
 *  org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum
 *  org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector
 *  org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.BeanInitializationException
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.osgi.javaconfig;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

@PublicApi
public final class OsgiServices {
    public static Duration defaultImportTimeout() {
        return Duration.of(5L, ChronoUnit.MINUTES);
    }

    public static <T> FactoryBean<T> factoryBeanForOsgiService(Class<T> serviceInterface) {
        return OsgiServices.factoryBeanForOsgiService(serviceInterface, null);
    }

    public static <T> FactoryBean<T> factoryBeanForOsgiService(Class<T> serviceInterface, @Nullable String filter) {
        OsgiServiceProxyFactoryBean factoryBean = new OsgiServiceProxyFactoryBean();
        factoryBean.setFilter(filter);
        factoryBean.setInterfaces(new Class[]{serviceInterface});
        factoryBean.setBeanClassLoader(serviceInterface.getClassLoader());
        factoryBean.setTimeout(OsgiServices.defaultImportTimeout().toMillis());
        return factoryBean;
    }

    public static <T> T importOsgiService(Class<T> serviceClass) {
        return OsgiServices.importOsgiService(serviceClass, null);
    }

    public static <T> T importOsgiService(Class<T> serviceClass, @Nullable String filter) {
        FactoryBean<T> factoryBean = OsgiServices.factoryBeanForOsgiService(serviceClass, filter);
        try {
            if (factoryBean instanceof BundleContextAware) {
                BundleContext bundleContext = FrameworkUtil.getBundle(OsgiServices.class).getBundleContext();
                ((BundleContextAware)factoryBean).setBundleContext(bundleContext);
            }
            if (factoryBean instanceof InitializingBean) {
                ((InitializingBean)factoryBean).afterPropertiesSet();
            }
            return (T)factoryBean.getObject();
        }
        catch (Exception e) {
            throw new BeanInitializationException(e.getMessage(), (Throwable)e);
        }
    }

    public static FactoryBean<ServiceRegistration> exportAsModuleType(ListableModuleDescriptorFactory moduleDescriptorFactory) {
        return OsgiServices.exportOsgiService(moduleDescriptorFactory, Collections.emptyMap(), ListableModuleDescriptorFactory.class, new Class[0]);
    }

    public static FactoryBean<ServiceRegistration> exportOsgiService(Object bean, @Nullable Map<String, Object> serviceProps, Class<?> firstInterface, Class<?> ... otherInterfaces) {
        HashMap mutableServiceProps = new HashMap(serviceProps == null ? Collections.emptyMap() : serviceProps);
        Class[] interfaces = OsgiServices.concatClasses(firstInterface, otherInterfaces);
        OsgiServiceFactoryBean exporter = new OsgiServiceFactoryBean();
        exporter.setInterfaceDetector((InterfaceDetector)DefaultInterfaceDetector.DISABLED);
        exporter.setBeanClassLoader(bean.getClass().getClassLoader());
        exporter.setExportContextClassLoader(ExportContextClassLoaderEnum.UNMANAGED);
        exporter.setInterfaces(interfaces);
        exporter.setServiceProperties(mutableServiceProps);
        exporter.setTarget(bean);
        exporter.setListeners(new OsgiServiceRegistrationListener[0]);
        return exporter;
    }

    private static Class<?>[] concatClasses(Class<?> firstInterface, Class<?>[] otherInterfaces) {
        return (Class[])Stream.concat(Stream.of(firstInterface), Stream.of(otherInterfaces)).toArray(Class[]::new);
    }

    private OsgiServices() {
    }
}

