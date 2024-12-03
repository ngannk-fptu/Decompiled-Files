/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.annotation.AnnotationBeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedNotification;
import org.springframework.jmx.export.annotation.ManagedNotifications;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

public class AnnotationJmxAttributeSource
implements JmxAttributeSource,
BeanFactoryAware {
    @Nullable
    private StringValueResolver embeddedValueResolver;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.embeddedValueResolver = new EmbeddedValueResolver((ConfigurableBeanFactory)beanFactory);
        }
    }

    @Override
    @Nullable
    public org.springframework.jmx.export.metadata.ManagedResource getManagedResource(Class<?> beanClass) throws InvalidMetadataException {
        Class<?> target;
        ManagedResource ann = AnnotationUtils.findAnnotation(beanClass, ManagedResource.class);
        if (ann == null) {
            return null;
        }
        Class<?> declaringClass = AnnotationUtils.findAnnotationDeclaringClass(ManagedResource.class, beanClass);
        Class<?> clazz = target = declaringClass != null && !declaringClass.isInterface() ? declaringClass : beanClass;
        if (!Modifier.isPublic(target.getModifiers())) {
            throw new InvalidMetadataException("@ManagedResource class '" + target.getName() + "' must be public");
        }
        org.springframework.jmx.export.metadata.ManagedResource managedResource = new org.springframework.jmx.export.metadata.ManagedResource();
        AnnotationBeanUtils.copyPropertiesToBean((Annotation)ann, (Object)managedResource, this.embeddedValueResolver, new String[0]);
        return managedResource;
    }

    @Override
    @Nullable
    public org.springframework.jmx.export.metadata.ManagedAttribute getManagedAttribute(Method method) throws InvalidMetadataException {
        ManagedAttribute ann = AnnotationUtils.findAnnotation(method, ManagedAttribute.class);
        if (ann == null) {
            return null;
        }
        org.springframework.jmx.export.metadata.ManagedAttribute managedAttribute = new org.springframework.jmx.export.metadata.ManagedAttribute();
        AnnotationBeanUtils.copyPropertiesToBean((Annotation)ann, (Object)managedAttribute, "defaultValue");
        if (ann.defaultValue().length() > 0) {
            managedAttribute.setDefaultValue(ann.defaultValue());
        }
        return managedAttribute;
    }

    @Override
    @Nullable
    public org.springframework.jmx.export.metadata.ManagedMetric getManagedMetric(Method method) throws InvalidMetadataException {
        ManagedMetric ann = AnnotationUtils.findAnnotation(method, ManagedMetric.class);
        return AnnotationJmxAttributeSource.copyPropertiesToBean(ann, org.springframework.jmx.export.metadata.ManagedMetric.class);
    }

    @Override
    @Nullable
    public org.springframework.jmx.export.metadata.ManagedOperation getManagedOperation(Method method) throws InvalidMetadataException {
        ManagedOperation ann = AnnotationUtils.findAnnotation(method, ManagedOperation.class);
        return AnnotationJmxAttributeSource.copyPropertiesToBean(ann, org.springframework.jmx.export.metadata.ManagedOperation.class);
    }

    @Override
    public org.springframework.jmx.export.metadata.ManagedOperationParameter[] getManagedOperationParameters(Method method) throws InvalidMetadataException {
        Set<ManagedOperationParameter> anns = AnnotationUtils.getRepeatableAnnotations(method, ManagedOperationParameter.class, ManagedOperationParameters.class);
        return AnnotationJmxAttributeSource.copyPropertiesToBeanArray(anns, org.springframework.jmx.export.metadata.ManagedOperationParameter.class);
    }

    @Override
    public org.springframework.jmx.export.metadata.ManagedNotification[] getManagedNotifications(Class<?> clazz) throws InvalidMetadataException {
        Set<ManagedNotification> anns = AnnotationUtils.getRepeatableAnnotations(clazz, ManagedNotification.class, ManagedNotifications.class);
        return AnnotationJmxAttributeSource.copyPropertiesToBeanArray(anns, org.springframework.jmx.export.metadata.ManagedNotification.class);
    }

    private static <T> T[] copyPropertiesToBeanArray(Collection<? extends Annotation> anns, Class<T> beanClass) {
        Object[] beans2 = (Object[])Array.newInstance(beanClass, anns.size());
        int i = 0;
        for (Annotation annotation : anns) {
            beans2[i++] = AnnotationJmxAttributeSource.copyPropertiesToBean(annotation, beanClass);
        }
        return beans2;
    }

    @Nullable
    private static <T> T copyPropertiesToBean(@Nullable Annotation ann, Class<T> beanClass) {
        if (ann == null) {
            return null;
        }
        T bean2 = BeanUtils.instantiateClass(beanClass);
        AnnotationBeanUtils.copyPropertiesToBean(ann, bean2, new String[0]);
        return bean2;
    }
}

