/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.util.internal;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;

public class ServiceReferenceBasedMap
extends AbstractMap {
    private ServiceReference reference;
    private static final String READ_ONLY_MSG = "this is a readonly map";

    public ServiceReferenceBasedMap(ServiceReference ref) {
        Assert.notNull((Object)ref);
        this.reference = ref;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(READ_ONLY_MSG);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        Assert.notNull((Object)value);
        String[] keys = this.reference.getPropertyKeys();
        for (int i = 0; i < keys.length; ++i) {
            if (!value.equals(this.reference.getProperty(keys[i]))) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set entrySet() {
        String[] keys = this.reference.getPropertyKeys();
        LinkedHashSet<SimpleEntry> entrySet = new LinkedHashSet<SimpleEntry>(keys.length);
        for (int i = 0; i < keys.length; ++i) {
            entrySet.add(new SimpleEntry(keys[i], this.reference.getProperty(keys[i])));
        }
        return Collections.unmodifiableSet(entrySet);
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            return this.reference.getProperty((String)key);
        }
        throw new IllegalArgumentException("only String keys are allowed");
    }

    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException(READ_ONLY_MSG);
    }

    @Override
    public void putAll(Map t) {
        throw new UnsupportedOperationException(READ_ONLY_MSG);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException(READ_ONLY_MSG);
    }

    private static class SimpleEntry
    implements Map.Entry {
        Object key;
        Object value;

        public SimpleEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public Object setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return this.eq(this.key, e.getKey()) && this.eq(this.value, e.getValue());
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        private boolean eq(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }
    }
}

