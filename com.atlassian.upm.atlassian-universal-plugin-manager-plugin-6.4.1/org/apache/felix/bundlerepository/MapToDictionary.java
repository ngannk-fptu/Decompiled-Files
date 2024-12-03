/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import org.apache.felix.bundlerepository.IteratorToEnumeration;

public class MapToDictionary
extends Dictionary {
    private Map m_map = null;

    public MapToDictionary(Map map) {
        this.m_map = map;
    }

    public void setSourceMap(Map map) {
        this.m_map = map;
    }

    public Enumeration elements() {
        if (this.m_map == null) {
            return null;
        }
        return new IteratorToEnumeration(this.m_map.values().iterator());
    }

    public Object get(Object key) {
        if (this.m_map == null) {
            return null;
        }
        return this.m_map.get(key);
    }

    public boolean isEmpty() {
        if (this.m_map == null) {
            return true;
        }
        return this.m_map.isEmpty();
    }

    public Enumeration keys() {
        if (this.m_map == null) {
            return null;
        }
        return new IteratorToEnumeration(this.m_map.keySet().iterator());
    }

    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        if (this.m_map == null) {
            return 0;
        }
        return this.m_map.size();
    }
}

