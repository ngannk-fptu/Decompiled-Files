/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.rometools.rome.feed.impl;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.BeanIntrospector;
import com.rometools.rome.feed.impl.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyFromHelper {
    private static final Logger LOG = LoggerFactory.getLogger(CopyFromHelper.class);
    private static final Set<Class<?>> BASIC_TYPES = new HashSet();
    private static final Object[] NO_PARAMS = new Object[0];
    private final Class<? extends CopyFrom> beanInterfaceClass;
    private final Map<String, Class<?>> baseInterfaceMap;
    private final Map<Class<? extends CopyFrom>, Class<?>> baseImplMap;

    public CopyFromHelper(Class<? extends CopyFrom> beanInterfaceClass, Map<String, Class<?>> basePropInterfaceMap, Map<Class<? extends CopyFrom>, Class<?>> basePropClassImplMap) {
        this.beanInterfaceClass = beanInterfaceClass;
        this.baseInterfaceMap = basePropInterfaceMap;
        this.baseImplMap = basePropClassImplMap;
    }

    public void copy(Object target, Object source) {
        try {
            List<PropertyDescriptor> propertyDescriptors = BeanIntrospector.getPropertyDescriptorsWithGettersAndSetters(this.beanInterfaceClass);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                Method getter;
                Object value;
                String propertyName = propertyDescriptor.getName();
                if (!this.baseInterfaceMap.containsKey(propertyName) || (value = (getter = propertyDescriptor.getReadMethod()).invoke(source, NO_PARAMS)) == null) continue;
                Method setter = propertyDescriptor.getWriteMethod();
                Class<?> baseInterface = this.baseInterfaceMap.get(propertyName);
                value = this.doCopy(value, baseInterface);
                setter.invoke(target, value);
            }
        }
        catch (Exception e) {
            LOG.error("Error while copying object", (Throwable)e);
            throw new RuntimeException("Could not do a copyFrom " + e, e);
        }
    }

    private CopyFrom createInstance(Class<? extends CopyFrom> interfaceClass) throws Exception {
        if (this.baseImplMap.get(interfaceClass) == null) {
            return null;
        }
        return (CopyFrom)this.baseImplMap.get(interfaceClass).newInstance();
    }

    private <T> T doCopy(T value, Class<?> baseInterface) throws Exception {
        if (value != null) {
            Class<?> vClass = value.getClass();
            if (vClass.isArray()) {
                value = this.doCopyArray((Object[])value, baseInterface);
            } else if (value instanceof Collection) {
                value = this.doCopyCollection((Collection)value, baseInterface);
            } else if (value instanceof Map) {
                value = this.doCopyMap((Map)value, baseInterface);
            } else if (this.isBasicType(vClass)) {
                if (value instanceof Date) {
                    value = ((Date)value).clone();
                }
            } else if (value instanceof CopyFrom) {
                CopyFrom source = (CopyFrom)value;
                CopyFrom target = this.createInstance(source.getInterface());
                if (target == null) {
                    target = (CopyFrom)value.getClass().newInstance();
                }
                target.copyFrom(source);
                value = target;
            } else {
                throw new Exception("unsupported class for 'copyFrom' " + value.getClass());
            }
        }
        return (T)value;
    }

    private <T> T[] doCopyArray(T[] array, Class<?> baseInterface) throws Exception {
        Class<?> elementClass = array.getClass().getComponentType();
        int length = Array.getLength(array);
        Object[] newArray = (Object[])Array.newInstance(elementClass, length);
        for (int i = 0; i < length; ++i) {
            Object element = this.doCopy(Array.get(array, i), baseInterface);
            Array.set(newArray, i, element);
        }
        return newArray;
    }

    private <T> Collection<T> doCopyCollection(Collection<T> collection, Class<?> baseInterface) throws Exception {
        AbstractCollection newCollection = collection instanceof Set ? new LinkedHashSet() : new ArrayList();
        for (T item : collection) {
            T copied = this.doCopy(item, baseInterface);
            newCollection.add(copied);
        }
        return newCollection;
    }

    private <S, T> Map<S, T> doCopyMap(Map<S, T> map, Class<?> baseInterface) throws Exception {
        HashMap<S, T> newMap = new HashMap<S, T>();
        for (Map.Entry<S, T> entry : map.entrySet()) {
            T copiedValue = this.doCopy(entry.getValue(), baseInterface);
            newMap.put(entry.getKey(), copiedValue);
        }
        return newMap;
    }

    private boolean isBasicType(Class<?> type) {
        return BASIC_TYPES.contains(type);
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
        BASIC_TYPES.add(Date.class);
    }
}

