/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.memory;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import com.opensymphony.module.propertyset.DuplicatePropertyKeyException;
import com.opensymphony.module.propertyset.InvalidPropertyTypeException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class MemoryPropertySet
extends AbstractPropertySet {
    private HashMap map;

    public synchronized Collection getKeys(String prefix, int type) {
        Iterator keys = this.getMap().keySet().iterator();
        LinkedList<String> result = new LinkedList<String>();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            if (prefix != null && !key.startsWith(prefix)) continue;
            if (type == 0) {
                result.add(key);
                continue;
            }
            ValueEntry v = (ValueEntry)this.getMap().get(key);
            if (v.type != type) continue;
            result.add(key);
        }
        Collections.sort(result);
        return result;
    }

    public synchronized int getType(String key) {
        if (this.getMap().containsKey(key)) {
            return ((ValueEntry)this.getMap().get((Object)key)).type;
        }
        return 0;
    }

    public synchronized boolean exists(String key) {
        return this.getType(key) > 0;
    }

    public void init(Map config, Map args) {
        this.map = new HashMap();
    }

    public synchronized void remove(String key) {
        this.getMap().remove(key);
    }

    protected synchronized void setImpl(int type, String key, Object value) throws DuplicatePropertyKeyException {
        if (this.exists(key)) {
            ValueEntry v = (ValueEntry)this.getMap().get(key);
            if (v.type != type) {
                throw new DuplicatePropertyKeyException();
            }
            v.value = value;
        } else {
            this.getMap().put(key, new ValueEntry(type, value));
        }
    }

    protected HashMap getMap() {
        return this.map;
    }

    protected synchronized Object get(int type, String key) throws InvalidPropertyTypeException {
        if (this.exists(key)) {
            ValueEntry v = (ValueEntry)this.getMap().get(key);
            if (v.type != type) {
                throw new InvalidPropertyTypeException();
            }
            return v.value;
        }
        return null;
    }

    public static final class ValueEntry
    implements Serializable {
        Object value;
        int type;

        public ValueEntry() {
        }

        public ValueEntry(int type, Object value) {
            this.type = type;
            this.value = value;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

