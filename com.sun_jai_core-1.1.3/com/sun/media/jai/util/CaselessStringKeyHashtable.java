/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.JaiI18N;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import javax.media.jai.util.CaselessStringKey;

public class CaselessStringKeyHashtable
extends Hashtable
implements Cloneable,
Serializable {
    public CaselessStringKeyHashtable() {
    }

    public CaselessStringKeyHashtable(Map t) {
        super(t);
    }

    public Object clone() {
        return super.clone();
    }

    public boolean containsKey(String key) {
        return super.containsKey(new CaselessStringKey(key));
    }

    public boolean containsKey(CaselessStringKey key) {
        return super.containsKey(key);
    }

    public boolean containsKey(Object key) {
        throw new IllegalArgumentException();
    }

    public Object get(String key) {
        return super.get(new CaselessStringKey(key));
    }

    public Object get(CaselessStringKey key) {
        return super.get(key);
    }

    public Object get(Object key) {
        throw new IllegalArgumentException();
    }

    public Object put(String key, Object value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return super.put(new CaselessStringKey(key), value);
    }

    public Object put(CaselessStringKey key, Object value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return super.put(key, value);
    }

    public Object put(Object key, Object value) {
        throw new IllegalArgumentException();
    }

    public Object remove(String key) {
        return super.remove(new CaselessStringKey(key));
    }

    public Object remove(CaselessStringKey key) {
        return super.remove(key);
    }

    public Object remove(Object key) {
        throw new IllegalArgumentException();
    }
}

