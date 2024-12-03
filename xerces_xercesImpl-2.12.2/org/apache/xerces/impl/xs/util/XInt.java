/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

public final class XInt {
    private final int fValue;

    XInt(int n) {
        this.fValue = n;
    }

    public final int intValue() {
        return this.fValue;
    }

    public final short shortValue() {
        return (short)this.fValue;
    }

    public final boolean equals(XInt xInt) {
        return this.fValue == xInt.fValue;
    }

    public String toString() {
        return Integer.toString(this.fValue);
    }
}

