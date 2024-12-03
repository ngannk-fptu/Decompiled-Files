/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class BeanMap
extends AbstractMap<String, Object>
implements Serializable,
Cloneable {
    private final Object bean;
    private transient Set<PropertyDescriptor> descriptors;

    public BeanMap(Object object) throws IntrospectionException {
        if (object == null) {
            throw new IllegalArgumentException("bean == null");
        }
        this.bean = object;
        this.descriptors = BeanMap.initDescriptors(object);
    }

    private static Set<PropertyDescriptor> initDescriptors(Object object) throws IntrospectionException {
        PropertyDescriptor[] propertyDescriptorArray;
        HashSet<PropertyDescriptor> hashSet = new HashSet<PropertyDescriptor>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptorArray = Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
            if ("class".equals(propertyDescriptor.getName()) && propertyDescriptor.getPropertyType() == Class.class || propertyDescriptor instanceof IndexedPropertyDescriptor) continue;
            hashSet.add(propertyDescriptor);
        }
        return Collections.unmodifiableSet(hashSet);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return new BeanSet();
    }

    @Override
    public Object get(Object object) {
        return super.get(object);
    }

    @Override
    public Object put(String string, Object object) {
        this.checkKey(string);
        for (Map.Entry<String, Object> entry : this.entrySet()) {
            if (!entry.getKey().equals(string)) continue;
            return entry.setValue(object);
        }
        return null;
    }

    @Override
    public Object remove(Object object) {
        return super.remove(this.checkKey(object));
    }

    @Override
    public int size() {
        return this.descriptors.size();
    }

    private String checkKey(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("key == null");
        }
        String string = (String)object;
        if (!this.containsKey(string)) {
            throw new IllegalArgumentException("Bad key: " + object);
        }
        return string;
    }

    private Object readResolve() throws IntrospectionException {
        this.descriptors = BeanMap.initDescriptors(this.bean);
        return this;
    }

    private static Object unwrap(Wrapped wrapped) {
        try {
            return wrapped.run();
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            throw (RuntimeException)invocationTargetException.getCause();
        }
    }

    private static interface Wrapped {
        public Object run() throws IllegalAccessException, InvocationTargetException;
    }

    private class BeanEntry
    implements Map.Entry<String, Object> {
        private final PropertyDescriptor mDescriptor;

        public BeanEntry(PropertyDescriptor propertyDescriptor) {
            this.mDescriptor = propertyDescriptor;
        }

        @Override
        public String getKey() {
            return this.mDescriptor.getName();
        }

        @Override
        public Object getValue() {
            return BeanMap.unwrap(new Wrapped(){

                @Override
                public Object run() throws IllegalAccessException, InvocationTargetException {
                    Method method = BeanEntry.this.mDescriptor.getReadMethod();
                    if (method == null) {
                        throw new UnsupportedOperationException("No getter: " + BeanEntry.this.mDescriptor.getName());
                    }
                    return method.invoke(BeanMap.this.bean, new Object[0]);
                }
            });
        }

        @Override
        public Object setValue(final Object object) {
            return BeanMap.unwrap(new Wrapped(){

                @Override
                public Object run() throws IllegalAccessException, InvocationTargetException {
                    Method method = BeanEntry.this.mDescriptor.getWriteMethod();
                    if (method == null) {
                        throw new UnsupportedOperationException("No write method for property: " + BeanEntry.this.mDescriptor.getName());
                    }
                    Object object2 = BeanEntry.this.getValue();
                    method.invoke(BeanMap.this.bean, object);
                    return object2;
                }
            });
        }

        @Override
        public boolean equals(Object object) {
            Object v;
            Object object2;
            Object k;
            if (!(object instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)object;
            String string = this.getKey();
            return (string == (k = entry.getKey()) || string != null && string.equals(k)) && ((object2 = this.getValue()) == (v = entry.getValue()) || object2 != null && object2.equals(v));
        }

        @Override
        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }

    private class BeanIterator
    implements Iterator<Map.Entry<String, Object>> {
        private final Iterator<PropertyDescriptor> mIterator;

        public BeanIterator(Iterator<PropertyDescriptor> iterator) {
            this.mIterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.mIterator.hasNext();
        }

        @Override
        public BeanEntry next() {
            return new BeanEntry(this.mIterator.next());
        }

        @Override
        public void remove() {
            this.mIterator.remove();
        }
    }

    private class BeanSet
    extends AbstractSet<Map.Entry<String, Object>> {
        private BeanSet() {
        }

        @Override
        public Iterator<Map.Entry<String, Object>> iterator() {
            return new BeanIterator(BeanMap.this.descriptors.iterator());
        }

        @Override
        public int size() {
            return BeanMap.this.descriptors.size();
        }
    }
}

