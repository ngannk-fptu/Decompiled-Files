/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopProxyUtils
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.context.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
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
    private final DefaultListenerRetriever defaultRetriever = new DefaultListenerRetriever();
    final Map<ListenerCacheKey, CachedListenerRetriever> retrieverCache = new ConcurrentHashMap<ListenerCacheKey, CachedListenerRetriever>(64);
    @Nullable
    private ClassLoader beanClassLoader;
    @Nullable
    private ConfigurableBeanFactory beanFactory;

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
        if (this.beanClassLoader == null) {
            this.beanClassLoader = this.beanFactory.getBeanClassLoader();
        }
    }

    private ConfigurableBeanFactory getBeanFactory() {
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
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
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
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            this.defaultRetriever.applicationListeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeApplicationListenerBean(String listenerBeanName) {
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeApplicationListeners(Predicate<ApplicationListener<?>> predicate) {
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            this.defaultRetriever.applicationListeners.removeIf(predicate);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeApplicationListenerBeans(Predicate<String> predicate) {
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            this.defaultRetriever.applicationListenerBeans.removeIf(predicate);
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAllListeners() {
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            this.defaultRetriever.applicationListeners.clear();
            this.defaultRetriever.applicationListenerBeans.clear();
            this.retrieverCache.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Collection<ApplicationListener<?>> getApplicationListeners() {
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            return this.defaultRetriever.getApplicationListeners();
        }
    }

    protected Collection<ApplicationListener<?>> getApplicationListeners(ApplicationEvent event, ResolvableType eventType) {
        Collection<ApplicationListener<?>> result;
        Object source = event.getSource();
        Class<?> sourceType = source != null ? source.getClass() : null;
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);
        CachedListenerRetriever newRetriever = null;
        CachedListenerRetriever existingRetriever = this.retrieverCache.get(cacheKey);
        if (existingRetriever == null && (this.beanClassLoader == null || ClassUtils.isCacheSafe(event.getClass(), (ClassLoader)this.beanClassLoader) && (sourceType == null || ClassUtils.isCacheSafe(sourceType, (ClassLoader)this.beanClassLoader))) && (existingRetriever = this.retrieverCache.putIfAbsent(cacheKey, newRetriever = new CachedListenerRetriever())) != null) {
            newRetriever = null;
        }
        if (existingRetriever != null && (result = existingRetriever.getApplicationListeners()) != null) {
            return result;
        }
        return this.retrieveApplicationListeners(eventType, sourceType, newRetriever);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<ApplicationListener<?>> retrieveApplicationListeners(ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable CachedListenerRetriever retriever) {
        LinkedHashSet<String> listenerBeans;
        LinkedHashSet listeners;
        ArrayList allListeners = new ArrayList();
        LinkedHashSet filteredListeners = retriever != null ? new LinkedHashSet() : null;
        LinkedHashSet<String> filteredListenerBeans = retriever != null ? new LinkedHashSet<String>() : null;
        DefaultListenerRetriever defaultListenerRetriever = this.defaultRetriever;
        synchronized (defaultListenerRetriever) {
            listeners = new LinkedHashSet(this.defaultRetriever.applicationListeners);
            listenerBeans = new LinkedHashSet<String>(this.defaultRetriever.applicationListenerBeans);
        }
        for (ApplicationListener applicationListener : listeners) {
            if (!this.supportsEvent(applicationListener, eventType, sourceType)) continue;
            if (retriever != null) {
                filteredListeners.add(applicationListener);
            }
            allListeners.add(applicationListener);
        }
        if (!listenerBeans.isEmpty()) {
            ConfigurableBeanFactory beanFactory = this.getBeanFactory();
            for (String listenerBeanName : listenerBeans) {
                try {
                    Object listener;
                    if (this.supportsEvent(beanFactory, listenerBeanName, eventType)) {
                        listener = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (allListeners.contains(listener) || !this.supportsEvent((ApplicationListener<?>)listener, eventType, sourceType)) continue;
                        if (retriever != null) {
                            if (beanFactory.isSingleton(listenerBeanName)) {
                                filteredListeners.add((ApplicationListener<?>)listener);
                            } else {
                                filteredListenerBeans.add(listenerBeanName);
                            }
                        }
                        allListeners.add((ApplicationListener<?>)listener);
                        continue;
                    }
                    listener = beanFactory.getSingleton(listenerBeanName);
                    if (retriever != null) {
                        filteredListeners.remove(listener);
                    }
                    allListeners.remove(listener);
                }
                catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
            }
        }
        AnnotationAwareOrderComparator.sort(allListeners);
        if (retriever != null) {
            if (filteredListenerBeans.isEmpty()) {
                retriever.applicationListeners = new LinkedHashSet(allListeners);
                retriever.applicationListenerBeans = filteredListenerBeans;
            } else {
                retriever.applicationListeners = filteredListeners;
                retriever.applicationListenerBeans = filteredListenerBeans;
            }
        }
        return allListeners;
    }

    private boolean supportsEvent(ConfigurableBeanFactory beanFactory, String listenerBeanName, ResolvableType eventType) {
        Class listenerType = beanFactory.getType(listenerBeanName);
        if (listenerType == null || GenericApplicationListener.class.isAssignableFrom(listenerType) || SmartApplicationListener.class.isAssignableFrom(listenerType)) {
            return true;
        }
        if (!this.supportsEvent(listenerType, eventType)) {
            return false;
        }
        try {
            BeanDefinition bd = beanFactory.getMergedBeanDefinition(listenerBeanName);
            ResolvableType genericEventType = bd.getResolvableType().as(ApplicationListener.class).getGeneric(new int[0]);
            return genericEventType == ResolvableType.NONE || genericEventType.isAssignableFrom(eventType);
        }
        catch (NoSuchBeanDefinitionException ex) {
            return true;
        }
    }

    protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
        ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
        return declaredEventType == null || declaredEventType.isAssignableFrom(eventType);
    }

    protected boolean supportsEvent(ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {
        GenericApplicationListener smartListener = listener instanceof GenericApplicationListener ? (GenericApplicationListener)listener : new GenericApplicationListenerAdapter(listener);
        return smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType);
    }

    private class DefaultListenerRetriever {
        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
        public final Set<String> applicationListenerBeans = new LinkedHashSet<String>();

        private DefaultListenerRetriever() {
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            ArrayList allListeners = new ArrayList(this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                ConfigurableBeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                for (String listenerBeanName : this.applicationListenerBeans) {
                    try {
                        ApplicationListener listener = (ApplicationListener)beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (allListeners.contains(listener)) continue;
                        allListeners.add(listener);
                    }
                    catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
                }
            }
            AnnotationAwareOrderComparator.sort(allListeners);
            return allListeners;
        }
    }

    private class CachedListenerRetriever {
        @Nullable
        public volatile Set<ApplicationListener<?>> applicationListeners;
        @Nullable
        public volatile Set<String> applicationListenerBeans;

        private CachedListenerRetriever() {
        }

        @Nullable
        public Collection<ApplicationListener<?>> getApplicationListeners() {
            Set<ApplicationListener<?>> applicationListeners = this.applicationListeners;
            Set<String> applicationListenerBeans = this.applicationListenerBeans;
            if (applicationListeners == null || applicationListenerBeans == null) {
                return null;
            }
            ArrayList allListeners = new ArrayList(applicationListeners.size() + applicationListenerBeans.size());
            allListeners.addAll(applicationListeners);
            if (!applicationListenerBeans.isEmpty()) {
                ConfigurableBeanFactory beanFactory = AbstractApplicationEventMulticaster.this.getBeanFactory();
                for (String listenerBeanName : applicationListenerBeans) {
                    try {
                        allListeners.add((ApplicationListener<?>)beanFactory.getBean(listenerBeanName, ApplicationListener.class));
                    }
                    catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {}
                }
            }
            if (!applicationListenerBeans.isEmpty()) {
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
            Assert.notNull((Object)eventType, (String)"Event type must not be null");
            this.eventType = eventType;
            this.sourceType = sourceType;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ListenerCacheKey)) {
                return false;
            }
            ListenerCacheKey otherKey = (ListenerCacheKey)other;
            return this.eventType.equals((Object)otherKey.eventType) && ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType);
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

