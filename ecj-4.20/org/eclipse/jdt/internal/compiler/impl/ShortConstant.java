/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class ShortConstant
extends Constant {
    private short value;

    public static Constant fromValue(short value) {
        return new ShortConstant(value);
    }

    private ShortConstant(short value) {
        this.value = value;
    }

    @Override
    public byte byteValue() {
        return (byte)this.value;
    }

    @Override
    public char charValue() {
        return (char)this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
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

    @Override
    public short shortValue() {
        return this.value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }

    @Override
    public String toString() {
        return "(short)" + this.value;
    }

    @Override
    public int typeID() {
        return 4;
    }

    public int hashCode() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ShortConstant other = (ShortConstant)obj;
        return this.value == other.value;
    }
}

