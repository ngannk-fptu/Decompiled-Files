/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.MutableDynaClass;

public abstract class BaseDynaBeanMapDecorator<K>
implements Map<K, Object> {
    private final DynaBean dynaBean;
    private final boolean readOnly;
    private transient Set<K> keySet;

    public BaseDynaBeanMapDecorator(DynaBean dynaBean) {
        this(dynaBean, true);
    }

    public BaseDynaBeanMapDecorator(DynaBean dynaBean, boolean readOnly) {
        if (dynaBean == null) {
            throw new IllegalArgumentException("DynaBean is null");
        }
        this.dynaBean = dynaBean;
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        DynaClass dynaClass = this.getDynaBean().getDynaClass();
        DynaProperty dynaProperty = dynaClass.getDynaProperty(this.toString(key));
        return dynaProperty != null;
    }

    @Override
    public boolean containsValue(Object value) {
        DynaProperty[] properties;
        for (DynaProperty propertie : properties = this.getDynaProperties()) {
            String key = propertie.getName();
            Object prop = this.getDynaBean().get(key);
            if (!(value == null ? prop == null : value.equals(prop))) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<Map.Entry<K, Object>> entrySet() {
        DynaProperty[] properties = this.getDynaProperties();
        HashSet<MapEntry<K>> set = new HashSet<MapEntry<K>>(properties.length);
        for (DynaProperty propertie : properties) {
            K key = this.convertKey(propertie.getName());
            Object value = this.getDynaBean().get(propertie.getName());
            set.add(new MapEntry<K>(key, value));
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Object get(Object key) {
        return this.getDynaBean().get(this.toString(key));
    }

    @Override
    public boolean isEmpty() {
        return this.getDynaProperties().length == 0;
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet != null) {
            return this.keySet;
        }
        DynaProperty[] properties = this.getDynaProperties();
        Set<K> set = new HashSet(properties.length);
        for (DynaProperty propertie : properties) {
            set.add(this.convertKey(propertie.getName()));
        }
        set = Collections.unmodifiableSet(set);
        DynaClass dynaClass = this.getDynaBean().getDynaClass();
        if (!(dynaClass instanceof MutableDynaClass)) {
            this.keySet = set;
        }
        return set;
    }

    @Override
    public Object put(K key, Object value) {
        if (this.isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        String property = this.toString(key);
        Object previous = this.getDynaBean().get(property);
        this.getDynaBean().set(property, value);
        return previous;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Object> map) {
        if (this.isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        for (Map.Entry<K, Object> e : map.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.getDynaProperties().length;
    }

    @Override
    public Collection<Object> values() {
        DynaProperty[] properties = this.getDynaProperties();
        ArrayList<Object> values = new ArrayList<Object>(properties.length);
        for (DynaProperty propertie : properties) {
            String key = propertie.getName();
            Object value = this.getDynaBean().get(key);
            values.add(value);
        }
        return Collections.unmodifiableList(values);
    }

    public DynaBean getDynaBean() {
        return this.dynaBean;
    }

    protected abstract K convertKey(String var1);

    private DynaProperty[] getDynaProperties() {
        return this.getDynaBean().getDynaClass().getDynaProperties();
    }

    private String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    private static class MapEntry<K>
    implements Map.Entry<K, Object> {
        private final K key;
        private final Object value;

        MapEntry(K key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return this.key.equals(e.getKey()) && (this.value == null ? e.getValue() == null : this.value.equals(e.getValue()));
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() + (this.value == null ? 0 : this.value.hashCode());
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }
}

