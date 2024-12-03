/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import java.io.Serializable;
import java.math.BigInteger;

public class IntegerValueType
extends Number
implements Comparable,
Serializable {
    private final String value;
    private static final long serialVersionUID = 1L;

    private IntegerValueType(String canonicalizedValue) {
        this.value = canonicalizedValue;
    }

    private IntegerValueType(long v) {
        this.value = Long.toString(v);
    }

    public static IntegerValueType create(BigInteger bi) {
        return IntegerValueType.create(bi.toString());
    }

    public static IntegerValueType create(String nonCanonicalizedValue) {
        int idx = 0;
        String v = "";
        int len = nonCanonicalizedValue.length();
        if (len == 0) {
            return null;
        }
        switch (nonCanonicalizedValue.charAt(idx)) {
            case '+': {
                ++idx;
                break;
            }
            case '-': {
                v = v + '-';
                ++idx;
                break;
            }
            case '0': 
            case '1': 
            case '2': 
            case '3': 
            case '4': 
            case '5': 
            case '6': 
            case '7': 
            case '8': 
            case '9': {
                break;
            }
            default: {
                return null;
            }
        }
        if (idx == len) {
            return null;
        }
        while (idx < len && nonCanonicalizedValue.charAt(idx) == '0') {
            ++idx;
        }
        if (idx == len) {
            return new IntegerValueType("0");
        }
        while (idx < len) {
            char ch;
            if ('0' <= (ch = nonCanonicalizedValue.charAt(idx++)) && ch <= '9') {
                v = v + ch;
                continue;
            }
            return null;
        }
        return new IntegerValueType(v);
    }

    public int compareTo(Object o) {
        int llen;
        int lp;
        int rp;
        boolean rhsIsNegative;
        IntegerValueType rhs = o instanceof IntegerValueType ? (IntegerValueType)o : new IntegerValueType(((Number)o).longValue());
        boolean lhsIsNegative = this.value.charAt(0) == '-';
        boolean bl = rhsIsNegative = rhs.value.charAt(0) == '-';
        if (lhsIsNegative && !rhsIsNegative) {
            return -1;
        }
        if (rhsIsNegative && !lhsIsNegative) {
            return 1;
        }
        if (lhsIsNegative && rhsIsNegative) {
            rp = 1;
            lp = 1;
        } else {
            rp = 0;
            lp = 0;
        }
        int rlen = rhs.value.length() - rp;
        if (llen > rlen) {
            return lhsIsNegative ? -1 : 1;
        }
        if (llen < rlen) {
            return lhsIsNegative ? 1 : -1;
        }
        for (llen = this.value.length() - lp; llen > 0; --llen) {
            char rch;
            char lch;
            if ((lch = this.value.charAt(lp++)) > (rch = rhs.value.charAt(rp++))) {
                return lhsIsNegative ? -1 : 1;
            }
            if (lch >= rch) continue;
            return lhsIsNegative ? 1 : -1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof IntegerValueType) {
            return this.value.equals(((IntegerValueType)o).value);
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return this.value;
    }

    public int precision() {
        int len = this.value.length();
        if (this.value.charAt(0) == '-') {
            return len - 1;
        }
        return len;
    }

    public boolean isNonPositive() {
        char ch = this.value.charAt(0);
        return ch == '-' || ch == '0';
    }

    public boolean isPositive() {
        char ch = this.value.charAt(0);
        return ch != '-' && ch != '0';
    }

    public boolean isNegative() {
        return this.value.charAt(0) == '-';
    }

    public boolean isNonNegative() {
        return this.value.charAt(0) != '-';
    }

    public BigInteger toBigInteger() {
        return new BigInteger(this.value);
    }

    public double doubleValue() {
        return this.toBigInteger().doubleValue();
    }

    public float floatValue() {
        return (float)this.doubleValue();
    }

    public int intValue() {
        return this.toBigInteger().intValue();
    }

    public long longValue() {
        return this.toBigInteger().longValue();
    }
}

