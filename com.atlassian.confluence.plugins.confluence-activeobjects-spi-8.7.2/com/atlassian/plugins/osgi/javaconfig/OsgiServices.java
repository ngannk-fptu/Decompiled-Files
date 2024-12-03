/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  javax.annotation.Nonnull
 *  org.eclipse.gemini.blueprint.context.BundleContextAware
 *  org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener
 *  org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector
 *  org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum
 *  org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector
 *  org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean
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
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.ImportOptions;
import com.atlassian.plugins.osgi.javaconfig.ServiceCollection;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

@PublicApi
public final class OsgiServices {
    public static <T> FactoryBean<T> factoryBeanForOsgiService(Class<T> serviceInterface) {
        return OsgiServices.factoryBeanForOsgiService(serviceInterface, ImportOptions.defaultOptions());
    }

    public static <T> FactoryBean<T> factoryBeanForOsgiService(Class<T> serviceInterface, ImportOptions options) {
        OsgiServiceProxyFactoryBean factoryBean = new OsgiServiceProxyFactoryBean();
        factoryBean.setAvailability(options.getAvailability());
        factoryBean.setBeanClassLoader(serviceInterface.getClassLoader());
        factoryBean.setFilter(options.getFilter());
        factoryBean.setInterfaces(new Class[]{serviceInterface});
        factoryBean.setTimeout(options.getTimeout().toMillis());
        return factoryBean;
    }

    public static <T> T importOsgiService(Class<T> serviceClass) {
        return OsgiServices.importOsgiService(serviceClass, ImportOptions.defaultOptions());
    }

    public static <T> T importOsgiService(Class<T> serviceClass, ImportOptions options) {
        return OsgiServices.invokeFactoryBean(OsgiServices.factoryBeanForOsgiService(serviceClass, options));
    }

    public static <T> T importOsgiServiceCollection(@Nonnull ServiceCollection<T> serviceCollection) {
        Objects.requireNonNull(serviceCollection, ServiceCollection.class.getSimpleName() + " should not be null.");
        return OsgiServices.importOsgiServiceCollection(serviceCollection, ImportOptions.defaultOptions().optional());
    }

    public static <T> T importOsgiServiceCollection(@Nonnull ServiceCollection<T> serviceCollection, @Nonnull ImportOptions options) {
        Objects.requireNonNull(serviceCollection, ServiceCollection.class.getSimpleName() + " should not be null.");
        Objects.requireNonNull(options, ImportOptions.class.getSimpleName() + " should not be null.");
        return OsgiServices.invokeFactoryBean(OsgiServices.factoryBeanForOsgiServiceCollection(serviceCollection, options));
    }

    public static <T> FactoryBean<T> factoryBeanForOsgiServiceCollection(@Nonnull ServiceCollection<T> serviceCollection) {
        Objects.requireNonNull(serviceCollection, ServiceCollection.class.getSimpleName() + " should not be null.");
        return OsgiServices.factoryBeanForOsgiServiceCollection(serviceCollection, ImportOptions.defaultOptions().optional());
    }

    public static <T> FactoryBean<T> factoryBeanForOsgiServiceCollection(@Nonnull ServiceCollection<T> serviceCollection, @Nonnull ImportOptions options) {
        Objects.requireNonNull(serviceCollection, ServiceCollection.class.getSimpleName() + " should not be null.");
        Objects.requireNonNull(options, ImportOptions.class.getSimpleName() + " should not be null.");
        Class<?> serviceClass = serviceCollection.getServiceClass();
        OsgiServiceCollectionProxyFactoryBean factoryBean = new OsgiServiceCollectionProxyFactoryBean();
        factoryBean.setAvailability(options.getAvailability());
        factoryBean.setBeanClassLoader(serviceClass.getClassLoader());
        factoryBean.setFilter(options.getFilter());
        factoryBean.setInterfaces(new Class[]{serviceClass});
        factoryBean.setCollectionType(serviceCollection.getCollectionType());
        factoryBean.setComparator(serviceCollection.getComparator());
        return factoryBean;
    }

    private static <T> T invokeFactoryBean(FactoryBean<T> factoryBean) {
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
        return OsgiServices.exportOsgiService(moduleDescriptorFactory, ExportOptions.as(ListableModuleDescriptorFactory.class, new Class[0]));
    }

    public static FactoryBean<ServiceRegistration> exportOsgiService(Object bean, ExportOptions options) {
        OsgiServiceFactoryBean exporter = new OsgiServiceFactoryBean();
        exporter.setInterfaceDetector((InterfaceDetector)DefaultInterfaceDetector.DISABLED);
        exporter.setBeanClassLoader(bean.getClass().getClassLoader());
        exporter.setExportContextClassLoader(ExportContextClassLoaderEnum.UNMANAGED);
        exporter.setInterfaces((Class[])options.getInterfaces());
        exporter.setServiceProperties(options.getProperties());
        exporter.setTarget(bean);
        exporter.setListeners(new OsgiServiceRegistrationListener[0]);
        return exporter;
    }

    private OsgiServices() {
    }
}

