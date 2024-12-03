/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.style;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.style.ValueStyler;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class DefaultValueStyler
implements ValueStyler {
    private static final String EMPTY = "[empty]";
    private static final String NULL = "[null]";
    private static final String COLLECTION = "collection";
    private static final String SET = "set";
    private static final String LIST = "list";
    private static final String MAP = "map";
    private static final String ARRAY = "array";

    @Override
    public String style(@Nullable Object value) {
        if (value == null) {
            return NULL;
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value instanceof Class) {
            return ClassUtils.getShortName((Class)value);
        }
        if (value instanceof Method) {
            Method method = (Method)value;
            return method.getName() + "@" + ClassUtils.getShortName(method.getDeclaringClass());
        }
        if (value instanceof Map) {
            return this.style((Map)value);
        }
        if (value instanceof Map.Entry) {
            return this.style((Map.Entry)value);
        }
        if (value instanceof Collection) {
            return this.style((Collection)value);
        }
        if (value.getClass().isArray()) {
            return this.styleArray(ObjectUtils.toObjectArray(value));
        }
        return String.valueOf(value);
    }

    private <K, V> String style(Map<K, V> value) {
        StringBuilder result = new StringBuilder(value.size() * 8 + 16);
        result.append("map[");
        Iterator<Map.Entry<K, V>> it = value.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            result.append(this.style(entry));
            if (!it.hasNext()) continue;
            result.append(',').append(' ');
        }
        if (value.isEmpty()) {
            result.append(EMPTY);
        }
        result.append("]");
        return result.toString();
    }

    private String style(Map.Entry<?, ?> value) {
        return this.style(value.getKey()) + " -> " + this.style(value.getValue());
    }

    private String style(Collection<?> value) {
        StringBuilder result = new StringBuilder(value.size() * 8 + 16);
        result.append(this.getCollectionTypeString(value)).append('[');
        Iterator<?> i = value.iterator();
        while (i.hasNext()) {
            result.append(this.style(i.next()));
            if (!i.hasNext()) continue;
            result.append(',').append(' ');
        }
        if (value.isEmpty()) {
            result.append(EMPTY);
        }
        result.append("]");
        return result.toString();
    }

    private String getCollectionTypeString(Collection<?> value) {
        if (value instanceof List) {
            return LIST;
        }
        if (value instanceof Set) {
            return SET;
        }
        return COLLECTION;
    }

    private String styleArray(Object[] array) {
        StringBuilder result = new StringBuilder(array.length * 8 + 16);
        result.append("array<").append(ClassUtils.getShortName(array.getClass().getComponentType())).append(">[");
        for (int i = 0; i < array.length - 1; ++i) {
            result.append(this.style(array[i]));
            result.append(',').append(' ');
        }
        if (array.length > 0) {
            result.append(this.style(array[array.length - 1]));
        } else {
            result.append(EMPTY);
        }
        result.append("]");
        return result.toString();
    }
}

