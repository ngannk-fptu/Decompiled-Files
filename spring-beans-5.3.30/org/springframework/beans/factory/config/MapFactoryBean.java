/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.beans.factory.config;

import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

public class MapFactoryBean
extends AbstractFactoryBean<Map<Object, Object>> {
    @Nullable
    private Map<?, ?> sourceMap;
    @Nullable
    private Class<? extends Map> targetMapClass;

    public void setSourceMap(Map<?, ?> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public void setTargetMapClass(@Nullable Class<? extends Map> targetMapClass) {
        if (targetMapClass == null) {
            throw new IllegalArgumentException("'targetMapClass' must not be null");
        }
        if (!Map.class.isAssignableFrom(targetMapClass)) {
            throw new IllegalArgumentException("'targetMapClass' must implement [java.util.Map]");
        }
        this.targetMapClass = targetMapClass;
    }

    @Override
    public Class<Map> getObjectType() {
        return Map.class;
    }

    @Override
    protected Map<Object, Object> createInstance() {
        if (this.sourceMap == null) {
            throw new IllegalArgumentException("'sourceMap' is required");
        }
        Map result = null;
        result = this.targetMapClass != null ? BeanUtils.instantiateClass(this.targetMapClass) : CollectionUtils.newLinkedHashMap((int)this.sourceMap.size());
        Class keyType = null;
        Class valueType = null;
        if (this.targetMapClass != null) {
            ResolvableType mapType = ResolvableType.forClass(this.targetMapClass).asMap();
            keyType = mapType.resolveGeneric(new int[]{0});
            valueType = mapType.resolveGeneric(new int[]{1});
        }
        if (keyType != null || valueType != null) {
            TypeConverter converter = this.getBeanTypeConverter();
            for (Map.Entry<?, ?> entry : this.sourceMap.entrySet()) {
                Object convertedKey = converter.convertIfNecessary(entry.getKey(), keyType);
                Object convertedValue = converter.convertIfNecessary(entry.getValue(), valueType);
                result.put(convertedKey, convertedValue);
            }
        } else {
            result.putAll(this.sourceMap);
        }
        return result;
    }
}

