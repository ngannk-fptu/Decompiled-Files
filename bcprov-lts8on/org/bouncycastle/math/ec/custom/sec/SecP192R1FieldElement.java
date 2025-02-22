/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP192R1Field;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP192R1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF"));
    protected int[] x;

    public SecP192R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP192R1FieldElement");
        }
        this.x = SecP192R1Field.fromBigInteger(x);
    }

    public SecP192R1FieldElement() {
        this.x = Nat192.create();
    }

    protected SecP192R1FieldElement(int[] x) {
        this.x = x;
    }

    @Override
    public boolean isZero() {
        return Nat192.isZero(this.x);
    }

    @Override
    public boolean isOne() {
        return Nat192.isOne(this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat192.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat192.toBigInteger(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP192R1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.add(this.x, ((SecP192R1FieldElement)b).x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        int[] z = Nat192.create();
        SecP192R1Field.addOne(this.x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.subtract(this.x, ((SecP192R1FieldElement)b).x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.multiply(this.x, ((SecP192R1FieldElement)b).x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat192.create();
        SecP192R1Field.inv(((SecP192R1FieldElement)b).x, z);
        SecP192R1Field.multiply(z, this.x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement negate() {
        int[] z = Nat192.create();
        SecP192R1Field.negate(this.x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement square() {
        int[] z = Nat192.create();
        SecP192R1Field.square(this.x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement invert() {
        int[] z = Nat192.create();
        SecP192R1Field.inv(this.x, z);
        return new SecP192R1FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat192.isZero(x1) || Nat192.isOne(x1)) {
            return this;
        }
        int[] t1 = Nat192.create();
        int[] t2 = Nat192.create();
        SecP192R1Field.square(x1, t1);
        SecP192R1Field.multiply(t1, x1, t1);
        SecP192R1Field.squareN(t1, 2, t2);
        SecP192R1Field.multiply(t2, t1, t2);
        SecP192R1Field.squareN(t2, 4, t1);
        SecP192R1Field.multiply(t1, t2, t1);
        SecP192R1Field.squareN(t1, 8, t2);
        SecP192R1Field.multiply(t2, t1, t2);
        SecP192R1Field.squareN(t2, 16, t1);
        SecP192R1Field.multiply(t1, t2, t1);
        SecP192R1Field.squareN(t1, 32, t2);
        SecP192R1Field.multiply(t2, t1, t2);
        SecP192R1Field.squareN(t2, 64, t1);
        SecP192R1Field.multiply(t1, t2, t1);
        SecP192R1Field.squareN(t1, 62, t1);
        SecP192R1Field.square(t1, t2);
        return Nat192.eq(x1, t2) ? new SecP192R1FieldElement(t1) : null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP192R1FieldElement)) {
            return false;
        }
        SecP192R1FieldElement o = (SecP192R1FieldElement)other;
        return Nat192.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 6);
    }
}

