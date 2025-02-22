/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP128R1Field;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP128R1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFDFFFFFFFFFFFFFFFFFFFFFFFF"));
    protected int[] x;

    public SecP128R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP128R1FieldElement");
        }
        this.x = SecP128R1Field.fromBigInteger(x);
    }

    public SecP128R1FieldElement() {
        this.x = Nat128.create();
    }

    protected SecP128R1FieldElement(int[] x) {
        this.x = x;
    }

    @Override
    public boolean isZero() {
        return Nat128.isZero(this.x);
    }

    @Override
    public boolean isOne() {
        return Nat128.isOne(this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat128.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat128.toBigInteger(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP128R1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat128.create();
        SecP128R1Field.add(this.x, ((SecP128R1FieldElement)b).x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        int[] z = Nat128.create();
        SecP128R1Field.addOne(this.x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat128.create();
        SecP128R1Field.subtract(this.x, ((SecP128R1FieldElement)b).x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat128.create();
        SecP128R1Field.multiply(this.x, ((SecP128R1FieldElement)b).x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat128.create();
        SecP128R1Field.inv(((SecP128R1FieldElement)b).x, z);
        SecP128R1Field.multiply(z, this.x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement negate() {
        int[] z = Nat128.create();
        SecP128R1Field.negate(this.x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement square() {
        int[] z = Nat128.create();
        SecP128R1Field.square(this.x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement invert() {
        int[] z = Nat128.create();
        SecP128R1Field.inv(this.x, z);
        return new SecP128R1FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat128.isZero(x1) || Nat128.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat128.create();
        SecP128R1Field.square(x1, x2);
        SecP128R1Field.multiply(x2, x1, x2);
        int[] x4 = Nat128.create();
        SecP128R1Field.squareN(x2, 2, x4);
        SecP128R1Field.multiply(x4, x2, x4);
        int[] x8 = Nat128.create();
        SecP128R1Field.squareN(x4, 4, x8);
        SecP128R1Field.multiply(x8, x4, x8);
        int[] x10 = x4;
        SecP128R1Field.squareN(x8, 2, x10);
        SecP128R1Field.multiply(x10, x2, x10);
        int[] x20 = x2;
        SecP128R1Field.squareN(x10, 10, x20);
        SecP128R1Field.multiply(x20, x10, x20);
        int[] x30 = x8;
        SecP128R1Field.squareN(x20, 10, x30);
        SecP128R1Field.multiply(x30, x10, x30);
        int[] x31 = x10;
        SecP128R1Field.square(x30, x31);
        SecP128R1Field.multiply(x31, x1, x31);
        int[] t1 = x31;
        SecP128R1Field.squareN(t1, 95, t1);
        int[] t2 = x30;
        SecP128R1Field.square(t1, t2);
        return Nat128.eq(x1, t2) ? new SecP128R1FieldElement(t1) : null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP128R1FieldElement)) {
            return false;
        }
        SecP128R1FieldElement o = (SecP128R1FieldElement)other;
        return Nat128.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 4);
    }
}

