/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class CharConstant
extends Constant {
    private char value;

    public static Constant fromValue(char value) {
        return new CharConstant(value);
    }

    private CharConstant(char value) {
        this.value = value;
    }

    @Override
    public byte byteValue() {
        return (byte)this.value;
    }

    @Override
    public char charValue() {
        return this.value;
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
        return (short)this.value;
    }

    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }

    @Override
    public String toString() {
        return "(char)" + this.value;
    }

    @Override
    public int typeID() {
        return 2;
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
        CharConstant other = (CharConstant)obj;
        return this.value == other.value;
    }
}

