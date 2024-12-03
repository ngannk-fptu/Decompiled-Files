/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.lang.reflect.Array;
import java.util.AbstractList;

public class ArrayListWrapper
extends AbstractList {
    private Object array;

    public ArrayListWrapper(Object array) {
        this.array = array;
    }

    public Object get(int index) {
        return Array.get(this.array, index);
    }

    public Object set(int index, Object element) {
        Object old = this.get(index);
        Array.set(this.array, index, element);
        return old;
    }

    public int size() {
        return Array.getLength(this.array);
    }
}

