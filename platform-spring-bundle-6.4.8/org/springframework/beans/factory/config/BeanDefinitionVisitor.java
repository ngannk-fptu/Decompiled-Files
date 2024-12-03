/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringValueResolver;

public class BeanDefinitionVisitor {
    @Nullable
    private StringValueResolver valueResolver;

    public BeanDefinitionVisitor(StringValueResolver valueResolver) {
        Assert.notNull((Object)valueResolver, "StringValueResolver must not be null");
        this.valueResolver = valueResolver;
    }

    protected BeanDefinitionVisitor() {
    }

    public void visitBeanDefinition(BeanDefinition beanDefinition) {
        this.visitParentName(beanDefinition);
        this.visitBeanClassName(beanDefinition);
        this.visitFactoryBeanName(beanDefinition);
        this.visitFactoryMethodName(beanDefinition);
        this.visitScope(beanDefinition);
        if (beanDefinition.hasPropertyValues()) {
            this.visitPropertyValues(beanDefinition.getPropertyValues());
        }
        if (beanDefinition.hasConstructorArgumentValues()) {
            ConstructorArgumentValues cas = beanDefinition.getConstructorArgumentValues();
            this.visitIndexedArgumentValues(cas.getIndexedArgumentValues());
            this.visitGenericArgumentValues(cas.getGenericArgumentValues());
        }
    }

    protected void visitParentName(BeanDefinition beanDefinition) {
        String resolvedName;
        String parentName = beanDefinition.getParentName();
        if (parentName != null && !parentName.equals(resolvedName = this.resolveStringValue(parentName))) {
            beanDefinition.setParentName(resolvedName);
        }
    }

    protected void visitBeanClassName(BeanDefinition beanDefinition) {
        String resolvedName;
        String beanClassName = beanDefinition.getBeanClassName();
        if (beanClassName != null && !beanClassName.equals(resolvedName = this.resolveStringValue(beanClassName))) {
            beanDefinition.setBeanClassName(resolvedName);
        }
    }

    protected void visitFactoryBeanName(BeanDefinition beanDefinition) {
        String resolvedName;
        String factoryBeanName = beanDefinition.getFactoryBeanName();
        if (factoryBeanName != null && !factoryBeanName.equals(resolvedName = this.resolveStringValue(factoryBeanName))) {
            beanDefinition.setFactoryBeanName(resolvedName);
        }
    }

    protected void visitFactoryMethodName(BeanDefinition beanDefinition) {
        String resolvedName;
        String factoryMethodName = beanDefinition.getFactoryMethodName();
        if (factoryMethodName != null && !factoryMethodName.equals(resolvedName = this.resolveStringValue(factoryMethodName))) {
            beanDefinition.setFactoryMethodName(resolvedName);
        }
    }

    protected void visitScope(BeanDefinition beanDefinition) {
        String resolvedScope;
        String scope = beanDefinition.getScope();
        if (scope != null && !scope.equals(resolvedScope = this.resolveStringValue(scope))) {
            beanDefinition.setScope(resolvedScope);
        }
    }

    protected void visitPropertyValues(MutablePropertyValues pvs) {
        PropertyValue[] pvArray;
        for (PropertyValue pv : pvArray = pvs.getPropertyValues()) {
            Object newVal = this.resolveValue(pv.getValue());
            if (ObjectUtils.nullSafeEquals(newVal, pv.getValue())) continue;
            pvs.add(pv.getName(), newVal);
        }
    }

    protected void visitIndexedArgumentValues(Map<Integer, ConstructorArgumentValues.ValueHolder> ias) {
        for (ConstructorArgumentValues.ValueHolder valueHolder : ias.values()) {
            Object newVal = this.resolveValue(valueHolder.getValue());
            if (ObjectUtils.nullSafeEquals(newVal, valueHolder.getValue())) continue;
            valueHolder.setValue(newVal);
        }
    }

    protected void visitGenericArgumentValues(List<ConstructorArgumentValues.ValueHolder> gas) {
        for (ConstructorArgumentValues.ValueHolder valueHolder : gas) {
            Object newVal = this.resolveValue(valueHolder.getValue());
            if (ObjectUtils.nullSafeEquals(newVal, valueHolder.getValue())) continue;
            valueHolder.setValue(newVal);
        }
    }

