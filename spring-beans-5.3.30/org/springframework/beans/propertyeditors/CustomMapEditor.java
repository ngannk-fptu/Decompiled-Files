/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class CustomMapEditor
extends PropertyEditorSupport {
    private final Class<? extends Map> mapType;
    private final boolean nullAsEmptyMap;

    public CustomMapEditor(Class<? extends Map> mapType) {
        this(mapType, false);
    }

    public CustomMapEditor(Class<? extends Map> mapType, boolean nullAsEmptyMap) {
        Assert.notNull(mapType, (String)"Map type is required");
        if (!Map.class.isAssignableFrom(mapType)) {
            throw new IllegalArgumentException("Map type [" + mapType.getName() + "] does not implement [java.util.Map]");
        }
        this.mapType = mapType;
        this.nullAsEmptyMap = nullAsEmptyMap;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.setValue(text);
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value == null && this.nullAsEmptyMap) {
            super.setValue(this.createMap(this.mapType, 0));
        } else if (value == null || this.mapType.isInstance(value) && !this.alwaysCreateNewMap()) {
            super.setValue(value);
        } else if (value instanceof Map) {
            Map source = (Map)value;
            Map<Object, Object> target = this.createMap(this.mapType, source.size());
            source.forEach((key, val) -> target.put(this.convertKey(key), this.convertValue(val)));
            super.setValue(target);
        } else {
            throw new IllegalArgumentException("Value cannot be converted to Map: " + value);
        }
    }

    protected Map<Object, Object> createMap(Class<? extends Map> mapType, int initialCapacity) {
        if (!mapType.isInterface()) {
            try {
                return (Map)ReflectionUtils.accessibleConstructor(mapType, (Class[])new Class[0]).newInstance(new Object[0]);
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Could not instantiate map class: " + mapType.getName(), ex);
            }
        }
        if (SortedMap.class == mapType) {
            return new TreeMap<Object, Object>();
        }
        return new LinkedHashMap<Object, Object>(initialCapacity);
    }

    protected boolean alwaysCreateNewMap() {
        return false;
    }

    protected Object convertKey(Object key) {
        return key;
    }

    protected Object convertValue(Object value) {
        return value;
    }

    @Override
    @Nullable
    public String getAsText() {
        return null;
    }
}

