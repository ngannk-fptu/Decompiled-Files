/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.reference.CollapsedReference;
import com.atlassian.confluence.api.model.reference.EmptyReference;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ExperimentalApi
public abstract class Reference<T>
implements Iterable<T> {
    @Deprecated
    public Reference() {
        throw new IllegalStateException("use package protected constructor");
    }

    Reference(boolean dummy) {
    }

    public abstract T get() throws IllegalStateException;

    public abstract boolean exists();

    public abstract boolean isExpanded();

    public abstract Map<Object, Object> getIdProperties();

    public Object getIdProperty(Enum key) {
        return this.getIdProperties().get(key);
    }

    public boolean existsAndExpanded() {
        return this.exists() && this.isExpanded();
    }

    public abstract Class<? extends T> referentClass();

    public static <T> Reference<T> empty(Class<T> referrentClass) {
        return new EmptyReference<T>(referrentClass);
    }

    public static <T> Reference<T> orEmpty(Reference<T> reference, Class<T> referentClass) {
        if (reference != null && reference.exists()) {
            return reference;
        }
        return Reference.empty(referentClass);
    }

    public static <T> Reference<T> orEmpty(T entity, Class<T> referentClass) {
        if (entity != null) {
            return Reference.to(entity);
        }
        return Reference.empty(referentClass);
    }

    public static <T> Reference<T> collapsed(T obj) {
        Objects.requireNonNull(obj);
        return new CollapsedReference<T>(obj);
    }

    public static <T> Reference<T> collapsed(Class<T> objClass) {
        Objects.requireNonNull(objClass);
        return new CollapsedReference<T>(objClass, true);
    }

    public static <T> Reference<T> collapsed(Class<T> objClass, Map idProperties) {
        return new CollapsedReference<T>(objClass, idProperties);
    }

    public static <T> Reference<T> to(T value) {
        Objects.requireNonNull(value);
        return new ExpandedReference<T>(value);
    }

    static Map<Object, Object> resolveIdProps(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }
        Class<?> idEnum = null;
        for (Class<?> clazz : obj.getClass().getDeclaredClasses()) {
            if (!clazz.isEnum() || !clazz.getSimpleName().equals("IdProperties")) continue;
            idEnum = clazz;
            break;
        }
        if (idEnum == null) {
            throw new IllegalStateException("Cannot create reference to " + obj.getClass() + ".  Model class requires an enum named 'IdProperties' specifying what fields comprise the id properties for the object");
        }
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        for (Enum enumConstant : (Enum[])idEnum.getEnumConstants()) {
            try {
                Field field = Reference.getFieldFromClassOrParentClasses(obj.getClass(), enumConstant.toString());
                if (field == null) {
                    throw new IllegalStateException("IdProperty : " + enumConstant.toString() + " does not exist as a field on " + obj.getClass());
                }
                field.setAccessible(true);
                map.put(enumConstant, field.get(obj));
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    private static Field getFieldFromClassOrParentClasses(Class clazz, String field) {
        while (clazz != null) {
            for (Field classField : clazz.getDeclaredFields()) {
                if (!classField.getName().equals(field)) continue;
                return classField;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reference)) {
            return false;
        }
        Reference that = (Reference)o;
        if (this.exists() != that.exists()) {
            return false;
        }
        if (!this.getIdProperties().equals(that.getIdProperties())) {
            return false;
        }
        return this.referentClass().equals(that.referentClass());
    }

    public int hashCode() {
        int result = this.getIdProperties().hashCode();
        result = 31 * result + this.referentClass().hashCode();
        return result;
    }
}

