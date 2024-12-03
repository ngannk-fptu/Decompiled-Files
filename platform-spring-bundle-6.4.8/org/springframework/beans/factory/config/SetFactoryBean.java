/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public class SetFactoryBean
extends AbstractFactoryBean<Set<Object>> {
    @Nullable
    private Set<?> sourceSet;
    @Nullable
    private Class<? extends Set> targetSetClass;

    public void setSourceSet(Set<?> sourceSet) {
        this.sourceSet = sourceSet;
    }

    public void setTargetSetClass(@Nullable Class<? extends Set> targetSetClass) {
        if (targetSetClass == null) {
            throw new IllegalArgumentException("'targetSetClass' must not be null");
        }
        if (!Set.class.isAssignableFrom(targetSetClass)) {
            throw new IllegalArgumentException("'targetSetClass' must implement [java.util.Set]");
        }
        this.targetSetClass = targetSetClass;
    }

    @Override
    public Class<Set> getObjectType() {
        return Set.class;
    }

    @Override
    protected Set<Object> createInstance() {
        if (this.sourceSet == null) {
            throw new IllegalArgumentException("'sourceSet' is required");
        }
        LinkedHashSet<Object> result = null;
        result = this.targetSetClass != null ? BeanUtils.instantiateClass(this.targetSetClass) : new LinkedHashSet<Object>(this.sourceSet.size());
        Class<?> valueType = null;
        if (this.targetSetClass != null) {
            valueType = ResolvableType.forClass(this.targetSetClass).asCollection().resolveGeneric(new int[0]);
        }
        if (valueType != null) {
            TypeConverter converter = this.getBeanTypeConverter();
            for (Object elem : this.sourceSet) {
                result.add(converter.convertIfNecessary(elem, valueType));
            }
        } else {
            result.addAll(this.sourceSet);
        }
        return result;
    }
}

