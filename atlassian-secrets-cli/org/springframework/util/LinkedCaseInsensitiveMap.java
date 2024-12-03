/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;

public class LinkedCaseInsensitiveMap<V>
implements Map<String, V>,
Serializable,
Cloneable {
    private final LinkedHashMap<String, V> targetMap;
    private final HashMap<String, String> caseInsensitiveKeys;
    private final Locale locale;

    public LinkedCaseInsensitiveMap() {
        this((Locale)null);
    }

    public LinkedCaseInsensitiveMap(@Nullable Locale locale) {
        this(16, locale);
    }

    public LinkedCaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, null);
    }

    public LinkedCaseInsensitiveMap(int initialCapacity, @Nullable Locale locale) {
        this.targetMap = new LinkedHashMap<String, V>(initialCapacity){

            @Override
            public boolean containsKey(Object key) {
                return LinkedCaseInsensitiveMap.this.containsKey(key);
            }

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
                if (doRemove) {
                    LinkedCaseInsensitiveMap.this.caseInsensitiveKeys.remove(LinkedCaseInsensitiveMap.this.convertKey(eldest.getKey()));
                }
                return doRemove;
            }
        };
        this.caseInsensitiveKeys = new HashMap(initialCapacity);
        this.locale = locale != null ? locale : Locale.getDefault();
    }

    private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
        this.targetMap = (LinkedHashMap)other.targetMap.clone();
        this.caseInsensitiveKeys = (HashMap)other.caseInsensitiveKeys.clone();
        this.locale = other.locale;
    }

    @Override
    public int size() {
        return this.targetMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof String && this.caseInsensitiveKeys.containsKey(this.convertKey((String)key));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override
    @Nullable
    public V get(Object key) {
        String caseInsensitiveKey;
        if (key instanceof String && (caseInsensitiveKey = this.caseInsensitiveKeys.get(this.convertKey((String)key))) != null) {
            return this.targetMap.get(caseInsensitiveKey);
        }
        return null;
    }

    @Override
    @Nullable
    public V getOrDefault(Object key, V defaultValue) {
        String caseInsensitiveKey;
        if (key instanceof String && (caseInsensitiveKey = this.caseInsensitiveKeys.get(this.convertKey((String)key))) != null) {
            return this.targetMap.get(caseInsensitiveKey);
        }
        return defaultValue;
    }

    @Override
    @Nullable
    public V put(String key, @Nullable V value) {
        String oldKey = this.caseInsensitiveKeys.put(this.convertKey(key), key);
        if (oldKey != null && !oldKey.equals(key)) {
            this.targetMap.remove(oldKey);
        }
        return this.targetMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        map.forEach(this::put);
    }

    @Override
    @Nullable
    public V remove(Object key) {
        String caseInsensitiveKey;
        if (key instanceof String && (caseInsensitiveKey = this.caseInsensitiveKeys.remove(this.convertKey((String)key))) != null) {
            return this.targetMap.remove(caseInsensitiveKey);
        }
        return null;
    }

    @Override
    public void clear() {
        this.caseInsensitiveKeys.clear();
        this.targetMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.targetMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.targetMap.values();
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return this.targetMap.entrySet();
    }

    public LinkedCaseInsensitiveMap<V> clone() {
        return new LinkedCaseInsensitiveMap<V>(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this.targetMap.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    public String toString() {
        return this.targetMap.toString();
    }

    public Locale getLocale() {
        return this.locale;
    }

    protected String convertKey(String key) {
        return key.toLowerCase(this.getLocale());
    }

    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return false;
    }
}

