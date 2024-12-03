/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Conventions;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

abstract class ConfigurationClassUtils {
    private static final String CONFIGURATION_CLASS_FULL = "full";
    private static final String CONFIGURATION_CLASS_LITE = "lite";
    private static final String CONFIGURATION_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");
    private static final String ORDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "order");
    private static final Log logger = LogFactory.getLog(ConfigurationClassUtils.class);
    private static final Set<String> candidateIndicators = new HashSet<String>(8);

    ConfigurationClassUtils() {
    }

    public static boolean checkConfigurationClassCandidate(BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {
        AnnotationMetadata metadata;
        String className = beanDef.getBeanClassName();
        if (className == null || beanDef.getFactoryMethodName() != null) {
            return false;
        }
        if (beanDef instanceof AnnotatedBeanDefinition && className.equals(((AnnotatedBeanDefinition)beanDef).getMetadata().getClassName())) {
            metadata = ((AnnotatedBeanDefinition)beanDef).getMetadata();
        } else if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition)beanDef).hasBeanClass()) {
            Class<?> beanClass = ((AbstractBeanDefinition)beanDef).getBeanClass();
            metadata = new StandardAnnotationMetadata(beanClass, true);
        } else {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                metadata = metadataReader.getAnnotationMetadata();
            }
            catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not find class file for introspecting configuration annotations: " + className, ex);
                }
                return false;
            }
        }
        if (ConfigurationClassUtils.isFullConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
        } else if (ConfigurationClassUtils.isLiteConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
        } else {
            return false;
        }
        Integer order = ConfigurationClassUtils.getOrder(metadata);
        if (order != null) {
            beanDef.setAttribute(ORDER_ATTRIBUTE, order);
        }
        return true;
    }

    public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
        return ConfigurationClassUtils.isFullConfigurationCandidate(metadata) || ConfigurationClassUtils.isLiteConfigurationCandidate(metadata);
    }

    public static boolean isFullConfigurationCandidate(AnnotationMetadata metadata) {
        return metadata.isAnnotated(Configuration.class.getName());
    }

    public static boolean isLiteConfigurationCandidate(AnnotationMetadata metadata) {
        if (metadata.isInterface()) {
            return false;
        }
        for (String indicator : candidateIndicators) {
            if (!metadata.isAnnotated(indicator)) continue;
            return true;
        }
        try {
            return metadata.hasAnnotatedMethods(Bean.class.getName());
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
            }
            return false;
        }
    }

    public static boolean isFullConfigurationClass(BeanDefinition beanDef) {
        return CONFIGURATION_CLASS_FULL.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
    }

    public static boolean isLiteConfigurationClass(BeanDefinition beanDef) {
        return CONFIGURATION_CLASS_LITE.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
    }

    @Nullable
    public static Integer getOrder(AnnotationMetadata metadata) {
        Map<String, Object> orderAttributes = metadata.getAnnotationAttributes(Order.class.getName());
        return orderAttributes != null ? (Integer)orderAttributes.get("value") : null;
    }

    public static int getOrder(BeanDefinition beanDef) {
        Integer order = (Integer)beanDef.getAttribute(ORDER_ATTRIBUTE);
        return order != null ? order : Integer.MAX_VALUE;
    }

    static {
        candidateIndicators.add(Component.class.getName());
        candidateIndicators.add(ComponentScan.class.getName());
        candidateIndicators.add(Import.class.getName());
        candidateIndicators.add(ImportResource.class.getName());
    }
}

