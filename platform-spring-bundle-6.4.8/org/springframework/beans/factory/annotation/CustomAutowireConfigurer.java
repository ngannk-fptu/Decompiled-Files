/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class CustomAutowireConfigurer
implements BeanFactoryPostProcessor,
BeanClassLoaderAware,
Ordered {
    private int order = Integer.MAX_VALUE;
    @Nullable
    private Set<?> customQualifierTypes;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public void setCustomQualifierTypes(Set<?> customQualifierTypes) {
        this.customQualifierTypes = customQualifierTypes;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.customQualifierTypes != null) {
            if (!(beanFactory instanceof DefaultListableBeanFactory)) {
                throw new IllegalStateException("CustomAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
            }
            DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory)beanFactory;
            if (!(dlbf.getAutowireCandidateResolver() instanceof QualifierAnnotationAutowireCandidateResolver)) {
                dlbf.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
            }
            QualifierAnnotationAutowireCandidateResolver resolver = (QualifierAnnotationAutowireCandidateResolver)dlbf.getAutowireCandidateResolver();
            for (Object value : this.customQualifierTypes) {
                Class<?> customType = null;
                if (value instanceof Class) {
                    customType = (Class<?>)value;
                } else if (value instanceof String) {
                    String className = (String)value;
                    customType = ClassUtils.resolveClassName(className, this.beanClassLoader);
                } else {
                    throw new IllegalArgumentException("Invalid value [" + value + "] for custom qualifier type: needs to be Class or String.");
                }
                if (!Annotation.class.isAssignableFrom(customType)) {
                    throw new IllegalArgumentException("Qualifier type [" + customType.getName() + "] needs to be annotation type");
                }
                resolver.addQualifierType((Class<? extends Annotation>)customType);
            }
        }
    }
}

