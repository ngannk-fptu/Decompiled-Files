/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.framework.AopInfrastructureBean
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.core.Conventions
 *  org.springframework.core.annotation.Order
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.annotation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.Conventions;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

abstract class ConfigurationClassUtils {
    public static final String CONFIGURATION_CLASS_FULL = "full";
    public static final String CONFIGURATION_CLASS_LITE = "lite";
    public static final String CONFIGURATION_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, (String)"configurationClass");
    private static final String ORDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, (String)"order");
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
            Class beanClass = ((AbstractBeanDefinition)beanDef).getBeanClass();
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass) || BeanPostProcessor.class.isAssignableFrom(beanClass) || AopInfrastructureBean.class.isAssignableFrom(beanClass) || EventListenerFactory.class.isAssignableFrom(beanClass)) {
                return false;
            }
            metadata = AnnotationMetadata.introspect((Class)beanClass);
        } else {
            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
                metadata = metadataReader.getAnnotationMetadata();
            }
            catch (IOException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Could not find class file for introspecting configuration annotations: " + className), (Throwable)ex);
                }
                return false;
            }
        }
        Map config = metadata.getAnnotationAttributes(Configuration.class.getName());
        if (config != null && !Boolean.FALSE.equals(config.get("proxyBeanMethods"))) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, (Object)CONFIGURATION_CLASS_FULL);
        } else if (config != null || ConfigurationClassUtils.isConfigurationCandidate(metadata)) {
            beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, (Object)CONFIGURATION_CLASS_LITE);
        } else {
            return false;
        }
        Integer order = ConfigurationClassUtils.getOrder(metadata);
        if (order != null) {
            beanDef.setAttribute(ORDER_ATTRIBUTE, (Object)order);
        }
        return true;
    }

    public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
        if (metadata.isInterface()) {
            return false;
        }
        for (String indicator : candidateIndicators) {
            if (!metadata.isAnnotated(indicator)) continue;
            return true;
        }
        return ConfigurationClassUtils.hasBeanMethods(metadata);
    }

    static boolean hasBeanMethods(AnnotationMetadata metadata) {
        try {
            return metadata.hasAnnotatedMethods(Bean.class.getName());
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex));
            }
            return false;
        }
    }

    @Nullable
    public static Integer getOrder(AnnotationMetadata metadata) {
        Map orderAttributes = metadata.getAnnotationAttributes(Order.class.getName());
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

