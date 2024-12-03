/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.math.BigInteger;
import org.apache.axis.utils.Messages;

public class UnsignedLong
extends Number
implements Comparable {
    protected BigInteger lValue = BigInteger.ZERO;
    private static BigInteger MAX = new BigInteger("18446744073709551615");
    private Object __equalsCalc = null;

    public UnsignedLong() {
    }

    public UnsignedLong(double value) throws NumberFormatException {
        this.setValue(new BigInteger(Double.toString(value)));
    }

    public UnsignedLong(BigInteger value) throws NumberFormatException {
        this.setValue(value);
    }

    public UnsignedLong(long lValue) throws NumberFormatException {
        this.setValue(BigInteger.valueOf(lValue));
    }

    public UnsignedLong(String stValue) throws NumberFormatException {
        this.setValue(new BigInteger(stValue));
    }

    private void setValue(BigInteger val) {
        if (!UnsignedLong.isValid(val)) {
            throw new NumberFormatException(Messages.getMessage("badUnsignedLong00") + String.valueOf(val) + "]");
        }
        this.lValue = val;
    }

    public static boolean isValid(BigInteger value) {
        return value.compareTo(BigInteger.ZERO) != -1 && value.compareTo(MAX) != 1;
    }

    public String toString() {
        return this.lValue.toString();
    }

    public int hashCode() {
        if (this.lValue != null) {
            return this.lValue.hashCode();
        }
        return 0;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof UnsignedLong)) {
            return false;
        }
        UnsignedLong other = (UnsignedLong)obj;
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
        return this.lValue.longValue();
    }

    public double doubleValue() {
        return this.lValue.doubleValue();
    }

    public float floatValue() {
        return this.lValue.floatValue();
    }
}

