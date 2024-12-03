/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 */
package com.atlassian.plugin.spring.scanner.runtime.impl;

import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.spring.scanner.runtime.impl.util.AnnotationIndexReader;
import java.beans.Introspector;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

@ParametersAreNonnullByDefault
public class ComponentImportBeanFactoryPostProcessor
implements BeanFactoryPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentImportBeanFactoryPostProcessor.class);
    private final BundleContext bundleContext;
    private String profileName;

    public ComponentImportBeanFactoryPostProcessor(BundleContext bundleContext) {
        this.bundleContext = Objects.requireNonNull(bundleContext);
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        Collection<String> importedServices = this.readServicesToImportFromSpringScannerIndexFiles();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
        ComponentImportBeanFactoryPostProcessor.generateOsgiServiceImportBeans(beanFactory.getBeanClassLoader(), importedServices).forEach((arg_0, arg_1) -> ((BeanDefinitionRegistry)registry).registerBeanDefinition(arg_0, arg_1));
    }

    private Collection<String> readServicesToImportFromSpringScannerIndexFiles() {
        String[] profileNames = AnnotationIndexReader.splitProfiles(this.profileName);
        TreeSet<String> indexFileEntries = new TreeSet<String>();
        for (String fileToRead : AnnotationIndexReader.getIndexFilesForProfiles(profileNames, "imports")) {
            indexFileEntries.addAll(AnnotationIndexReader.readAllIndexFilesForProduct(fileToRead, this.bundleContext));
        }
        return indexFileEntries;
    }

    static Map<String, BeanDefinition> generateOsgiServiceImportBeans(ClassLoader serviceClassLoader, Iterable<String> indexFileEntries) {
        TreeMap<String, BeanDefinition> beanDefinitions = new TreeMap<String, BeanDefinition>();
        for (String indexFileEntry : indexFileEntries) {
            String[] typeAndName = indexFileEntry.split("#");
            String serviceClassName = typeAndName[0];
            ComponentImportBeanFactoryPostProcessor.loadServiceClass(serviceClassName, serviceClassLoader).ifPresent(serviceClass -> {
                String userProvidedBeanName = typeAndName.length > 1 ? typeAndName[1] : null;
                String beanName = (String)StringUtils.defaultIfBlank((CharSequence)userProvidedBeanName, (CharSequence)ComponentImportBeanFactoryPostProcessor.getDefaultBeanName(serviceClass, beanDefinitions.keySet()));
                BeanDefinition beanDefinition = ComponentImportBeanFactoryPostProcessor.generateOsgiServiceImportBean(serviceClass);
                beanDefinitions.put(beanName, beanDefinition);
            });
        }
        return beanDefinitions;
    }

    private static String getDefaultBeanName(Class<?> serviceClass, Collection<String> existingBeanNames) {
        String firstAttempt = Introspector.decapitalize(serviceClass.getSimpleName());
        if (!existingBeanNames.contains(firstAttempt)) {
            return firstAttempt;
        }
        return serviceClass.getName();
    }

    private static Optional<Class<?>> loadServiceClass(String serviceClass, ClassLoader serviceClassLoader) {
        try {
            return Optional.of(serviceClassLoader.loadClass(serviceClass));
        }
        catch (ClassNotFoundException e) {
            LOGGER.warn("Unable to load class '{}' for component importation purposes. Skipping...", (Object)serviceClass);
            return Optional.empty();
        }
    }

    static BeanDefinition generateOsgiServiceImportBean(Class<?> serviceClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(OsgiServiceProxyFactoryBeanFactory.class, (String)"create");
        builder.setRole(2);
        builder.addConstructorArgValue(OsgiServiceProxyFactoryBean.class);
        builder.addConstructorArgValue((Object)OsgiPlugin.class.getClassLoader());
        builder.addConstructorArgValue((Object)("(objectClass=" + serviceClass.getName() + ")"));
        builder.addConstructorArgValue((Object)new Class[]{serviceClass});
        return builder.getBeanDefinition();
    }

    static class OsgiServiceProxyFactoryBeanFactory {
        OsgiServiceProxyFactoryBeanFactory() {
        }

        static OsgiServiceProxyFactoryBean create(Class<OsgiServiceProxyFactoryBean> factoryBeanClass, ClassLoader beanClassLoader, String filter, Class<?> ... interfaces) throws ReflectiveOperationException {
            OsgiServiceProxyFactoryBean factoryBean = factoryBeanClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            factoryBean.setBeanClassLoader(beanClassLoader);
            factoryBean.setFilter(filter);
            factoryBean.setInterfaces((Class[])interfaces);
            return factoryBean;
        }
    }
}

