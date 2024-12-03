/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import org.apache.axis.utils.Messages;

public class UnsignedInt
extends Number
implements Comparable {
    protected Long lValue = new Long(0L);
    private Object __equalsCalc = null;

    public UnsignedInt() {
    }

    public UnsignedInt(long iValue) throws NumberFormatException {
        this.setValue(iValue);
    }

    public UnsignedInt(String stValue) throws NumberFormatException {
        this.setValue(Long.parseLong(stValue));
    }

    public void setValue(long iValue) throws NumberFormatException {
        if (!UnsignedInt.isValid(iValue)) {
            throw new NumberFormatException(Messages.getMessage("badUnsignedInt00") + String.valueOf(iValue) + "]");
        }
        this.lValue = new Long(iValue);
    }

    public String toString() {
        if (this.lValue != null) {
            return this.lValue.toString();
        }
        return null;
    }

    public int hashCode() {
        if (this.lValue != null) {
            return this.lValue.hashCode();
        }
        return 0;
    }

    public static boolean isValid(long iValue) {
        return iValue >= 0L && iValue <= 0xFFFFFFFFL;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof UnsignedInt)) {
            return false;
        }
        UnsignedInt other = (UnsignedInt)obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.__equalsCalc != null) {
            return this.__equalsCalc == obj;
        }
        this.__equalsCalc = obj;
        boolean _equals = this.lValue == null && other.lValue == null || this.lValue != null && this.lValue.equals(other.lValue);
        this.__equalsCalc = null;
        return _equals;
    }

    public int compareTo(Object obj) {
        if (this.lValue != null) {
            return this.lValue.compareTo(obj);
        }
        if (this.equals(obj)) {
            return 0;
        }
        return 1;
    }

    public byte byteValue() {
        return this.lValue.byteValue();
    }

    public short shortValue() {
        return this.lValue.shortValue();
    }

    public int intValue() {
        return this.lValue.intValue();
    }

    public long longValue() {
        return this.lValue;
    }

    public double doubleValue() {
        return this.lValue.doubleValue();
    }

    public float floatValue() {
        return this.lValue.floatValue();
    }
}

