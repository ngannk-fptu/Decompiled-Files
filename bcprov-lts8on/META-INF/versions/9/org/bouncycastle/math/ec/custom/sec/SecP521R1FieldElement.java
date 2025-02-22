/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP521R1Field;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP521R1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
    protected int[] x;

    public SecP521R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP521R1FieldElement");
        }
        this.x = SecP521R1Field.fromBigInteger(x);
    }

    public SecP521R1FieldElement() {
        this.x = Nat.create(17);
    }

    protected SecP521R1FieldElement(int[] x) {
        this.x = x;
    }

    @Override
    public boolean isZero() {
        return Nat.isZero(17, this.x);
    }

    @Override
    public boolean isOne() {
        return Nat.isOne(17, this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat.toBigInteger(17, this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP521R1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat.create(17);
        SecP521R1Field.add(this.x, ((SecP521R1FieldElement)b).x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        int[] z = Nat.create(17);
        SecP521R1Field.addOne(this.x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat.create(17);
        SecP521R1Field.subtract(this.x, ((SecP521R1FieldElement)b).x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat.create(17);
        SecP521R1Field.multiply(this.x, ((SecP521R1FieldElement)b).x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat.create(17);
        SecP521R1Field.inv(((SecP521R1FieldElement)b).x, z);
        SecP521R1Field.multiply(z, this.x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement negate() {
        int[] z = Nat.create(17);
        SecP521R1Field.negate(this.x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement square() {
        int[] z = Nat.create(17);
        SecP521R1Field.square(this.x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement invert() {
        int[] z = Nat.create(17);
        SecP521R1Field.inv(this.x, z);
        return new SecP521R1FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat.isZero(17, x1) || Nat.isOne(17, x1)) {
            return this;
        }
        int[] tt0 = Nat.create(33);
        int[] t1 = Nat.create(17);
        int[] t2 = Nat.create(17);
        SecP521R1Field.squareN(x1, 519, t1, tt0);
        SecP521R1Field.square(t1, t2, tt0);
        return Nat.eq(17, x1, t2) ? new SecP521R1FieldElement(t1) : null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP521R1FieldElement)) {
            return false;
        }
        SecP521R1FieldElement o = (SecP521R1FieldElement)other;
        return Nat.eq(17, this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 17);
    }
}

