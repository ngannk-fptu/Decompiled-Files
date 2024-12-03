/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import java.lang.reflect.Array;
import java.util.AbstractList;

class PrimtiveArrayBackedReadOnlyList
extends AbstractList {
    private final Object array;

    PrimtiveArrayBackedReadOnlyList(Object array) {
        this.array = array;
    }

    @Override
    public Object get(int index) {
        return Array.get(this.array, index);
    }

    @Override
    public int size() {
        return Array.getLength(this.array);
    }
}

