/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.OrderComparator
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class StaticListableBeanFactory
implements ListableBeanFactory {
    private final Map<String, Object> beans;

    public StaticListableBeanFactory() {
        this.beans = new LinkedHashMap<String, Object>();
    }

    public StaticListableBeanFactory(Map<String, Object> beans) {
        Assert.notNull(beans, (String)"Beans Map must not be null");
        this.beans = beans;
    }

    public void addBean(String name, Object bean) {
        this.beans.put(name, bean);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (BeanFactoryUtils.isFactoryDereference(name) && !(bean instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, bean.getClass());
        }
        if (bean instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
            try {
                Object exposedObject = ((FactoryBean)bean).getObject();
                if (exposedObject == null) {
                    throw new BeanCreationException(beanName, "FactoryBean exposed null object");
                }
                return exposedObject;
            }
            catch (Exception ex) {
                throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
            }
        }
        return bean;
    }

    @Override
    public <T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException {
        Object bean = this.getBean(name);
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
        return (T)bean;
    }

    @Override
    public Object getBean(String name, Object ... args) throws BeansException {
        if (!ObjectUtils.isEmpty((Object[])args)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return this.getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        String[] beanNames = this.getBeanNamesForType(requiredType);
        if (beanNames.length == 1) {
            return this.getBean(beanNames[0], requiredType);
        }
        if (beanNames.length > 1) {
            throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
        }
        throw new NoSuchBeanDefinitionException(requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object ... args) throws BeansException {
        if (!ObjectUtils.isEmpty((Object[])args)) {
            throw new UnsupportedOperationException("StaticListableBeanFactory does not support explicit bean creation arguments");
        }
        return this.getBean(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeansException {
        return this.getBeanProvider(ResolvableType.forRawClass(requiredType), true);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return this.getBeanProvider(requiredType, true);
    }

    @Override
    public boolean containsBean(String name) {
        return this.beans.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        Object bean = this.getBean(name);
        if (bean instanceof FactoryBean) {
            return ((FactoryBean)bean).isSingleton();
        }
        return true;
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        Object bean = this.getBean(name);
        return bean instanceof SmartFactoryBean && ((SmartFactoryBean)bean).isPrototype() || bean instanceof FactoryBean && !((FactoryBean)bean).isSingleton();
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = this.getType(name);
        return type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override
    public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = this.getType(name);
        return typeToMatch == null || type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.getType(name, true);
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        String beanName = BeanFactoryUtils.transformedBeanName(name);
        Object bean = this.beans.get(beanName);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(beanName, "Defined beans are [" + StringUtils.collectionToCommaDelimitedString(this.beans.keySet()) + "]");
        }
        if (bean instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
            return ((FactoryBean)bean).getObjectType();
        }
        return bean.getClass();
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beans.containsKey(name);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beans.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beans.keySet());
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        return this.getBeanProvider(ResolvableType.forRawClass(requiredType), allowEagerInit);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, boolean allowEagerInit) {
        return new ObjectProvider<T>(){

            @Override
            public T getObject() throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return StaticListableBeanFactory.this.getBean(beanNames[0], requiredType);
                }
                if (beanNames.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override
            public T getObject(Object ... args) throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return StaticListableBeanFactory.this.getBean(beanNames[0], args);
                }
                if (beanNames.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
                }
                throw new NoSuchBeanDefinitionException(requiredType);
            }

            @Override
            @Nullable
            public T getIfAvailable() throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return StaticListableBeanFactory.this.getBean(beanNames[0]);
                }
                if (beanNames.length > 1) {
                    throw new NoUniqueBeanDefinitionException(requiredType, beanNames);
                }
                return null;
            }

            @Override
            @Nullable
            public T getIfUnique() throws BeansException {
                String[] beanNames = StaticListableBeanFactory.this.getBeanNamesForType(requiredType);
                if (beanNames.length == 1) {
                    return StaticListableBeanFactory.this.getBean(beanNames[0]);
                }
                return null;
            }

            @Override
            public Stream<T> stream() {
                return Arrays.stream(StaticListableBeanFactory.this.getBeanNamesForType(requiredType)).map(name -> StaticListableBeanFactory.this.getBean((String)name));
            }

            @Override
            public Stream<T> orderedStream() {
                return this.stream().sorted((Comparator)OrderComparator.INSTANCE);
            }
        };
    }

    @Override
    public String[] getBeanNamesForType(@Nullable ResolvableType type) {
        return this.getBeanNamesForType(type, true, true);
    }

    @Override
    public String[] getBeanNamesForType(@Nullable ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        Class resolved = type != null ? type.resolve() : null;
        boolean isFactoryType = resolved != null && FactoryBean.class.isAssignableFrom(resolved);
        ArrayList<String> matches = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (beanInstance instanceof FactoryBean && !isFactoryType) {
                FactoryBean factoryBean = (FactoryBean)beanInstance;
                Class<?> objectType = factoryBean.getObjectType();
                if (!includeNonSingletons && !factoryBean.isSingleton() || objectType == null || type != null && !type.isAssignableFrom(objectType)) continue;
                matches.add(beanName);
                continue;
            }
            if (type != null && !type.isInstance(beanInstance)) continue;
            matches.add(beanName);
        }
        return StringUtils.toStringArray(matches);
    }

    @Override
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        return this.getBeanNamesForType(ResolvableType.forClass(type));
    }

    @Override
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return this.getBeanNamesForType(ResolvableType.forClass(type), includeNonSingletons, allowEagerInit);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return this.getBeansOfType(type, true, true);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        boolean isFactoryType = type != null && FactoryBean.class.isAssignableFrom(type);
        LinkedHashMap<String, Object> matches = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
            String beanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (beanInstance instanceof FactoryBean && !isFactoryType) {
                FactoryBean factory = (FactoryBean)beanInstance;
                Class<?> objectType = factory.getObjectType();
                if (!includeNonSingletons && !factory.isSingleton() || objectType == null || type != null && !type.isAssignableFrom(objectType)) continue;
                matches.put(beanName, this.getBean(beanName, type));
                continue;
            }
            if (type != null && !type.isInstance(beanInstance)) continue;
            if (isFactoryType) {
                beanName = "&" + beanName;
            }
            matches.put(beanName, beanInstance);
        }
        return matches;
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        ArrayList<String> results = new ArrayList<String>();
        for (String beanName : this.beans.keySet()) {
            if (this.findAnnotationOnBean(beanName, annotationType) == null) continue;
            results.add(beanName);
        }
        return StringUtils.toStringArray(results);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        LinkedHashMap<String, Object> results = new LinkedHashMap<String, Object>();
        for (String beanName : this.beans.keySet()) {
            if (this.findAnnotationOnBean(beanName, annotationType) == null) continue;
            results.put(beanName, this.getBean(beanName));
        }
        return results;
    }

    @Override
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return this.findAnnotationOnBean(beanName, annotationType, true);
    }

    @Override
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        Class<?> beanType = this.getType(beanName, allowFactoryBeanInit);
        return (A)(beanType != null ? AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType) : null);
    }
}

