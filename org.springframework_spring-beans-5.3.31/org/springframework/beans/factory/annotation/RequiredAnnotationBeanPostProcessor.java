/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Conventions
 *  org.springframework.core.PriorityOrdered
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Conventions;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class RequiredAnnotationBeanPostProcessor
implements SmartInstantiationAwareBeanPostProcessor,
MergedBeanDefinitionPostProcessor,
PriorityOrdered,
BeanFactoryAware {
    public static final String SKIP_REQUIRED_CHECK_ATTRIBUTE = Conventions.getQualifiedAttributeName(RequiredAnnotationBeanPostProcessor.class, (String)"skipRequiredCheck");
    private Class<? extends Annotation> requiredAnnotationType = Required.class;
    private int order = 0x7FFFFFFE;
    @Nullable
    private ConfigurableListableBeanFactory beanFactory;
    private final Set<String> validatedBeanNames = Collections.newSetFromMap(new ConcurrentHashMap(64));

    public void setRequiredAnnotationType(Class<? extends Annotation> requiredAnnotationType) {
        Assert.notNull(requiredAnnotationType, (String)"'requiredAnnotationType' must not be null");
        this.requiredAnnotationType = requiredAnnotationType;
    }

    protected Class<? extends Annotation> getRequiredAnnotationType() {
        return this.requiredAnnotationType;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) {
        if (!this.validatedBeanNames.contains(beanName)) {
            if (!this.shouldSkip(this.beanFactory, beanName)) {
                ArrayList<String> invalidProperties = new ArrayList<String>();
                for (PropertyDescriptor pd : pds) {
                    if (!this.isRequiredProperty(pd) || pvs.contains(pd.getName())) continue;
                    invalidProperties.add(pd.getName());
                }
                if (!invalidProperties.isEmpty()) {
                    throw new BeanInitializationException(this.buildExceptionMessage(invalidProperties, beanName));
                }
            }
            this.validatedBeanNames.add(beanName);
        }
        return pvs;
    }

    protected boolean shouldSkip(@Nullable ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory == null || !beanFactory.containsBeanDefinition(beanName)) {
            return false;
        }
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        if (beanDefinition.getFactoryBeanName() != null) {
            return true;
        }
        Object value = beanDefinition.getAttribute(SKIP_REQUIRED_CHECK_ATTRIBUTE);
        return value != null && (Boolean.TRUE.equals(value) || Boolean.parseBoolean(value.toString()));
    }

    protected boolean isRequiredProperty(PropertyDescriptor propertyDescriptor) {
        Method setter = propertyDescriptor.getWriteMethod();
        return setter != null && AnnotationUtils.getAnnotation((Method)setter, this.getRequiredAnnotationType()) != null;
    }

    private String buildExceptionMessage(List<String> invalidProperties, String beanName) {
        int size = invalidProperties.size();
        StringBuilder sb = new StringBuilder();
        sb.append(size == 1 ? "Property" : "Properties");
        for (int i = 0; i < size; ++i) {
            String propertyName = invalidProperties.get(i);
            if (i > 0) {
                if (i == size - 1) {
                    sb.append(" and");
                } else {
                    sb.append(',');
                }
            }
            sb.append(" '").append(propertyName).append('\'');
        }
        sb.append(size == 1 ? " is" : " are");
        sb.append(" required for bean '").append(beanName).append('\'');
        return sb.toString();
    }
}