    @Nullable
    protected Object resolveValue(@Nullable Object value) {
        if (value instanceof BeanDefinition) {
            this.visitBeanDefinition((BeanDefinition)value);
        } else if (value instanceof BeanDefinitionHolder) {
            this.visitBeanDefinition(((BeanDefinitionHolder)value).getBeanDefinition());
        } else if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference)value;
            String newBeanName = this.resolveStringValue(ref.getBeanName());
            if (newBeanName == null) {
                return null;
            }
            if (!newBeanName.equals(ref.getBeanName())) {
                return new RuntimeBeanReference(newBeanName);
            }
        } else if (value instanceof RuntimeBeanNameReference) {
            RuntimeBeanNameReference ref = (RuntimeBeanNameReference)value;
            String newBeanName = this.resolveStringValue(ref.getBeanName());
            if (newBeanName == null) {
                return null;
            }
            if (!newBeanName.equals(ref.getBeanName())) {
                return new RuntimeBeanNameReference(newBeanName);
            }
        } else if (value instanceof Object[]) {
            this.visitArray((Object[])value);
        } else if (value instanceof List) {
            this.visitList((List)value);
        } else if (value instanceof Set) {
            this.visitSet((Set)value);
        } else if (value instanceof Map) {
            this.visitMap((Map)value);
        } else if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue)value;
            String stringValue = typedStringValue.getValue();
            if (stringValue != null) {
                String visitedString = this.resolveStringValue(stringValue);
                typedStringValue.setValue(visitedString);
            }
        } else if (value instanceof String) {
            return this.resolveStringValue((String)value);
        }
        return value;
    }

    protected void visitArray(Object[] arrayVal) {
        for (int i2 = 0; i2 < arrayVal.length; ++i2) {
            Object elem = arrayVal[i2];
            Object newVal = this.resolveValue(elem);
            if (ObjectUtils.nullSafeEquals(newVal, elem)) continue;
            arrayVal[i2] = newVal;
        }
    }

    protected void visitList(List listVal) {
        for (int i2 = 0; i2 < listVal.size(); ++i2) {
            Object elem = listVal.get(i2);
            Object newVal = this.resolveValue(elem);
            if (ObjectUtils.nullSafeEquals(newVal, elem)) continue;
            listVal.set(i2, newVal);
        }
    }

    protected void visitSet(Set setVal) {
        LinkedHashSet<Object> newContent = new LinkedHashSet<Object>();
        boolean entriesModified = false;
        for (Object elem : setVal) {
            int elemHash = elem != null ? elem.hashCode() : 0;
            Object newVal = this.resolveValue(elem);
            int newValHash = newVal != null ? newVal.hashCode() : 0;
            newContent.add(newVal);
            entriesModified = entriesModified || newVal != elem || newValHash != elemHash;
        }
        if (entriesModified) {
            setVal.clear();
            setVal.addAll(newContent);
        }
    }

    protected void visitMap(Map<?, ?> mapVal) {
        LinkedHashMap<Object, Object> newContent = new LinkedHashMap<Object, Object>();
        boolean entriesModified = false;
        for (Map.Entry<?, ?> entry : mapVal.entrySet()) {
            Object key = entry.getKey();
            int keyHash = key != null ? key.hashCode() : 0;
            Object newKey = this.resolveValue(key);
            int newKeyHash = newKey != null ? newKey.hashCode() : 0;
            Object val = entry.getValue();
            Object newVal = this.resolveValue(val);
            newContent.put(newKey, newVal);
            entriesModified = entriesModified || newVal != val || newKey != key || newKeyHash != keyHash;
        }
        if (entriesModified) {
            mapVal.clear();
            mapVal.putAll(newContent);
        }
    }

    @Nullable
    protected String resolveStringValue(String strVal) {
        if (this.valueResolver == null) {
            throw new IllegalStateException("No StringValueResolver specified - pass a resolver object into the constructor or override the 'resolveStringValue' method");
        }
        String resolvedValue = this.valueResolver.resolveStringValue(strVal);
        return strVal.equals(resolvedValue) ? strVal : resolvedValue;
    }
}

