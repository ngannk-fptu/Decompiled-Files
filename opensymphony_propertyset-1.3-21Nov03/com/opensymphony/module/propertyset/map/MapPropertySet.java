/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.propertyset.map;

import com.opensymphony.module.propertyset.AbstractPropertySet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class MapPropertySet
extends AbstractPropertySet {
    protected Map map;

    public synchronized Collection getKeys(String prefix, int type) {
        Iterator keys = this.map.keySet().iterator();
        LinkedList<String> result = new LinkedList<String>();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            if (prefix != null && !key.startsWith(prefix)) continue;
            result.add(key);
        }
        Collections.sort(result);
        return result;
    }

    public synchronized void setMap(Map map) {
        if (map == null) {
            throw new NullPointerException("Map cannot be null.");
        }
        this.map = map;
    }

    public synchronized Map getMap() {
        return this.map;
    }

    public int getType(String key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("PropertySet does not support types");
    }

    public synchronized boolean exists(String key) {
        return this.map.containsKey(key);
    }

    public void init(Map config, Map args) {
        this.map = (Map)args.get("map");
        if (this.map == null) {
            this.map = new HashMap();
        }
    }

    public synchronized void remove(String key) {
        this.map.remove(key);
    }

    public boolean supportsType(int type) {
        return false;
    }

    public boolean supportsTypes() {
        return false;
    }

    protected synchronized void setImpl(int type, String key, Object value) {
        this.map.put(key, value);
    }

    protected synchronized Object get(int type, String key) {
        return this.exists(key) ? this.map.get(key) : null;
    }
}

