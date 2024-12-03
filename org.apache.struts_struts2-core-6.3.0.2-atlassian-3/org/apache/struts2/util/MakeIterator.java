/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import org.apache.struts2.util.IteratorFilterSupport;

public class MakeIterator {
    public static boolean isIterable(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Map) {
            return true;
        }
        if (object instanceof Iterable) {
            return true;
        }
        if (object.getClass().isArray()) {
            return true;
        }
        if (object instanceof Enumeration) {
            return true;
        }
        return object instanceof Iterator;
    }

    public static Iterator convert(Object value) {
        Iterator iterator;
        if (value instanceof Iterator) {
            return (Iterator)((Object)value);
        }
        if (value instanceof Map) {
            value = ((Map)((Object)value)).entrySet();
        }
        if (value == null) {
            return null;
        }
        if (value instanceof Iterable) {
            iterator = ((Iterable)value).iterator();
        } else if (value.getClass().isArray()) {
            ArrayList<Object> list = new ArrayList<Object>(Array.getLength(value));
            for (int j = 0; j < Array.getLength(value); ++j) {
                list.add(Array.get(value, j));
            }
            iterator = list.iterator();
        } else {
            iterator = value instanceof Enumeration ? new IteratorFilterSupport.EnumerationIterator((Enumeration)((Object)value)) : Arrays.asList(value).iterator();
        }
        return iterator;
    }
}

