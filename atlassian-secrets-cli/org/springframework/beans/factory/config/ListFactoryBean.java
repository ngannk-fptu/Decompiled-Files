/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public class ListFactoryBean
extends AbstractFactoryBean<List<Object>> {
    @Nullable
    private List<?> sourceList;
    @Nullable
    private Class<? extends List> targetListClass;

    public void setSourceList(List<?> sourceList) {
        this.sourceList = sourceList;
    }

    public void setTargetListClass(@Nullable Class<? extends List> targetListClass) {
        if (targetListClass == null) {
            throw new IllegalArgumentException("'targetListClass' must not be null");
        }
        if (!List.class.isAssignableFrom(targetListClass)) {
            throw new IllegalArgumentException("'targetListClass' must implement [java.util.List]");
        }
        this.targetListClass = targetListClass;
    }

    @Override
    public Class<List> getObjectType() {
        return List.class;
    }

    @Override
    protected List<Object> createInstance() {
        if (this.sourceList == null) {
            throw new IllegalArgumentException("'sourceList' is required");
        }
        ArrayList<Object> result = null;
        result = this.targetListClass != null ? BeanUtils.instantiateClass(this.targetListClass) : new ArrayList<Object>(this.sourceList.size());
        Class<?> valueType = null;
        if (this.targetListClass != null) {
            valueType = ResolvableType.forClass(this.targetListClass).asCollection().resolveGeneric(new int[0]);
        }
        if (valueType != null) {
            TypeConverter converter = this.getBeanTypeConverter();
            for (Object elem : this.sourceList) {
                result.add(converter.convertIfNecessary(elem, valueType));
            }
        } else {
            result.addAll(this.sourceList);
        }
        return result;
    }
}

