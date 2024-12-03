/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import java.util.ArrayList;
import java.util.List;

public class StringKeyObjectValueIgnoreCaseMultivaluedMap
extends StringKeyIgnoreCaseMultivaluedMap<Object> {
    public StringKeyObjectValueIgnoreCaseMultivaluedMap() {
    }

    public StringKeyObjectValueIgnoreCaseMultivaluedMap(StringKeyObjectValueIgnoreCaseMultivaluedMap that) {
        super(that);
    }

    public <A> List<A> get(String key, Class<A> type) {
        ArrayList l = null;
        List values = (List)this.get(key);
        if (values != null) {
            l = new ArrayList();
            for (Object value : values) {
                if (type.isInstance(value)) {
                    l.add(value);
                    continue;
                }
                throw new IllegalArgumentException(type + " is not an instance of " + value.getClass());
            }
        }
        return l;
    }

    public <A> A getFirst(String key, Class<A> type) {
        Object value = this.getFirst(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (A)value;
        }
        throw new IllegalArgumentException(type + " is not an instance of " + value.getClass());
    }

    public <A> A getFirst(String key, A defaultValue) {
        Object value = this.getFirst(key);
        if (value == null) {
            return defaultValue;
        }
        if (defaultValue.getClass().isInstance(value)) {
            return (A)value;
        }
        throw new IllegalArgumentException(defaultValue.getClass() + " is not an instance of " + value.getClass());
    }
}

