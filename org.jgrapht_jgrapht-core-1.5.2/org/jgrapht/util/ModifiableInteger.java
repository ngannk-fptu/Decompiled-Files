/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

public class ModifiableInteger
extends Number
implements Comparable<ModifiableInteger> {
    private static final long serialVersionUID = 3618698612851422261L;
    public int value;

    @Deprecated
    public ModifiableInteger() {
    }

    public ModifiableInteger(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void increment() {
        ++this.value;
    }

    public void decrement() {
        --this.value;
    }

    @Override
    public int compareTo(ModifiableInteger anotherInteger) {
        int thisVal = this.value;
        int anotherVal = anotherInteger.value;
        return Integer.compare(thisVal, anotherVal);
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (o instanceof ModifiableInteger) {
            return this.value == ((ModifiableInteger)o).value;
        }
        return false;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    public Integer toInteger() {
        return this.value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

