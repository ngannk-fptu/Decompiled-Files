/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.Properties;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class GenericTypeAwareAutowireCandidateResolver
extends SimpleAutowireCandidateResolver
implements BeanFactoryAware,
Cloneable {
    @Nullable
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (!super.isAutowireCandidate(bdHolder, descriptor)) {
            return false;
        }
        return this.checkGenericTypeMatch(bdHolder, descriptor);
    }

    protected boolean checkGenericTypeMatch(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        ResolvableType dependencyType = descriptor.getResolvableType();
        if (dependencyType.getType() instanceof Class) {
            return true;
        }
        ResolvableType targetType = null;
        boolean cacheType = false;
        RootBeanDefinition rbd = null;
        if (bdHolder.getBeanDefinition() instanceof RootBeanDefinition) {
            rbd = (RootBeanDefinition)bdHolder.getBeanDefinition();
        }
        if (rbd != null && (targetType = rbd.targetType) == null) {
            RootBeanDefinition dbd;
            cacheType = true;
            targetType = this.getReturnTypeForFactoryMethod(rbd, descriptor);
            if (targetType == null && (dbd = this.getResolvedDecoratedDefinition(rbd)) != null && (targetType = dbd.targetType) == null) {
                targetType = this.getReturnTypeForFactoryMethod(dbd, descriptor);
            }
        }
        if (targetType == null) {
            Class<?> beanClass;
            Class<?> beanType;
            if (this.beanFactory != null && (beanType = this.beanFactory.getType(bdHolder.getBeanName())) != null) {
                targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanType));
            }
            if (targetType == null && rbd != null && rbd.hasBeanClass() && rbd.getFactoryMethodName() == null && !FactoryBean.class.isAssignableFrom(beanClass = rbd.getBeanClass())) {
                targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanClass));
            }
        }
        if (targetType == null) {
            return true;
        }
        if (cacheType) {
            rbd.targetType = targetType;
        }
        if (descriptor.fallbackMatchAllowed() && (targetType.hasUnresolvableGenerics() || targetType.resolve() == Properties.class)) {
            return true;
        }
        return dependencyType.isAssignableFrom(targetType);
    }

    @Nullable
    protected RootBeanDefinition getResolvedDecoratedDefinition(RootBeanDefinition rbd) {
        BeanDefinition dbd;
        ConfigurableListableBeanFactory clbf;
        BeanDefinitionHolder decDef = rbd.getDecoratedDefinition();
        if (decDef != null && this.beanFactory instanceof ConfigurableListableBeanFactory && (clbf = (ConfigurableListableBeanFactory)this.beanFactory).containsBeanDefinition(decDef.getBeanName()) && (dbd = clbf.getMergedBeanDefinition(decDef.getBeanName())) instanceof RootBeanDefinition) {
            return (RootBeanDefinition)dbd;
        }
        return null;
    }

    @Nullable
    protected ResolvableType getReturnTypeForFactoryMethod(RootBeanDefinition rbd, DependencyDescriptor descriptor) {
        Class<?> resolvedClass;
        Method factoryMethod;
        ResolvableType returnType = rbd.factoryMethodReturnType;
        if (returnType == null && (factoryMethod = rbd.getResolvedFactoryMethod()) != null) {
            returnType = ResolvableType.forMethodReturnType(factoryMethod);
        }
        if (returnType != null && (resolvedClass = returnType.resolve()) != null && descriptor.getDependencyType().isAssignableFrom(resolvedClass)) {
            return returnType;
        }
        return null;
    }

    @Override
    public AutowireCandidateResolver cloneIfNecessary() {
        try {
            return (AutowireCandidateResolver)this.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

