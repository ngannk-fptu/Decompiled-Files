/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

public final class SimpleSet
implements Cloneable {
    public Object[] values;
    public int elementSize;
    public int threshold;

    public SimpleSet() {
        this(13);
    }

    public SimpleSet(int size) {
        if (size < 3) {
            size = 3;
        }
        this.elementSize = 0;
        this.threshold = size + 1;
        this.values = new Object[2 * size + 1];
    }

    public Object add(Object object) {
        Object current;
        int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                this.values[index] = object;
                return this.values[index];
            }
            if (++index != length) continue;
            index = 0;
        }
        this.values[index] = object;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return object;
    }

    public Object addIfNotIncluded(Object object) {
        Object current;
        int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                return null;
            }
            if (++index != length) continue;
            index = 0;
        }
        this.values[index] = object;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return object;
    }

    public void asArray(Object[] copy) {
        if (this.elementSize != copy.length) {
            throw new IllegalArgumentException();
        }
        int index = this.elementSize;
        int i = 0;
        int l = this.values.length;
        while (i < l && index > 0) {
            if (this.values[i] != null) {
                copy[--index] = this.values[i];
            }
            ++i;
        }
    }

    public void clear() {
        int i = this.values.length;
        while (--i >= 0) {
            this.values[i] = null;
        }
        this.elementSize = 0;
    }

    public Object clone() throws CloneNotSupportedException {
        SimpleSet result = (SimpleSet)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.values.length;
        result.values = new Object[length];
        System.arraycopy(this.values, 0, result.values, 0, length);
        return result;
    }

    public boolean includes(Object object) {
        Object current;
        int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public Object remove(Object object) {
        Object current;
        int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                --this.elementSize;
                Object oldValue = this.values[index];
                this.values[index] = null;
                if (this.values[index + 1 == length ? 0 : index + 1] != null) {
                    this.rehash();
                }
                return oldValue;
            }
            if (++index != length) continue;
            index = 0;
        }
        return null;
    }

    private void rehash() {
        SimpleSet newSet = new SimpleSet(this.elementSize * 2);
        int i = this.values.length;
        while (--i >= 0) {
            Object current = this.values[i];
            if (current == null) continue;
            newSet.add(current);
        }
        this.values = newSet.values;
        this.elementSize = newSet.elementSize;
        this.threshold = newSet.threshold;
    }

    public String toString() {
        String s = "";
        int i = 0;
        int l = this.values.length;
        while (i < l) {
            Object object = this.values[i];
            if (object != null) {
                s = String.valueOf(s) + object.toString() + "\n";
            }
            ++i;
        }
        return s;
    }
}

