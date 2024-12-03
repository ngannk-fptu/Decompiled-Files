/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import java.util.AbstractList;

class NonPrimitiveArrayBackedReadOnlyList
extends AbstractList {
    private final Object[] array;

    NonPrimitiveArrayBackedReadOnlyList(Object[] array) {
        this.array = array;
    }

    @Override
    public Object get(int index) {
        return this.array[index];
    }

    @Override
    public int size() {
        return this.array.length;
    }
}

