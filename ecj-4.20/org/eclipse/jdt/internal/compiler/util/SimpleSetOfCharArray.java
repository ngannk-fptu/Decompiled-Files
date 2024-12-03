/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import org.eclipse.jdt.core.compiler.CharOperation;

public final class SimpleSetOfCharArray
implements Cloneable {
    public char[][] values;
    public int elementSize;
    public int threshold;

    public SimpleSetOfCharArray() {
        this(13);
    }

    public SimpleSetOfCharArray(int size) {
        if (size < 3) {
            size = 3;
        }
        this.elementSize = 0;
        this.threshold = size + 1;
        this.values = new char[2 * size + 1][];
    }

    public Object add(char[] object) {
        char[] current;
        int length = this.values.length;
        int index = (CharOperation.hashCode(object) & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (CharOperation.equals(current, object)) {
                this.values[index] = object;
                return object;
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
        SimpleSetOfCharArray result = (SimpleSetOfCharArray)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.values.length;
        result.values = new char[length][];
        System.arraycopy(this.values, 0, result.values, 0, length);
        return result;
    }

    public char[] get(char[] object) {
        char[] current;
        int length = this.values.length;
        int index = (CharOperation.hashCode(object) & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (CharOperation.equals(current, object)) {
                return current;
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

    public boolean includes(char[] object) {
        char[] current;
        int length = this.values.length;
        int index = (CharOperation.hashCode(object) & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (CharOperation.equals(current, object)) {
                return true;
            }
            if (++index != length) continue;
            index = 0;
        }
        return false;
    }

    public char[] remove(char[] object) {
        char[] current;
        int length = this.values.length;
        int index = (CharOperation.hashCode(object) & Integer.MAX_VALUE) % length;
        while ((current = this.values[index]) != null) {
            if (CharOperation.equals(current, object)) {
                --this.elementSize;
                char[] oldValue = this.values[index];
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
        SimpleSetOfCharArray newSet = new SimpleSetOfCharArray(this.elementSize * 2);
        int i = this.values.length;
        while (--i >= 0) {
            char[] current = this.values[i];
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
            char[] object = this.values[i];
            if (object != null) {
                s = String.valueOf(s) + new String(object) + "\n";
            }
            ++i;
        }
        return s;
    }
}

