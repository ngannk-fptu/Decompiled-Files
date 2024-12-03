/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.property;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.property.NullReturn;

public class LocalPropertyStack {
    private final Deque<Map<String, Object>> stack = new LinkedList<Map<String, Object>>();
    private final Object LOCK = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addLocal(String property) {
        Object object = this.LOCK;
        synchronized (object) {
            Map<String, Object> map = this.stack.peek();
            if (map != null) {
                map.put(property, NullReturn.NULL);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void enterScope() {
        Object object = this.LOCK;
        synchronized (object) {
            this.stack.addFirst(new ConcurrentHashMap());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void exitScope() {
        Object object = this.LOCK;
        synchronized (object) {
            this.stack.removeFirst().clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LocalPropertyStack copy() {
        Object object = this.LOCK;
        synchronized (object) {
            LocalPropertyStack ret = new LocalPropertyStack();
            ret.stack.addAll(this.stack);
            return ret;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object evaluate(String property, PropertyHelper helper) {
        Object object = this.LOCK;
        synchronized (object) {
            for (Map<String, Object> map : this.stack) {
                Object ret = map.get(property);
                if (ret == null) continue;
                return ret;
            }
        }
        return null;
    }

    public boolean setNew(String property, Object value, PropertyHelper propertyHelper) {
        Map<String, Object> map = this.getMapForProperty(property);
        if (map == null) {
            return false;
        }
        Object currValue = map.get(property);
        if (currValue == NullReturn.NULL) {
            map.put(property, value);
        }
        return true;
    }

    public boolean set(String property, Object value, PropertyHelper propertyHelper) {
        Map<String, Object> map = this.getMapForProperty(property);
        if (map == null) {
            return false;
        }
        map.put(property, value);
        return true;
    }

    public Set<String> getPropertyNames() {
        Set names = this.stack.stream().map(Map::keySet).collect(Collector.of(HashSet::new, AbstractCollection::addAll, (ns1, ns2) -> {
            ns1.addAll(ns2);
            return ns1;
        }, Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
        return Collections.unmodifiableSet(names);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, Object> getMapForProperty(String property) {
        Object object = this.LOCK;
        synchronized (object) {
            for (Map<String, Object> map : this.stack) {
                if (map.get(property) == null) continue;
                return map;
            }
        }
        return null;
    }
}

