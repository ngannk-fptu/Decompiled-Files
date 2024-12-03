/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.sun.jersey.core.util;

import com.sun.jersey.core.util.KeyComparatorLinkedHashMap;
import com.sun.jersey.core.util.StringIgnoreCaseKeyComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

public class StringKeyIgnoreCaseMultivaluedMap<V>
extends KeyComparatorLinkedHashMap<String, List<V>>
implements MultivaluedMap<String, V> {
    public StringKeyIgnoreCaseMultivaluedMap() {
        super(StringIgnoreCaseKeyComparator.SINGLETON);
    }

    public StringKeyIgnoreCaseMultivaluedMap(StringKeyIgnoreCaseMultivaluedMap<V> that) {
        super(StringIgnoreCaseKeyComparator.SINGLETON);
        for (Map.Entry e : that.entrySet()) {
            this.put(e.getKey(), new ArrayList((Collection)e.getValue()));
        }
    }

    public void putSingle(String key, V value) {
        if (value == null) {
            return;
        }
        List<V> l = this.getList(key);
        l.clear();
        l.add(value);
    }

    public void add(String key, V value) {
        if (value == null) {
            return;
        }
        List<V> l = this.getList(key);
        l.add(value);
    }

    public V getFirst(String key) {
        List values = (List)this.get(key);
        if (values != null && values.size() > 0) {
            return (V)values.get(0);
        }
        return null;
    }

    protected List<V> getList(String key) {
        LinkedList l = (LinkedList)this.get(key);
        if (l == null) {
            l = new LinkedList();
            this.put(key, l);
        }
        return l;
    }
}

