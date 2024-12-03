/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
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
        Class target;
        MergedAnnotation<ManagedResource> ann = MergedAnnotations.from(beanClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ManagedResource.class).withNonMergedAttributes();
        if (!ann.isPresent()) {
            return null;
        }
        Class declaringClass = (Class)ann.getSource();
        Class clazz = target = declaringClass != null && !declaringClass.isInterface() ? declaringClass : beanClass;
        if (!Modifier.isPublic(target.getModifiers())) {
            throw new InvalidMetadataException("@ManagedResource class '" + target.getName() + "' must be public");
        }
        org.springframework.jmx.export.metadata.ManagedResource bean2 = new org.springframework.jmx.export.metadata.ManagedResource();
        Map<String, Object> map = ann.asMap(new MergedAnnotation.Adapt[0]);
        ArrayList<PropertyValue> list = new ArrayList<PropertyValue>(map.size());
        map.forEach((attrName, attrValue) -> {
            if (!"value".equals(attrName)) {
                Object value = attrValue;
                if (this.embeddedValueResolver != null && value instanceof String) {
                    value = this.embeddedValueResolver.resolveStringValue((String)value);
                }
                list.add(new PropertyValue((String)attrName, value));
            }
        });
        PropertyAccessorFactory.forBeanPropertyAccess(bean2).setPropertyValues(new MutablePropertyValues(list));
        return bean2;
    }

    @Override
    @Nullable
    public org.springframework.jmx.export.metadata.ManagedAttribute getManagedAttribute(Method method) throws InvalidMetadataException {
        MergedAnnotation<ManagedAttribute> ann = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ManagedAttribute.class).withNonMergedAttributes();
        if (!ann.isPresent()) {
            return null;
        }
        org.springframework.jmx.export.metadata.ManagedAttribute bean2 = new org.springframework.jmx.export.metadata.ManagedAttribute();
        Map<String, Object> map = ann.asMap(new MergedAnnotation.Adapt[0]);
        MutablePropertyValues pvs = new MutablePropertyValues(map);
        pvs.removePropertyValue("defaultValue");
        PropertyAccessorFactory.forBeanPropertyAccess(bean2).setPropertyValues(pvs);
        String defaultValue = (String)map.get("defaultValue");
        if (defaultValue.length() > 0) {
            bean2.setDefaultValue(defaultValue);
        }
        return bean2;
    }

    @Override
    @Nullable
    public org.springframework.jmx.export.metadata.ManagedMetric getManagedMetric(Method method) throws InvalidMetadataException {
        MergedAnnotation<ManagedMetric> ann = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ManagedMetric.class).withNonMergedAttributes();
        return AnnotationJmxAttributeSource.copyPropertiesToBean(ann, org.springframework.jmx.export.metadata.ManagedMetric.class);
    }

    @Override
    @Nullable
    public org.springframework.jmx.export.metadata.ManagedOperation getManagedOperation(Method method) throws InvalidMetadataException {
        MergedAnnotation<ManagedOperation> ann = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ManagedOperation.class).withNonMergedAttributes();
        return AnnotationJmxAttributeSource.copyPropertiesToBean(ann, org.springframework.jmx.export.metadata.ManagedOperation.class);
    }

    @Override
    public org.springframework.jmx.export.metadata.ManagedOperationParameter[] getManagedOperationParameters(Method method) throws InvalidMetadataException {
        List<MergedAnnotation<? extends Annotation>> anns = AnnotationJmxAttributeSource.getRepeatableAnnotations(method, ManagedOperationParameter.class, ManagedOperationParameters.class);
        return AnnotationJmxAttributeSource.copyPropertiesToBeanArray(anns, org.springframework.jmx.export.metadata.ManagedOperationParameter.class);
    }

    @Override
    public org.springframework.jmx.export.metadata.ManagedNotification[] getManagedNotifications(Class<?> clazz) throws InvalidMetadataException {
        List<MergedAnnotation<? extends Annotation>> anns = AnnotationJmxAttributeSource.getRepeatableAnnotations(clazz, ManagedNotification.class, ManagedNotifications.class);
        return AnnotationJmxAttributeSource.copyPropertiesToBeanArray(anns, org.springframework.jmx.export.metadata.ManagedNotification.class);
    }

    private static List<MergedAnnotation<? extends Annotation>> getRepeatableAnnotations(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType, Class<? extends Annotation> containerAnnotationType) {
        return MergedAnnotations.from(annotatedElement, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.of(annotationType, containerAnnotationType)).stream(annotationType).filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex)).map(MergedAnnotation::withNonMergedAttributes).collect(Collectors.toList());
    }

    private static <T> T[] copyPropertiesToBeanArray(List<MergedAnnotation<? extends Annotation>> anns, Class<T> beanClass) {
        Object[] beans2 = (Object[])Array.newInstance(beanClass, anns.size());
        int i2 = 0;
        for (MergedAnnotation<? extends Annotation> ann : anns) {
            beans2[i2++] = AnnotationJmxAttributeSource.copyPropertiesToBean(ann, beanClass);
        }
        return beans2;
    }

    @Nullable
    private static <T> T copyPropertiesToBean(MergedAnnotation<? extends Annotation> ann, Class<T> beanClass) {
        if (!ann.isPresent()) {
            return null;
        }
        T bean2 = BeanUtils.instantiateClass(beanClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean2);
        bw.setPropertyValues(new MutablePropertyValues(ann.asMap(new MergedAnnotation.Adapt[0])));
        return bean2;
    }
}

