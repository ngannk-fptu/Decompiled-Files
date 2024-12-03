/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface ListableBeanFactory
extends BeanFactory {
    public boolean containsBeanDefinition(String var1);

    public int getBeanDefinitionCount();

    public String[] getBeanDefinitionNames();

    public <T> ObjectProvider<T> getBeanProvider(Class<T> var1, boolean var2);

    public <T> ObjectProvider<T> getBeanProvider(ResolvableType var1, boolean var2);

    public String[] getBeanNamesForType(ResolvableType var1);

    public String[] getBeanNamesForType(ResolvableType var1, boolean var2, boolean var3);

    public String[] getBeanNamesForType(@Nullable Class<?> var1);

    public String[] getBeanNamesForType(@Nullable Class<?> var1, boolean var2, boolean var3);

    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> var1) throws BeansException;

    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> var1, boolean var2, boolean var3) throws BeansException;

    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> var1);

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> var1) throws BeansException;

    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String var1, Class<A> var2) throws NoSuchBeanDefinitionException;

    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String var1, Class<A> var2, boolean var3) throws NoSuchBeanDefinitionException;
}

