/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopProxyUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.comparator.Comparators
 */
package org.springframework.data.mapping.callback;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.comparator.Comparators;

class EntityCallbackDiscoverer {
    private final CallbackRetriever defaultRetriever = new CallbackRetriever(false);
    private final Map<CallbackCacheKey, CallbackRetriever> retrieverCache = new ConcurrentHashMap<CallbackCacheKey, CallbackRetriever>(64);
    private final Map<Class<?>, ResolvableType> entityTypeCache = new ConcurrentReferenceHashMap(64);
    @Nullable
    private ClassLoader beanClassLoader;
    @Nullable
    private BeanFactory beanFactory;
    private Object retrievalMutex = this.defaultRetriever;

    EntityCallbackDiscoverer() {
    }

    EntityCallbackDiscoverer(BeanFactory beanFactory) {
        this.setBeanFactory(beanFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addEntityCallback(EntityCallback<?> callback) {
        Assert.notNull(callback, (String)"Callback must not be null!");
        Object object = this.retrievalMutex;
        synchronized (object) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(callback);
            if (singletonTarget instanceof EntityCallback) {
                this.defaultRetriever.entityCallbacks.remove(singletonTarget);
            }
            this.defaultRetriever.entityCallbacks.add(callback);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void addEntityCallbackBean(String callbackBeanName) {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.entityCallbackBeans.add(callbackBeanName);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeEntityCallback(EntityCallback<?> callback) {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.entityCallbacks.remove(callback);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeEntityCallbackBean(String callbackBeanName) {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.entityCallbackBeans.remove(callbackBeanName);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clear() {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.entityCallbacks.clear();
            this.defaultRetriever.entityCallbackBeans.clear();
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T extends S, S> Collection<EntityCallback<S>> getEntityCallbacks(Class<T> entity, ResolvableType callbackType) {
        Class<T> sourceType = entity;
        CallbackCacheKey cacheKey = new CallbackCacheKey(callbackType, sourceType);
        CallbackRetriever retriever = this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getEntityCallbacks();
        }
        if (this.beanClassLoader == null || ClassUtils.isCacheSafe(entity.getClass(), (ClassLoader)this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, (ClassLoader)this.beanClassLoader))) {
            Object object = this.retrievalMutex;
            synchronized (object) {
                retriever = this.retrieverCache.get(cacheKey);
                if (retriever != null) {
                    return retriever.getEntityCallbacks();
                }
                retriever = new CallbackRetriever(true);
                Collection<EntityCallback<S>> callbacks = this.retrieveEntityCallbacks(ResolvableType.forClass(sourceType), callbackType, retriever);
                this.retrieverCache.put(cacheKey, retriever);
                return callbacks;
            }
        }
        return this.retrieveEntityCallbacks(callbackType, callbackType, null);
    }

    @Nullable
    ResolvableType resolveDeclaredEntityType(Class<?> callbackType) {
        ResolvableType eventType = this.entityTypeCache.get(callbackType);
        if (eventType == null) {
            eventType = ResolvableType.forClass(callbackType).as(EntityCallback.class).getGeneric(new int[0]);
            this.entityTypeCache.put(callbackType, eventType);
        }
        return eventType != ResolvableType.NONE ? eventType : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<EntityCallback<?>> retrieveEntityCallbacks(ResolvableType entityType, ResolvableType callbackType, @Nullable CallbackRetriever retriever) {
        LinkedHashSet callbackBeans;
        LinkedHashSet callbacks;
        ArrayList allCallbacks = new ArrayList();
        Iterator iterator = this.retrievalMutex;
        synchronized (iterator) {
            callbacks = new LinkedHashSet(this.defaultRetriever.entityCallbacks);
            callbackBeans = new LinkedHashSet(this.defaultRetriever.entityCallbackBeans);
        }
        for (EntityCallback callback : callbacks) {
            if (!this.supportsEvent(callback, entityType, callbackType)) continue;
            if (retriever != null) {
                retriever.getEntityCallbacks().add(callback);
            }
            allCallbacks.add(callback);
        }
        if (!callbackBeans.isEmpty()) {
            BeanFactory beanFactory = this.getRequiredBeanFactory();
            for (String callbackBeanName : callbackBeans) {
                try {
                    EntityCallback callback;
                    Class callbackImplType = beanFactory.getType(callbackBeanName);
                    if (callbackImplType != null && !this.supportsEvent(callbackImplType, entityType) || allCallbacks.contains(callback = (EntityCallback)beanFactory.getBean(callbackBeanName, EntityCallback.class)) || !this.supportsEvent(callback, entityType, callbackType)) continue;
                    if (retriever != null) {
                        if (beanFactory.isSingleton(callbackBeanName)) {
                            retriever.entityCallbacks.add(callback);
                        } else {
                            retriever.entityCallbackBeans.add(callbackBeanName);
                        }
                    }
                    allCallbacks.add(callback);
                }
                catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
            }
        }
        AnnotationAwareOrderComparator.sort(allCallbacks);
        if (retriever != null && retriever.entityCallbackBeans.isEmpty()) {
            retriever.entityCallbacks.clear();
            retriever.entityCallbacks.addAll(allCallbacks);
        }
        return allCallbacks;
    }

    protected boolean supportsEvent(Class<?> callback, ResolvableType entityType) {
        ResolvableType declaredEventType = this.resolveDeclaredEntityType(callback);
        return declaredEventType == null || declaredEventType.isAssignableFrom(entityType);
    }

    protected boolean supportsEvent(EntityCallback<?> callback, ResolvableType entityType, ResolvableType callbackType) {
        return this.supportsEvent(callback.getClass(), entityType) && callbackType.isAssignableFrom(ResolvableType.forInstance(callback));
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)beanFactory;
            if (this.beanClassLoader == null) {
                this.beanClassLoader = cbf.getBeanClassLoader();
            }
            this.retrievalMutex = cbf.getSingletonMutex();
        }
        this.defaultRetriever.discoverEntityCallbacks(this.beanFactory);
        this.retrieverCache.clear();
    }

    @Nullable
    static Method lookupCallbackMethod(Class<?> callbackType, Class<?> entityType, Object[] args) {
        ArrayList methods = new ArrayList(1);
        ReflectionUtils.doWithMethods(callbackType, methods::add, method -> {
            if (!Modifier.isPublic(method.getModifiers()) || method.getParameterCount() != args.length + 1 || method.isBridge() || ReflectionUtils.isObjectMethod((Method)method)) {
                return false;
            }
            return ClassUtils.isAssignable(method.getParameterTypes()[0], (Class)entityType);
        });
        if (methods.size() == 1) {
            return (Method)methods.iterator().next();
        }
        throw new IllegalStateException(String.format("%s does not define a callback method accepting %s and %s additional arguments.", ClassUtils.getShortName(callbackType), ClassUtils.getShortName(entityType), args.length));
    }

    static <T> BiFunction<EntityCallback<T>, T, Object> computeCallbackInvokerFunction(EntityCallback<T> callback, Method callbackMethod, Object[] args) {
        return (entityCallback, entity) -> {
            Object[] invocationArgs = new Object[args.length + 1];
            invocationArgs[0] = entity;
            if (args.length > 0) {
                System.arraycopy(args, 0, invocationArgs, 1, args.length);
            }
            return ReflectionUtils.invokeMethod((Method)callbackMethod, (Object)callback, (Object[])invocationArgs);
        };
    }

    private BeanFactory getRequiredBeanFactory() {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"EntityCallbacks cannot retrieve callback beans because it is not associated with a BeanFactory");
        return this.beanFactory;
    }

    static final class CallbackCacheKey
    implements Comparable<CallbackCacheKey> {
        private final ResolvableType callbackType;
        private final Class<?> entityType;

        public CallbackCacheKey(ResolvableType callbackType, @Nullable Class<?> entityType) {
            Assert.notNull((Object)callbackType, (String)"Callback type must not be null");
            Assert.notNull(entityType, (String)"Entity type must not be null");
            this.callbackType = callbackType;
            this.entityType = entityType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            CallbackCacheKey otherKey = (CallbackCacheKey)other;
            return this.callbackType.equals((Object)otherKey.callbackType) && ObjectUtils.nullSafeEquals(this.entityType, otherKey.entityType);
        }

        public int hashCode() {
            return this.callbackType.hashCode() * 17 + ObjectUtils.nullSafeHashCode(this.entityType);
        }

        @Override
        public int compareTo(CallbackCacheKey other) {
            return Comparators.nullsHigh().thenComparing(it -> this.callbackType.toString()).thenComparing(it -> this.entityType.getName()).compare(this, other);
        }
    }

    class CallbackRetriever {
        private final Set<EntityCallback<?>> entityCallbacks = new LinkedHashSet();
        private final Set<String> entityCallbackBeans = new LinkedHashSet<String>();
        private final boolean preFiltered;

        CallbackRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        Collection<EntityCallback<?>> getEntityCallbacks() {
            ArrayList allCallbacks = new ArrayList(this.entityCallbacks.size() + this.entityCallbackBeans.size());
            allCallbacks.addAll(this.entityCallbacks);
            if (!this.entityCallbackBeans.isEmpty()) {
                BeanFactory beanFactory = EntityCallbackDiscoverer.this.getRequiredBeanFactory();
                for (String callbackBeanName : this.entityCallbackBeans) {
                    try {
                        EntityCallback callback = (EntityCallback)beanFactory.getBean(callbackBeanName, EntityCallback.class);
                        if (!this.preFiltered && allCallbacks.contains(callback)) continue;
                        allCallbacks.add(callback);
                    }
                    catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
                }
            }
            if (!this.preFiltered || !this.entityCallbackBeans.isEmpty()) {
                AnnotationAwareOrderComparator.sort(allCallbacks);
            }
            return allCallbacks;
        }

        void discoverEntityCallbacks(BeanFactory beanFactory) {
            beanFactory.getBeanProvider(EntityCallback.class).stream().forEach(this.entityCallbacks::add);
        }
    }
}

