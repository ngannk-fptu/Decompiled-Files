/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

public class MultivaluedMapImpl
extends HashMap<String, List<String>>
implements MultivaluedMap<String, String> {
    static final long serialVersionUID = -6052320403766368902L;

    public MultivaluedMapImpl() {
    }

    public MultivaluedMapImpl(MultivaluedMap<String, String> that) {
        for (Map.Entry e : that.entrySet()) {
            this.put(e.getKey(), new ArrayList((Collection)e.getValue()));
        }
    }

    @Override
    public final void putSingle(String key, String value) {
        List<String> l = this.getList(key);
        l.clear();
        if (value != null) {
            l.add(value);
        } else {
            l.add("");
        }
    }

    @Override
    public final void add(String key, String value) {
        List<String> l = this.getList(key);
        if (value != null) {
            l.add(value);
        } else {
            l.add("");
        }
    }

    @Override
    public final String getFirst(String key) {
        List values = (List)this.get(key);
        if (values != null && values.size() > 0) {
            return (String)values.get(0);
        }
        return null;
    }

    public final void addFirst(String key, String value) {
        List<String> l = this.getList(key);
        if (value != null) {
            l.add(0, value);
        } else {
            l.add(0, "");
        }
    }

    public final <A> List<A> get(String key, Class<A> type) {
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        ArrayList<A> l = null;
        List values = (List)this.get(key);
        if (values != null) {
            l = new ArrayList<A>();
            for (String value : values) {
                try {
                    l.add(c.newInstance(value));
                }
                catch (Exception ex) {
                    l.add(null);
                }
            }
        }
        return l;
    }

    @Override
    public final void putSingle(String key, Object value) {
        List<String> l = this.getList(key);
        l.clear();
        if (value != null) {
            l.add(value.toString());
        } else {
            l.add("");
        }
    }

    @Override
    public final void add(String key, Object value) {
        List<String> l = this.getList(key);
        if (value != null) {
            l.add(value.toString());
        } else {
            l.add("");
        }
    }

    private List<String> getList(String key) {
        LinkedList l = (LinkedList)this.get(key);
        if (l == null) {
            l = new LinkedList();
            this.put(key, l);
        }
        return l;
    }

    public final <A> A getFirst(String key, Class<A> type) {
        String value = this.getFirst(key);
        if (value == null) {
            return null;
        }
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        A retVal = null;
        try {
            retVal = c.newInstance(value);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return retVal;
    }

    public final <A> A getFirst(String key, A defaultValue) {
        String value = this.getFirst(key);
        if (value == null) {
            return defaultValue;
        }
        Class<?> type = defaultValue.getClass();
        Constructor<?> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        Object retVal = defaultValue;
        try {
            retVal = c.newInstance(value);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return retVal;
    }
}

