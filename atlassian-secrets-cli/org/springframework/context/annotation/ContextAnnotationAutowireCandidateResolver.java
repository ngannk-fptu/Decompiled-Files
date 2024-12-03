/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ContextAnnotationAutowireCandidateResolver
extends QualifierAnnotationAutowireCandidateResolver {
    @Override
    @Nullable
    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
        return this.isLazy(descriptor) ? this.buildLazyResolutionProxy(descriptor, beanName) : null;
    }

    protected boolean isLazy(DependencyDescriptor descriptor) {
        Lazy lazy;
        Method method;
        for (Annotation ann : descriptor.getAnnotations()) {
            Lazy lazy2 = AnnotationUtils.getAnnotation(ann, Lazy.class);
            if (lazy2 == null || !lazy2.value()) continue;
            return true;
        }
        MethodParameter methodParam = descriptor.getMethodParameter();
        return methodParam != null && ((method = methodParam.getMethod()) == null || Void.TYPE == method.getReturnType()) && (lazy = AnnotationUtils.getAnnotation(methodParam.getAnnotatedElement(), Lazy.class)) != null && lazy.value();
    }

    protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, final @Nullable String beanName) {
        Assert.state(this.getBeanFactory() instanceof DefaultListableBeanFactory, "BeanFactory needs to be a DefaultListableBeanFactory");
        final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)this.getBeanFactory();
        TargetSource ts = new TargetSource(){

            @Override
            public Class<?> getTargetClass() {
                return descriptor.getDependencyType();
            }

            @Override
            public boolean isStatic() {
                return false;
            }

            @Override
            public Object getTarget() {
                Object target = beanFactory.doResolveDependency(descriptor, beanName, null, null);
                if (target == null) {
                    Class<?> type = this.getTargetClass();
                    if (Map.class == type) {
                        return Collections.emptyMap();
                    }
                    if (List.class == type) {
                        return Collections.emptyList();
                    }
                    if (Set.class == type || Collection.class == type) {
                        return Collections.emptySet();
                    }
                    throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(), "Optional dependency not present for lazy injection point");
                }
                return target;
            }

            @Override
            public void releaseTarget(Object target) {
            }
        };
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetSource(ts);
        Class<?> dependencyType = descriptor.getDependencyType();
        if (dependencyType.isInterface()) {
            pf.addInterface(dependencyType);
        }
        return pf.getProxy(beanFactory.getBeanClassLoader());
    }
}

