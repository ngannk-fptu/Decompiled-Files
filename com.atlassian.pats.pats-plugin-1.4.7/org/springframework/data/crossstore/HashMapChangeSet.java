/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.crossstore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.crossstore.ChangeSet;
import org.springframework.lang.Nullable;

public class HashMapChangeSet
implements ChangeSet {
    private final Map<String, Object> values;

    public HashMapChangeSet(Map<String, Object> values) {
        this.values = values;
    }

    public HashMapChangeSet() {
        this(new HashMap<String, Object>());
    }

    @Override
    public void set(String key, Object o) {
        this.values.put(key, o);
    }

    public String toString() {
        return "HashMapChangeSet: values=[" + this.values + "]";
    }

    @Override
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    @Override
    @Nullable
    public Object removeProperty(String k) {
        return this.values.remove(k);
    }

    @Override
    @Nullable
    public <T> T get(String key, Class<T> requiredClass, ConversionService conversionService) {
        Object value = this.values.get(key);
        if (value == null) {
            return null;
        }
        return (T)conversionService.convert(value, requiredClass);
    }
}

