/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.rometools.rome.feed.impl;

import com.rometools.rome.feed.impl.BeanIntrospector;
import com.rometools.rome.feed.impl.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloneableBean {
    private static final Logger LOG = LoggerFactory.getLogger(CloneableBean.class);
    private static final Set<Class<?>> BASIC_TYPES = new HashSet();
    private static final Class<?>[] NO_PARAMS_DEF = new Class[0];
    private static final Object[] NO_PARAMS = new Object[0];

    private CloneableBean() {
    }

    public static Object beanClone(Object obj, Set<String> ignoreProperties) throws CloneNotSupportedException {
        Class<?> clazz = obj.getClass();
        try {
            Object clonedBean = clazz.newInstance();
            List<PropertyDescriptor> propertyDescriptors = BeanIntrospector.getPropertyDescriptorsWithGettersAndSetters(clazz);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                boolean ignoredProperty = ignoreProperties.contains(propertyName);
                if (ignoredProperty) continue;
                Method getter = propertyDescriptor.getReadMethod();
                Method setter = propertyDescriptor.getWriteMethod();
                Object value = getter.invoke(obj, NO_PARAMS);
                if (value == null) continue;
                value = CloneableBean.doClone(value);
                setter.invoke(clonedBean, value);
            }
            return clonedBean;
        }
        catch (CloneNotSupportedException e) {
            LOG.error("Error while cloning bean", (Throwable)e);
            throw e;
        }
        catch (Exception e) {
            LOG.error("Error while cloning bean", (Throwable)e);
            throw new CloneNotSupportedException("Cannot clone a " + clazz + " object");
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static <T> T doClone(T value) throws Exception {
        if (value == null) return (T)value;
        Class<?> vClass = value.getClass();
        if (vClass.isArray()) {
            value = CloneableBean.cloneArray(value);
            return (T)value;
        } else if (value instanceof Collection) {
            value = CloneableBean.cloneCollection((Collection)value);
            return (T)value;
        } else if (value instanceof Map) {
            value = CloneableBean.cloneMap((Map)value);
            return (T)value;
        } else {
            if (CloneableBean.isBasicType(vClass)) return (T)value;
            if (!(value instanceof Cloneable)) throw new CloneNotSupportedException("Cannot clone a " + vClass.getName() + " object");
            Method cloneMethod = vClass.getMethod("clone", NO_PARAMS_DEF);
            if (!Modifier.isPublic(cloneMethod.getModifiers())) throw new CloneNotSupportedException("Cannot clone a " + value.getClass() + " object, clone() is not public");
            value = cloneMethod.invoke(value, NO_PARAMS);
        }
        return (T)value;
    }

    private static <T> T cloneArray(T array) throws Exception {
        Class<?> elementClass = array.getClass().getComponentType();
        int length = Array.getLength(array);
        Object newArray = Array.newInstance(elementClass, length);
        for (int i = 0; i < length; ++i) {
            Array.set(newArray, i, CloneableBean.doClone(Array.get(array, i)));
        }
        return (T)newArray;
    }

    private static <T> Collection<T> cloneCollection(Collection<T> collection) throws Exception {
        Collection<T> newCollection = CloneableBean.newCollection(collection.getClass());
        for (T item : collection) {
            newCollection.add(CloneableBean.doClone(item));
        }
        return newCollection;
    }

    private static <T extends Collection<E>, E> Collection<E> newCollection(Class<T> type) throws InstantiationException, IllegalAccessException {
        Collection collection = SortedSet.class.isAssignableFrom(type) ? new TreeSet() : (Set.class.isAssignableFrom(type) ? new HashSet() : (List.class.isAssignableFrom(type) ? new ArrayList() : (Collection)type.newInstance()));
        return collection;
    }

    private static <K, V> Map<K, V> cloneMap(Map<K, V> map) throws Exception {
        Map<K, V> newMap = CloneableBean.newMap(map.getClass());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K clonedKey = CloneableBean.doClone(entry.getKey());
            V clonedValue = CloneableBean.doClone(entry.getValue());
            newMap.put(clonedKey, clonedValue);
        }
        return newMap;
    }

    private static <T extends Map<K, V>, K, V> Map<K, V> newMap(Class<T> type) throws InstantiationException, IllegalAccessException {
        AbstractMap map = SortedMap.class.isAssignableFrom(type) ? new TreeMap() : new HashMap();
        return map;
    }

    private static boolean isBasicType(Class<?> vClass) {
        return BASIC_TYPES.contains(vClass);
    }

    static {
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(Byte.class);
        BASIC_TYPES.add(Character.class);
        BASIC_TYPES.add(Double.class);
        BASIC_TYPES.add(Float.class);
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(Short.class);
        BASIC_TYPES.add(String.class);
    }
}

