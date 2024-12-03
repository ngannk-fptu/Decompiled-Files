/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.export.ModuleType
 *  com.atlassian.plugin.spring.scanner.annotation.export.ServiceProperty
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
 */
package com.atlassian.plugin.spring.scanner.runtime.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;
import com.atlassian.plugin.spring.scanner.annotation.export.ServiceProperty;
import com.atlassian.plugin.spring.scanner.runtime.impl.ExportedServiceManager;
import com.atlassian.plugin.spring.scanner.runtime.impl.util.AnnotationIndexReader;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class ServiceExporterBeanPostProcessor
implements DestructionAwareBeanPostProcessor,
InitializingBean {
    public static final String OSGI_SERVICE_SUFFIX = "_osgiService";
    static final String ATLASSIAN_DEV_MODE_PROP = "atlassian.dev.mode";
    private static final Logger log = LoggerFactory.getLogger(ServiceExporterBeanPostProcessor.class);
    private final boolean isDevMode = Boolean.parseBoolean(System.getProperty("atlassian.dev.mode", "false"));
    private final BundleContext bundleContext;
    private final ConfigurableListableBeanFactory beanFactory;
    private final ExportedServiceManager serviceManager;
    private final Map<String, String[]> exports;
    private String profileName;

    public ServiceExporterBeanPostProcessor(BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory) {
        this(bundleContext, beanFactory, new ExportedServiceManager());
    }

    ServiceExporterBeanPostProcessor(BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory, ExportedServiceManager serviceManager) {
        this.bundleContext = bundleContext;
        this.beanFactory = beanFactory;
        this.exports = new HashMap<String, String[]>();
        this.profileName = null;
        this.serviceManager = serviceManager;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void afterPropertiesSet() {
        String[] profileNames = AnnotationIndexReader.splitProfiles(this.profileName);
        this.parseExportsForExportFile("exports", profileNames);
        if (this.isDevMode) {
            this.parseExportsForExportFile("dev-exports", profileNames);
        }
    }

    private void parseExportsForExportFile(String exportFileName, String[] profileNames) {
        String[] defaultInterfaces = new String[]{};
        for (String fileToRead : AnnotationIndexReader.getIndexFilesForProfiles(profileNames, exportFileName)) {
            List<String> exportData = AnnotationIndexReader.readAllIndexFilesForProduct(fileToRead, this.bundleContext);
            for (String export : exportData) {
                String[] targetAndInterfaces = export.split("#");
                String target = targetAndInterfaces[0];
                String[] interfaces = targetAndInterfaces.length > 1 ? targetAndInterfaces[1].split(",") : defaultInterfaces;
                this.exports.put(target, interfaces);
            }
        }
    }

    public void postProcessBeforeDestruction(Object bean, String beanName) {
        if (this.serviceManager.hasService(bean)) {
            Object serviceBean;
            this.serviceManager.unregisterService(this.bundleContext, bean);
            String serviceName = this.getServiceName(beanName);
            if (this.beanFactory.containsBean(serviceName) && null != (serviceBean = this.beanFactory.getBean(serviceName))) {
                this.beanFactory.destroyBean(serviceName, serviceBean);
            }
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class[] interfaces = new Class[]{};
        ServiceProperty[] serviceProperties = new ServiceProperty[]{};
        Class beanTargetClass = AopUtils.getTargetClass((Object)bean);
        String beanClassName = beanTargetClass.getName();
        if (this.exports.containsKey(beanClassName) || this.isPublicComponent(beanTargetClass)) {
            if (this.exports.containsKey(beanClassName)) {
                interfaces = this.getExportedInterfaces(bean, beanName, beanClassName);
            } else if (this.hasAnnotation(beanTargetClass, ModuleType.class)) {
                interfaces = beanTargetClass.getAnnotation(ModuleType.class).value();
                serviceProperties = beanTargetClass.getAnnotation(ModuleType.class).properties();
            } else if (this.hasAnnotation(beanTargetClass, ExportAsService.class)) {
                interfaces = beanTargetClass.getAnnotation(ExportAsService.class).value();
                serviceProperties = beanTargetClass.getAnnotation(ExportAsService.class).properties();
            } else if (this.hasAnnotation(beanTargetClass, ExportAsDevService.class)) {
                interfaces = beanTargetClass.getAnnotation(ExportAsDevService.class).value();
                serviceProperties = beanTargetClass.getAnnotation(ExportAsDevService.class).properties();
            }
            if (interfaces.length < 1 && (interfaces = beanTargetClass.getInterfaces()).length < 1) {
                interfaces = new Class[]{beanTargetClass};
            }
            try {
                ServiceRegistration serviceRegistration = this.serviceManager.registerService(this.bundleContext, bean, beanName, this.asMap(serviceProperties), interfaces);
                String serviceName = this.getServiceName(beanName);
                this.beanFactory.initializeBean((Object)serviceRegistration, serviceName);
            }
            catch (Exception e) {
                log.error("Unable to register bean '{}' as an OSGi exported service", (Object)beanName, (Object)e);
            }
        }
        return bean;
    }

    private Class<?>[] getExportedInterfaces(Object bean, String beanName, String beanClassName) {
        ClassLoader beanClassLoader = bean.getClass().getClassLoader();
        return (Class[])Arrays.stream((Object[])this.exports.get(beanClassName)).flatMap(interfaceName -> ServiceExporterBeanPostProcessor.loadClass(interfaceName, beanClassLoader, beanName)).toArray(Class[]::new);
    }

    private static Stream<Class<?>> loadClass(String name, ClassLoader beanClassLoader, String beanName) {
        try {
            return Stream.of(beanClassLoader.loadClass(name));
        }
        catch (ClassNotFoundException e) {
            log.warn("Cannot find class for export '{}' of bean '{}': ", new Object[]{name, beanName, e});
            return Stream.empty();
        }
    }

    private Map<String, Object> asMap(ServiceProperty[] properties) {
        return Arrays.stream(properties).collect(Collectors.toMap(ServiceProperty::key, ServiceProperty::value));
    }

    private boolean isPublicComponent(Class<?> beanTargetClass) {
        return this.hasAnnotation(beanTargetClass, ModuleType.class) || this.hasAnnotation(beanTargetClass, ExportAsService.class) || this.hasAnnotation(beanTargetClass, ExportAsDevService.class) && this.isDevMode;
    }

    private boolean hasAnnotation(Class<?> beanTargetClass, Class<? extends Annotation> annotationClass) {
        return beanTargetClass.isAnnotationPresent(annotationClass);
    }

    private String getServiceName(String beanName) {
        return beanName + OSGI_SERVICE_SUFFIX;
    }
}

