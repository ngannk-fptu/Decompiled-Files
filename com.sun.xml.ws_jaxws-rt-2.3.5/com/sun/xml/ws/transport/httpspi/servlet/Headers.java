/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Headers
implements Map<String, List<String>> {
    HashMap<String, List<String>> map = new HashMap(32);

    private String normalize(String key) {
        if (key == null) {
            return null;
        }
        int len = key.length();
        if (len == 0) {
            return key;
        }
        char[] b = key.toCharArray();
        if (b[0] >= 'a' && b[0] <= 'z') {
            b[0] = (char)(b[0] - 32);
        }
        for (int i = 1; i < len; ++i) {
            if (b[i] < 'A' || b[i] > 'Z') continue;
            b[i] = (char)(b[i] + 32);
        }
        String s = new String(b);
        return s;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        if (!(key instanceof String)) {
            return false;
        }
        return this.map.containsKey(this.normalize((String)key));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        return this.map.get(this.normalize((String)key));
    }

    public String getFirst(String key) {
        List<String> l = this.map.get(this.normalize(key));
        if (l == null) {
            return null;
        }
        return l.get(0);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return this.map.put(this.normalize(key), value);
    }

    public void add(String key, String value) {
        String k = this.normalize(key);
        List<String> l = this.map.get(k);
        if (l == null) {
            l = new LinkedList<String>();
            this.map.put(k, l);
        }
        l.add(value);
    }

    public void set(String key, String value) {
        LinkedList<String> l = new LinkedList<String>();
        l.add(value);
        this.put(key, (List<String>)l);
    }

    @Override
    public List<String> remove(Object key) {
        return this.map.remove(this.normalize((String)key));
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> t) {
        for (Map.Entry<? extends String, ? extends List<String>> entry : t.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return this.map.values();
    }

    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.map.equals(o);
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    public String toString() {
        return this.map.toString();
    }
}

