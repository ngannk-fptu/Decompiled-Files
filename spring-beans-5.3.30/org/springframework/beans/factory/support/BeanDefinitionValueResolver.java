/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.NullBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

class BeanDefinitionValueResolver {
    private final AbstractAutowireCapableBeanFactory beanFactory;
    private final String beanName;
    private final BeanDefinition beanDefinition;
    private final TypeConverter typeConverter;

    public BeanDefinitionValueResolver(AbstractAutowireCapableBeanFactory beanFactory, String beanName, BeanDefinition beanDefinition, TypeConverter typeConverter) {
        this.beanFactory = beanFactory;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
        this.typeConverter = typeConverter;
    }

    @Nullable
    public Object resolveValueIfNecessary(Object argName, @Nullable Object value) {
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference)value;
            return this.resolveReference(argName, ref);
        }
        if (value instanceof RuntimeBeanNameReference) {
            String refName = ((RuntimeBeanNameReference)value).getBeanName();
            if (!this.beanFactory.containsBean(refName = String.valueOf(this.doEvaluate(refName)))) {
                throw new BeanDefinitionStoreException("Invalid bean name '" + refName + "' in bean reference for " + argName);
            }
            return refName;
        }
        if (value instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder bdHolder = (BeanDefinitionHolder)value;
            return this.resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
        }
        if (value instanceof BeanDefinition) {
            BeanDefinition bd = (BeanDefinition)value;
            String innerBeanName = "(inner bean)#" + ObjectUtils.getIdentityHexString((Object)bd);
            return this.resolveInnerBean(argName, innerBeanName, bd);
        }
        if (value instanceof DependencyDescriptor) {
            LinkedHashSet autowiredBeanNames = new LinkedHashSet(2);
            Object result = this.beanFactory.resolveDependency((DependencyDescriptor)value, this.beanName, autowiredBeanNames, this.typeConverter);
            for (String autowiredBeanName : autowiredBeanNames) {
                if (!this.beanFactory.containsBean(autowiredBeanName)) continue;
                this.beanFactory.registerDependentBean(autowiredBeanName, this.beanName);
            }
            return result;
        }
        if (value instanceof ManagedArray) {
            ManagedArray array = (ManagedArray)value;
            Class<?> elementType = array.resolvedElementType;
            if (elementType == null) {
                String elementTypeName = array.getElementTypeName();
                if (StringUtils.hasText((String)elementTypeName)) {
                    try {
                        array.resolvedElementType = elementType = ClassUtils.forName((String)elementTypeName, (ClassLoader)this.beanFactory.getBeanClassLoader());
                    }
                    catch (Throwable ex) {
                        throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error resolving array type for " + argName, ex);
                    }
                } else {
                    elementType = Object.class;
                }
            }
            return this.resolveManagedArray(argName, (List)value, elementType);
        }
        if (value instanceof ManagedList) {
            return this.resolveManagedList(argName, (List)value);
        }
        if (value instanceof ManagedSet) {
            return this.resolveManagedSet(argName, (Set)value);
        }
        if (value instanceof ManagedMap) {
            return this.resolveManagedMap(argName, (Map)value);
        }
        if (value instanceof ManagedProperties) {
            Properties original = (Properties)value;
            Properties copy = new Properties();
            original.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(propKey, propValue) -> {
                if (propKey instanceof TypedStringValue) {
                    propKey = this.evaluate((TypedStringValue)propKey);
                }
                if (propValue instanceof TypedStringValue) {
                    propValue = this.evaluate((TypedStringValue)propValue);
                }
                if (propKey == null || propValue == null) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting Properties key/value pair for " + argName + ": resolved to null");
                }
                copy.put(propKey, propValue);
            }));
            return copy;
        }
        if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue)value;
            Object valueObject = this.evaluate(typedStringValue);
            try {
                Class<?> resolvedTargetType = this.resolveTargetType(typedStringValue);
                if (resolvedTargetType != null) {
                    return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
                }
                return valueObject;
            }
            catch (Throwable ex) {
                throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Error converting typed String value for " + argName, ex);
            }
        }
        if (value instanceof NullBean) {
            return null;
        }
        return this.evaluate(value);
    }

    @Nullable
    protected Object evaluate(TypedStringValue value) {
        Object result = this.doEvaluate(value.getValue());
        if (!ObjectUtils.nullSafeEquals((Object)result, (Object)value.getValue())) {
            value.setDynamic();
        }
        return result;
    }

    @Nullable
    protected Object evaluate(@Nullable Object value) {
        if (value instanceof String) {
            return this.doEvaluate((String)value);
        }
        if (value instanceof String[]) {
            String[] values = (String[])value;
            boolean actuallyResolved = false;
            Object[] resolvedValues = new Object[values.length];
            for (int i = 0; i < values.length; ++i) {
                String originalValue = values[i];
                Object resolvedValue = this.doEvaluate(originalValue);
                if (resolvedValue != originalValue) {
                    actuallyResolved = true;
                }
                resolvedValues[i] = resolvedValue;
            }
            return actuallyResolved ? resolvedValues : values;
        }
        return value;
    }

    @Nullable
    private Object doEvaluate(@Nullable String value) {
        return this.beanFactory.evaluateBeanDefinitionString(value, this.beanDefinition);
    }

    @Nullable
    protected Class<?> resolveTargetType(TypedStringValue value) throws ClassNotFoundException {
        if (value.hasTargetType()) {
            return value.getTargetType();
        }
        return value.resolveTargetType(this.beanFactory.getBeanClassLoader());
    }

    @Nullable
    private Object resolveReference(Object argName, RuntimeBeanReference ref) {
        try {
            Object bean;
            Class<?> beanType = ref.getBeanType();
            if (ref.isToParent()) {
                BeanFactory parent = this.beanFactory.getParentBeanFactory();
                if (parent == null) {
                    throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot resolve reference to bean " + ref + " in parent factory: no parent factory available");
                }
                bean = beanType != null ? parent.getBean(beanType) : parent.getBean(String.valueOf(this.doEvaluate(ref.getBeanName())));
            } else {
                String resolvedName;
                if (beanType != null) {
                    NamedBeanHolder namedBean = this.beanFactory.resolveNamedBean(beanType);
                    bean = namedBean.getBeanInstance();
                    resolvedName = namedBean.getBeanName();
                } else {
                    resolvedName = String.valueOf(this.doEvaluate(ref.getBeanName()));
                    bean = this.beanFactory.getBean(resolvedName);
                }
                this.beanFactory.registerDependentBean(resolvedName, this.beanName);
            }
            if (bean instanceof NullBean) {
                bean = null;
            }
            return bean;
        }
        catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, (Throwable)((Object)ex));
        }
    }

    @Nullable
    private Object resolveInnerBean(Object argName, String innerBeanName, BeanDefinition innerBd) {
        RootBeanDefinition mbd = null;
        try {
            Object innerBean;
            mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
            String actualInnerBeanName = innerBeanName;
            if (mbd.isSingleton()) {
                actualInnerBeanName = this.adaptInnerBeanName(innerBeanName);
            }
            this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                for (String dependsOnBean : dependsOn) {
                    this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
                    this.beanFactory.getBean(dependsOnBean);
                }
            }
            if ((innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, null)) instanceof FactoryBean) {
                boolean synthetic = mbd.isSynthetic();
                innerBean = this.beanFactory.getObjectFromFactoryBean((FactoryBean)innerBean, actualInnerBeanName, !synthetic);
            }
            if (innerBean instanceof NullBean) {
                innerBean = null;
            }
            return innerBean;
        }
        catch (BeansException ex) {
            throw new BeanCreationException(this.beanDefinition.getResourceDescription(), this.beanName, "Cannot create inner bean '" + innerBeanName + "' " + (mbd != null && mbd.getBeanClassName() != null ? "of type [" + mbd.getBeanClassName() + "] " : "") + "while setting " + argName, (Throwable)((Object)ex));
        }
    }

    private String adaptInnerBeanName(String innerBeanName) {
        String actualInnerBeanName = innerBeanName;
        int counter = 0;
        String prefix = innerBeanName + "#";
        while (this.beanFactory.isBeanNameInUse(actualInnerBeanName)) {
            actualInnerBeanName = prefix + ++counter;
        }
        return actualInnerBeanName;
    }

    private Object resolveManagedArray(Object argName, List<?> ml, Class<?> elementType) {
        Object resolved = Array.newInstance(elementType, ml.size());
        for (int i = 0; i < ml.size(); ++i) {
            Array.set(resolved, i, this.resolveValueIfNecessary(new KeyedArgName(argName, i), ml.get(i)));
        }
        return resolved;
    }

    private List<?> resolveManagedList(Object argName, List<?> ml) {
        ArrayList<Object> resolved = new ArrayList<Object>(ml.size());
        for (int i = 0; i < ml.size(); ++i) {
            resolved.add(this.resolveValueIfNecessary(new KeyedArgName(argName, i), ml.get(i)));
        }
        return resolved;
    }

    private Set<?> resolveManagedSet(Object argName, Set<?> ms) {
        LinkedHashSet<Object> resolved = new LinkedHashSet<Object>(ms.size());
        int i = 0;
        for (Object m : ms) {
            resolved.add(this.resolveValueIfNecessary(new KeyedArgName(argName, i), m));
            ++i;
        }
        return resolved;
    }

    private Map<?, ?> resolveManagedMap(Object argName, Map<?, ?> mm) {
        LinkedHashMap resolved = CollectionUtils.newLinkedHashMap((int)mm.size());
        mm.forEach((key, value) -> {
            Object resolvedKey = this.resolveValueIfNecessary(argName, key);
            Object resolvedValue = this.resolveValueIfNecessary(new KeyedArgName(argName, key), value);
            resolved.put(resolvedKey, resolvedValue);
        });
        return resolved;
    }

    private static class KeyedArgName {
        private final Object argName;
        private final Object key;

        public KeyedArgName(Object argName, Object key) {
            this.argName = argName;
            this.key = key;
        }

        public String toString() {
            return this.argName + " with key " + "[" + this.key + "]";
        }
    }
}

