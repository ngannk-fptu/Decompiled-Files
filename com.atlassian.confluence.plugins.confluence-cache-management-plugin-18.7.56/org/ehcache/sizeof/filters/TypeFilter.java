/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof.filters;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.ehcache.sizeof.filters.SizeOfFilter;
import org.ehcache.sizeof.util.WeakIdentityConcurrentMap;

public class TypeFilter
implements SizeOfFilter {
    private final WeakIdentityConcurrentMap<Class<?>, Object> classesIgnored = new WeakIdentityConcurrentMap();
    private final WeakIdentityConcurrentMap<Class<?>, Object> superClasses = new WeakIdentityConcurrentMap();
    private final WeakIdentityConcurrentMap<Class<?>, ConcurrentMap<Field, Object>> fieldsIgnored = new WeakIdentityConcurrentMap();

    @Override
    public Collection<Field> filterFields(Class<?> klazz, Collection<Field> fields) {
        ConcurrentMap<Field, Object> fieldsToIgnore = this.fieldsIgnored.get(klazz);
        if (fieldsToIgnore != null) {
            fields.removeIf(fieldsToIgnore::containsKey);
        }
        return fields;
    }

    @Override
    public boolean filterClass(Class<?> klazz) {
        if (!this.classesIgnored.containsKey(klazz)) {
            for (Class<?> aClass : this.superClasses.keySet()) {
                if (!aClass.isAssignableFrom(klazz)) continue;
                this.classesIgnored.put(klazz, this);
                return false;
            }
            return true;
        }
        return false;
    }

    public void addClass(Class<?> classToFilterOut, boolean strict) {
        if (!strict) {
            this.superClasses.putIfAbsent(classToFilterOut, this);
        } else {
            this.classesIgnored.put(classToFilterOut, this);
        }
    }

    public void addField(Field fieldToFilterOut) {
        ConcurrentMap<Field, Object> previous;
        Class<?> klazz = fieldToFilterOut.getDeclaringClass();
        ConcurrentMap<Field, Object> fields = this.fieldsIgnored.get(klazz);
        if (fields == null && (previous = this.fieldsIgnored.putIfAbsent(klazz, fields = new ConcurrentHashMap<Field, Object>())) != null) {
            fields = previous;
        }
        fields.put(fieldToFilterOut, this);
    }
}

