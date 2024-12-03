/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.lang.Nullable;

public interface AutowireCapableBeanFactory
extends BeanFactory {
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    public static final String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

    public <T> T createBean(Class<T> var1) throws BeansException;

    public void autowireBean(Object var1) throws BeansException;

    public Object configureBean(Object var1, String var2) throws BeansException;

    public Object createBean(Class<?> var1, int var2, boolean var3) throws BeansException;

    public Object autowire(Class<?> var1, int var2, boolean var3) throws BeansException;

    public void autowireBeanProperties(Object var1, int var2, boolean var3) throws BeansException;

    public void applyBeanPropertyValues(Object var1, String var2) throws BeansException;

    public Object initializeBean(Object var1, String var2) throws BeansException;

    public Object applyBeanPostProcessorsBeforeInitialization(Object var1, String var2) throws BeansException;

    public Object applyBeanPostProcessorsAfterInitialization(Object var1, String var2) throws BeansException;

    public void destroyBean(Object var1);

    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> var1) throws BeansException;

    public Object resolveBeanByName(String var1, DependencyDescriptor var2) throws BeansException;

    @Nullable
    public Object resolveDependency(DependencyDescriptor var1, @Nullable String var2) throws BeansException;

    @Nullable
    public Object resolveDependency(DependencyDescriptor var1, @Nullable String var2, @Nullable Set<String> var3, @Nullable TypeConverter var4) throws BeansException;
}

