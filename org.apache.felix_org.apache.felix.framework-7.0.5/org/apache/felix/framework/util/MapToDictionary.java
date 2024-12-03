/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

public class MapToDictionary
extends Dictionary {
    private Map m_map = null;

    public MapToDictionary(Map map) {
        if (map == null) {
            throw new IllegalArgumentException("Source map cannot be null.");
        }
        this.m_map = map;
    }

    public Enumeration elements() {
        return Collections.enumeration(this.m_map.values());
    }

    public Object get(Object key) {
        return this.m_map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.m_map.isEmpty();
    }

    public Enumeration keys() {
        return Collections.enumeration(this.m_map.keySet());
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.m_map.size();
    }

    public String toString() {
        return this.m_map.toString();
    }
}

