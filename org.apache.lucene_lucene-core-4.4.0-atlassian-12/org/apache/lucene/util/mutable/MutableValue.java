/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.mutable;

public abstract class MutableValue
implements Comparable<MutableValue> {
    public boolean exists = true;

    public abstract void copy(MutableValue var1);

    public abstract MutableValue duplicate();

    public abstract boolean equalsSameType(Object var1);

    public abstract int compareSameType(Object var1);

    public abstract Object toObject();

    public boolean exists() {
        return this.exists;
    }

    @Override
    public int compareTo(MutableValue other) {
        Class<?> c2;
        Class<?> c1 = this.getClass();
        if (c1 != (c2 = other.getClass())) {
            int c = c1.hashCode() - c2.hashCode();
            if (c == 0) {
                c = c1.getCanonicalName().compareTo(c2.getCanonicalName());
            }
            return c;
        }
        return this.compareSameType(other);
    }

    public boolean equals(Object other) {
        return this.getClass() == other.getClass() && this.equalsSameType(other);
    }

    public abstract int hashCode();

    public String toString() {
        return this.exists() ? this.toObject().toString() : "(null)";
    }
}

