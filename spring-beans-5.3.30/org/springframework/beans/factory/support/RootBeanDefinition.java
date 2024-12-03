/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class RootBeanDefinition
extends AbstractBeanDefinition {
    @Nullable
    private BeanDefinitionHolder decoratedDefinition;
    @Nullable
    private AnnotatedElement qualifiedElement;
    volatile boolean stale;
    boolean allowCaching = true;
    boolean isFactoryMethodUnique;
    @Nullable
    volatile ResolvableType targetType;
    @Nullable
    volatile Class<?> resolvedTargetType;
    @Nullable
    volatile Boolean isFactoryBean;
    @Nullable
    volatile ResolvableType factoryMethodReturnType;
    @Nullable
    volatile Method factoryMethodToIntrospect;
    @Nullable
    volatile String resolvedDestroyMethodName;
    final Object constructorArgumentLock = new Object();
    @Nullable
    Executable resolvedConstructorOrFactoryMethod;
    boolean constructorArgumentsResolved = false;
    @Nullable
    Object[] resolvedConstructorArguments;
    @Nullable
    Object[] preparedConstructorArguments;
    final Object postProcessingLock = new Object();
    boolean postProcessed = false;
    @Nullable
    volatile Boolean beforeInstantiationResolved;
    @Nullable
    private Set<Member> externallyManagedConfigMembers;
    @Nullable
    private Set<String> externallyManagedInitMethods;
    @Nullable
    private Set<String> externallyManagedDestroyMethods;

    public RootBeanDefinition() {
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass) {
        this.setBeanClass(beanClass);
    }

    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
        this.setBeanClass(beanClass);
        this.setInstanceSupplier(instanceSupplier);
    }

    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
        this.setBeanClass(beanClass);
        this.setScope(scope);
        this.setInstanceSupplier(instanceSupplier);
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
        this.setBeanClass(beanClass);
        this.setAutowireMode(autowireMode);
        if (dependencyCheck && this.getResolvedAutowireMode() != 3) {
            this.setDependencyCheck(1);
        }
    }

    public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.setBeanClass(beanClass);
    }

    public RootBeanDefinition(String beanClassName) {
        this.setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.setBeanClassName(beanClassName);
    }

    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
        this.decoratedDefinition = original.decoratedDefinition;
        this.qualifiedElement = original.qualifiedElement;
        this.allowCaching = original.allowCaching;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
        this.targetType = original.targetType;
        this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
    }

    RootBeanDefinition(BeanDefinition original) {
        super(original);
    }

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public void setParentName(@Nullable String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }

    public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
        this.decoratedDefinition = decoratedDefinition;
    }

    @Nullable
    public BeanDefinitionHolder getDecoratedDefinition() {
        return this.decoratedDefinition;
    }

    public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
        this.qualifiedElement = qualifiedElement;
    }

    @Nullable
    public AnnotatedElement getQualifiedElement() {
        return this.qualifiedElement;
    }

    public void setTargetType(ResolvableType targetType) {
        this.targetType = targetType;
    }

    public void setTargetType(@Nullable Class<?> targetType) {
        this.targetType = targetType != null ? ResolvableType.forClass(targetType) : null;
    }

    @Nullable
    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        }
        ResolvableType targetType = this.targetType;
        return targetType != null ? targetType.resolve() : null;
    }

    @Override
    public ResolvableType getResolvableType() {
        ResolvableType targetType = this.targetType;
        if (targetType != null) {
            return targetType;
        }
        ResolvableType returnType = this.factoryMethodReturnType;
        if (returnType != null) {
            return returnType;
        }
        Method factoryMethod = this.factoryMethodToIntrospect;
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType((Method)factoryMethod);
        }
        return super.getResolvableType();
    }

    @Nullable
    public Constructor<?>[] getPreferredConstructors() {
        return null;
    }

    public void setUniqueFactoryMethodName(String name) {
        Assert.hasText((String)name, (String)"Factory method name must not be empty");
        this.setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }

    public void setNonUniqueFactoryMethodName(String name) {
        Assert.hasText((String)name, (String)"Factory method name must not be empty");
        this.setFactoryMethodName(name);
        this.isFactoryMethodUnique = false;
    }

    public boolean isFactoryMethod(Method candidate) {
        return candidate.getName().equals(this.getFactoryMethodName());
    }

    public void setResolvedFactoryMethod(@Nullable Method method) {
        this.factoryMethodToIntrospect = method;
    }

    @Nullable
    public Method getResolvedFactoryMethod() {
        return this.factoryMethodToIntrospect;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerExternallyManagedConfigMember(Member configMember) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            if (this.externallyManagedConfigMembers == null) {
                this.externallyManagedConfigMembers = new LinkedHashSet<Member>(1);
            }
            this.externallyManagedConfigMembers.add(configMember);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isExternallyManagedConfigMember(Member configMember) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            return this.externallyManagedConfigMembers != null && this.externallyManagedConfigMembers.contains(configMember);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<Member> getExternallyManagedConfigMembers() {
        Object object = this.postProcessingLock;
        synchronized (object) {
            return this.externallyManagedConfigMembers != null ? Collections.unmodifiableSet(new LinkedHashSet<Member>(this.externallyManagedConfigMembers)) : Collections.emptySet();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerExternallyManagedInitMethod(String initMethod) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            if (this.externallyManagedInitMethods == null) {
                this.externallyManagedInitMethods = new LinkedHashSet<String>(1);
            }
            this.externallyManagedInitMethods.add(initMethod);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isExternallyManagedInitMethod(String initMethod) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            return this.externallyManagedInitMethods != null && this.externallyManagedInitMethods.contains(initMethod);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean hasAnyExternallyManagedInitMethod(String initMethod) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            if (this.isExternallyManagedInitMethod(initMethod)) {
                return true;
            }
            return RootBeanDefinition.hasAnyExternallyManagedMethod(this.externallyManagedInitMethods, initMethod);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> getExternallyManagedInitMethods() {
        Object object = this.postProcessingLock;
        synchronized (object) {
            return this.externallyManagedInitMethods != null ? Collections.unmodifiableSet(new LinkedHashSet<String>(this.externallyManagedInitMethods)) : Collections.emptySet();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerExternallyManagedDestroyMethod(String destroyMethod) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            if (this.externallyManagedDestroyMethods == null) {
                this.externallyManagedDestroyMethods = new LinkedHashSet<String>(1);
            }
            this.externallyManagedDestroyMethods.add(destroyMethod);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            return this.externallyManagedDestroyMethods != null && this.externallyManagedDestroyMethods.contains(destroyMethod);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean hasAnyExternallyManagedDestroyMethod(String destroyMethod) {
        Object object = this.postProcessingLock;
        synchronized (object) {
            if (this.isExternallyManagedDestroyMethod(destroyMethod)) {
                return true;
            }
            return RootBeanDefinition.hasAnyExternallyManagedMethod(this.externallyManagedDestroyMethods, destroyMethod);
        }
    }

    private static boolean hasAnyExternallyManagedMethod(Set<String> candidates, String methodName) {
        if (candidates != null) {
            for (String candidate : candidates) {
                String candidateMethodName;
                int indexOfDot = candidate.lastIndexOf(46);
                if (indexOfDot <= 0 || !(candidateMethodName = candidate.substring(indexOfDot + 1)).equals(methodName)) continue;
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> getExternallyManagedDestroyMethods() {
        Object object = this.postProcessingLock;
        synchronized (object) {
            return this.externallyManagedDestroyMethods != null ? Collections.unmodifiableSet(new LinkedHashSet<String>(this.externallyManagedDestroyMethods)) : Collections.emptySet();
        }
    }

    @Override
    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof RootBeanDefinition && super.equals(other);
    }

    @Override
    public String toString() {
        return "Root bean: " + super.toString();
    }
}

