/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.event.GenericApplicationListenerAdapter;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public abstract class AbstractApplicationEventMulticaster
implements ApplicationEventMulticaster,
BeanClassLoaderAware,
BeanFactoryAware {
    private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);
    final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap<ListenerCacheKey, ListenerRetriever>(64);
    @Nullable
    private ClassLoader beanClassLoader;
    @Nullable
    private BeanFactory beanFactory;
    private Object retrievalMutex = this.defaultRetriever;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)beanFactory;
            if (this.beanClassLoader == null) {
                this.beanClassLoader = cbf.getBeanClassLoader();
            }
            this.retrievalMutex = cbf.getSingletonMutex();
        }
    }

    private BeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans because it is not associated with a BeanFactory");
        }
        return this.beanFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        Object object = this.retrievalMutex;
        synchronized (object) {
            Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
            if (singletonTarget instanceof ApplicationListener) {
                this.defaultRetriever.applicationListeners.remove(singletonTarget);
            }
            this.defaultRetriever.applicationListeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addApplicationListenerBean(String listenerBeanName) {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.applicationListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeApplicationListenerBean(String listenerBeanName) {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAllListeners() {
        Object object = this.retrievalMutex;
        synchronized (object) {
            this.defaultRetriever.applicationListeners.clear();
            this.defaultRetriever.applicationListenerBeans.clear();
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Collection<ApplicationListener<?>> getApplicationListeners() {
        Object object = this.retrievalMutex;
        synchronized (object) {
            return this.defaultRetriever.getApplicationListeners();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event, ResolvableType eventType) {
        Object source = event.getSource();
        Class<?> sourceType = source != null ? source.getClass() : null;
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);
        ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getApplicationListeners();
        }
        if (this.beanClassLoader == null || ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader))) {
            Object object = this.retrievalMutex;
            synchronized (object) {
                retriever = this.retrieverCache.get(cacheKey);
                if (retriever != null) {
                    return retriever.getApplicationListeners();
                }
                retriever = new ListenerRetriever(true);
                Collection<ApplicationListener<?>> listeners = this.retrieveApplicationListeners(eventType, sourceType, retriever);
                this.retrieverCache.put(cacheKey, retriever);
                return listeners;
            }
        }
        return this.retrieveApplicationListeners(eventType, sourceType, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable ListenerRetriever retriever) {
        LinkedHashSet<String> listenerBeans;
        LinkedHashSet listeners;
        ArrayList allListeners = new ArrayList();
        Iterator iterator = this.retrievalMutex;
        synchronized (iterator) {
            listeners = new LinkedHashSet(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet<String>(this.defaultRetriever.applicationListenerBeans);
        }
        for (ApplicationListener applicationListener : listeners) {
            if (!this.supportsEvent(applicationListener, eventType, sourceType)) continue;
            if (retriever != null) {
                retriever.applicationListeners.add(applicationListener);
            }
            allListeners.add(applicationListener);
        }
        if (!listenerBeans.isEmpty()) {
            BeanFactory beanFactory = this.getBeanFactory();
            for (String listenerBeanName : listenerBeans) {
                try {
                    ApplicationListener listener;
                    Class<?> listenerType = beanFactory.getType(listenerBeanName);
                    if (listenerType != null && !this.supportsEvent(listenerType, eventType) || allListeners.contains(listener = beanFactory.getBean(listenerBeanName, ApplicationListener.class)) || !this.supportsEvent(listener, eventType, sourceType)) continue;
                    if (retriever != null) {
                        if (beanFactory.isSingleton(listenerBeanName)) {
                            retriever.applicationListeners.add(listener);
                        } else {
                            retriever.applicationListenerBeans.add(listenerBeanName);
                        }
                    }
                    allListeners.add(listener);
                }
                catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
            }
        }
        AnnotationAwareOrderComparator.sort(allListeners);
        if (retriever != null && retriever.applicationListenerBeans.isEmpty()) {
            retriever.applicationListeners.clear();
            retriever.applicationListeners.addAll(allListeners);
        }
        return allListeners;
    }

    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        if (GenericApplicationListener.class.isAssignableFrom(listenerType) || SmartApplicationListener.class.isAssignableFrom(listenerType)) {
            return true;
        }
        ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
        return declaredEventType == null || declaredEventType.isAssignableFrom(eventType);
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
        GenericApplicationListener smartListener = listener instanceof GenericApplicationListener ? (GenericApplicationListener)listener : new GenericApplicationListenerAdapter(listener);
        return smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType);
    }

    private class ListenerRetriever {
        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
        public final Set<String> applicationListenerBeans = new LinkedHashSet<String>();
        private final boolean preFiltered;

        public ListenerRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            ArrayList allListeners = new ArrayList(this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                for (String listenerBeanName : this.applicationListenerBeans) {
                    try {
                        ApplicationListener listener = beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (!this.preFiltered && allListeners.contains(listener)) continue;
                        allListeners.add(listener);
                    }
                    catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
                }
            }
            if (!this.preFiltered || !this.applicationListenerBeans.isEmpty()) {
                AnnotationAwareOrderComparator.sort(allListeners);
            }
            return allListeners;
        }
    }

    private static final class ListenerCacheKey
    implements Comparable<ListenerCacheKey> {
        private final ResolvableType eventType;
        @Nullable
        private final Class<?> sourceType;

        public ListenerCacheKey(ResolvableType eventType, @Nullable Class<?> sourceType) {
            Assert.notNull((Object)eventType, "Event type must not be null");
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            ListenerCacheKey otherKey = (ListenerCacheKey)other;
            return this.eventType.equals(otherKey.eventType) && ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType);
        }

        public int hashCode() {
            return this.eventType.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.sourceType);
        }

        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType + "]";
        }

        @Override
        public int compareTo(ListenerCacheKey other) {
            int result = this.eventType.toString().compareTo(other.eventType.toString());
            if (result == 0) {
                if (this.sourceType == null) {
                    return other.sourceType == null ? 0 : -1;
                }
                if (other.sourceType == null) {
                    return 1;
                }
                result = this.sourceType.getName().compareTo(other.sourceType.getName());
            }
            return result;
        }
    }
}

