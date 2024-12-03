/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.PriorityOrdered
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.beans.factory.annotation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class InitDestroyAnnotationBeanPostProcessor
implements DestructionAwareBeanPostProcessor,
MergedBeanDefinitionPostProcessor,
PriorityOrdered,
Serializable {
    private final transient LifecycleMetadata emptyLifecycleMetadata = new LifecycleMetadata((Class)Object.class, (Collection)Collections.emptyList(), (Collection)Collections.emptyList()){

        @Override
        public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        }

        @Override
        public void invokeInitMethods(Object target, String beanName) {
        }

        @Override
        public void invokeDestroyMethods(Object target, String beanName) {
        }

        @Override
        public boolean hasDestroyMethods() {
            return false;
        }
    };
    protected transient Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Class<? extends Annotation> initAnnotationType;
    @Nullable
    private Class<? extends Annotation> destroyAnnotationType;
    private int order = Integer.MAX_VALUE;
    @Nullable
    private final transient Map<Class<?>, LifecycleMetadata> lifecycleMetadataCache = new ConcurrentHashMap(256);

    public void setInitAnnotationType(Class<? extends Annotation> initAnnotationType) {
        this.initAnnotationType = initAnnotationType;
    }

    public void setDestroyAnnotationType(Class<? extends Annotation> destroyAnnotationType) {
        this.destroyAnnotationType = destroyAnnotationType;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        LifecycleMetadata metadata = this.findLifecycleMetadata(beanType);
        metadata.checkConfigMembers(beanDefinition);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        LifecycleMetadata metadata = this.findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeInitMethods(bean, beanName);
        }
        catch (InvocationTargetException ex) {
            throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Failed to invoke init method", ex);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        LifecycleMetadata metadata = this.findLifecycleMetadata(bean.getClass());
        try {
            metadata.invokeDestroyMethods(bean, beanName);
        }
        catch (InvocationTargetException ex) {
            String msg = "Destroy method on bean with name '" + beanName + "' threw an exception";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn((Object)msg, ex.getTargetException());
            } else {
                this.logger.warn((Object)(msg + ": " + ex.getTargetException()));
            }
        }
        catch (Throwable ex) {
            this.logger.warn((Object)("Failed to invoke destroy method on bean with name '" + beanName + "'"), ex);
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return this.findLifecycleMetadata(bean.getClass()).hasDestroyMethods();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private LifecycleMetadata findLifecycleMetadata(Class<?> clazz) {
        if (this.lifecycleMetadataCache == null) {
            return this.buildLifecycleMetadata(clazz);
        }
        LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
        if (metadata == null) {
            Map<Class<?>, LifecycleMetadata> map = this.lifecycleMetadataCache;
            synchronized (map) {
                metadata = this.lifecycleMetadataCache.get(clazz);
                if (metadata == null) {
                    metadata = this.buildLifecycleMetadata(clazz);
                    this.lifecycleMetadataCache.put(clazz, metadata);
                }
                return metadata;
            }
        }
        return metadata;
    }

    private LifecycleMetadata buildLifecycleMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, Arrays.asList(this.initAnnotationType, this.destroyAnnotationType))) {
            return this.emptyLifecycleMetadata;
        }
        ArrayList<LifecycleElement> initMethods = new ArrayList<LifecycleElement>();
        ArrayList<LifecycleElement> destroyMethods = new ArrayList<LifecycleElement>();
        Class<?> targetClass = clazz;
        do {
            ArrayList currInitMethods = new ArrayList();
            ArrayList currDestroyMethods = new ArrayList();
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                if (this.initAnnotationType != null && method.isAnnotationPresent(this.initAnnotationType)) {
                    LifecycleElement element = new LifecycleElement(method);
                    currInitMethods.add(element);
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Found init method on class [" + clazz.getName() + "]: " + method));
                    }
                }
                if (this.destroyAnnotationType != null && method.isAnnotationPresent(this.destroyAnnotationType)) {
                    currDestroyMethods.add(new LifecycleElement(method));
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace((Object)("Found destroy method on class [" + clazz.getName() + "]: " + method));
                    }
                }
            });
            initMethods.addAll(0, currInitMethods);
            destroyMethods.addAll(currDestroyMethods);
        } while ((targetClass = targetClass.getSuperclass()) != null && targetClass != Object.class);
        return initMethods.isEmpty() && destroyMethods.isEmpty() ? this.emptyLifecycleMetadata : new LifecycleMetadata(clazz, initMethods, destroyMethods);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.logger = LogFactory.getLog(this.getClass());
    }

    private static class LifecycleElement {
        private final Method method;
        private final String identifier;

        public LifecycleElement(Method method) {
            if (method.getParameterCount() != 0) {
                throw new IllegalStateException("Lifecycle method annotation requires a no-arg method: " + method);
            }
            this.method = method;
            this.identifier = Modifier.isPrivate(method.getModifiers()) ? ClassUtils.getQualifiedMethodName((Method)method) : method.getName();
        }

        public Method getMethod() {
            return this.method;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public void invoke(Object target) throws Throwable {
            ReflectionUtils.makeAccessible((Method)this.method);
            this.method.invoke(target, (Object[])null);
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof LifecycleElement)) {
                return false;
            }
            LifecycleElement otherElement = (LifecycleElement)other;
            return this.identifier.equals(otherElement.identifier);
        }

        public int hashCode() {
            return this.identifier.hashCode();
        }
    }

    private class LifecycleMetadata {
        private final Class<?> targetClass;
        private final Collection<LifecycleElement> initMethods;
        private final Collection<LifecycleElement> destroyMethods;
        @Nullable
        private volatile Set<LifecycleElement> checkedInitMethods;
        @Nullable
        private volatile Set<LifecycleElement> checkedDestroyMethods;

        public LifecycleMetadata(Class<?> targetClass, Collection<LifecycleElement> initMethods, Collection<LifecycleElement> destroyMethods) {
            this.targetClass = targetClass;
            this.initMethods = initMethods;
            this.destroyMethods = destroyMethods;
        }

        public void checkConfigMembers(RootBeanDefinition beanDefinition) {
            LinkedHashSet<LifecycleElement> checkedInitMethods = new LinkedHashSet<LifecycleElement>(this.initMethods.size());
            for (LifecycleElement element : this.initMethods) {
                String methodIdentifier = element.getIdentifier();
                if (beanDefinition.isExternallyManagedInitMethod(methodIdentifier)) continue;
                beanDefinition.registerExternallyManagedInitMethod(methodIdentifier);
                checkedInitMethods.add(element);
                if (!InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) continue;
                InitDestroyAnnotationBeanPostProcessor.this.logger.trace((Object)("Registered init method on class [" + this.targetClass.getName() + "]: " + methodIdentifier));
            }
            LinkedHashSet<LifecycleElement> checkedDestroyMethods = new LinkedHashSet<LifecycleElement>(this.destroyMethods.size());
            for (LifecycleElement element : this.destroyMethods) {
                String methodIdentifier = element.getIdentifier();
                if (beanDefinition.isExternallyManagedDestroyMethod(methodIdentifier)) continue;
                beanDefinition.registerExternallyManagedDestroyMethod(methodIdentifier);
                checkedDestroyMethods.add(element);
                if (!InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) continue;
                InitDestroyAnnotationBeanPostProcessor.this.logger.trace((Object)("Registered destroy method on class [" + this.targetClass.getName() + "]: " + methodIdentifier));
            }
            this.checkedInitMethods = checkedInitMethods;
            this.checkedDestroyMethods = checkedDestroyMethods;
        }

        public void invokeInitMethods(Object target, String beanName) throws Throwable {
            Collection<LifecycleElement> initMethodsToIterate;
            Set<LifecycleElement> checkedInitMethods = this.checkedInitMethods;
            Collection<LifecycleElement> collection = initMethodsToIterate = checkedInitMethods != null ? checkedInitMethods : this.initMethods;
            if (!initMethodsToIterate.isEmpty()) {
                for (LifecycleElement element : initMethodsToIterate) {
                    if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.trace((Object)("Invoking init method on bean '" + beanName + "': " + element.getMethod()));
                    }
                    element.invoke(target);
                }
            }
        }

        public void invokeDestroyMethods(Object target, String beanName) throws Throwable {
            Collection<LifecycleElement> destroyMethodsToUse;
            Set<LifecycleElement> checkedDestroyMethods = this.checkedDestroyMethods;
            Collection<LifecycleElement> collection = destroyMethodsToUse = checkedDestroyMethods != null ? checkedDestroyMethods : this.destroyMethods;
            if (!destroyMethodsToUse.isEmpty()) {
                for (LifecycleElement element : destroyMethodsToUse) {
                    if (InitDestroyAnnotationBeanPostProcessor.this.logger.isTraceEnabled()) {
                        InitDestroyAnnotationBeanPostProcessor.this.logger.trace((Object)("Invoking destroy method on bean '" + beanName + "': " + element.getMethod()));
                    }
                    element.invoke(target);
                }
            }
        }

        public boolean hasDestroyMethods() {
            Set<LifecycleElement> checkedDestroyMethods = this.checkedDestroyMethods;
            Collection<LifecycleElement> destroyMethodsToUse = checkedDestroyMethods != null ? checkedDestroyMethods : this.destroyMethods;
            return !destroyMethodsToUse.isEmpty();
        }
    }
}

