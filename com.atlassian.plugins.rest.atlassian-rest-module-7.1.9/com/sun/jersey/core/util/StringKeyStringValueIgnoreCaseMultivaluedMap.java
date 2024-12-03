/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class StringKeyStringValueIgnoreCaseMultivaluedMap
extends StringKeyIgnoreCaseMultivaluedMap<String> {
    public StringKeyStringValueIgnoreCaseMultivaluedMap() {
    }

    public StringKeyStringValueIgnoreCaseMultivaluedMap(StringKeyStringValueIgnoreCaseMultivaluedMap that) {
        super(that);
    }

    public void putSingleObject(String key, Object value) {
        List l = this.getList(key);
        l.clear();
        if (value != null) {
            l.add(value.toString());
        } else {
            l.add("");
        }
    }

    public void addObject(String key, Object value) {
        List l = this.getList(key);
        if (value != null) {
            l.add(value.toString());
        } else {
            l.add("");
        }
    }

    public <A> List<A> get(String key, Class<A> type) {
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

    public <A> A getFirst(String key, Class<A> type) {
        String value = (String)this.getFirst(key);
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

    public <A> A getFirst(String key, A defaultValue) {
        String value = (String)this.getFirst(key);
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

