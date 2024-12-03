/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GroovyRowResult
extends GroovyObjectSupport
implements Map {
    private final Map result;

    public GroovyRowResult(Map result) {
        this.result = result;
    }

    @Override
    public Object getProperty(String property) {
        try {
            Object key = this.lookupKeyIgnoringCase(property);
            if (key != null) {
                return this.result.get(key);
            }
            throw new MissingPropertyException(property, GroovyRowResult.class);
        }
        catch (Exception e) {
            throw new MissingPropertyException(property, GroovyRowResult.class, e);
        }
    }

    private Object lookupKeyIgnoringCase(Object key) {
        if (this.result.containsKey(key)) {
            return key;
        }
        if (!(key instanceof CharSequence)) {
            return null;
        }
        String keyStr = key.toString();
        for (Object next : this.result.keySet()) {
            if (!(next instanceof String) || !keyStr.equalsIgnoreCase((String)next)) continue;
            return next;
        }
        return null;
    }

    public Object getAt(int index) {
        try {
            if (index < 0) {
                index += this.result.size();
            }
            Iterator it = this.result.values().iterator();
            int i = 0;
            Object obj = null;
            while (obj == null && it.hasNext()) {
                if (i == index) {
                    obj = it.next();
                } else {
                    it.next();
                }
                ++i;
            }
            return obj;
        }
        catch (Exception e) {
            throw new MissingPropertyException(Integer.toString(index), GroovyRowResult.class, e);
        }
    }

    public String toString() {
        return this.result.toString();
    }

    @Override
    public void clear() {
        this.result.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.lookupKeyIgnoringCase(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.result.containsValue(value);
    }

    public Set<Map.Entry> entrySet() {
        return this.result.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.result.equals(o);
    }

    public Object get(Object property) {
        if (property instanceof String) {
            return this.getProperty((String)property);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return this.result.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.result.isEmpty();
    }

    public Set keySet() {
        return this.result.keySet();
    }

    public Object put(Object key, Object value) {
        Object orig = this.remove(key);
        this.result.put(key, value);
        return orig;
    }

    public void putAll(Map t) {
        for (Map.Entry next : t.entrySet()) {
            this.put(next.getKey(), next.getValue());
        }
    }

    public Object remove(Object rawKey) {
        return this.result.remove(this.lookupKeyIgnoringCase(rawKey));
    }

    @Override
    public int size() {
        return this.result.size();
    }

    public Collection values() {
        return this.result.values();
    }
}

