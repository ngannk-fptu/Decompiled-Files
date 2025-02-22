/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Field;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP256R1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF"));
    protected int[] x;

    public SecP256R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256R1FieldElement");
        }
        this.x = SecP256R1Field.fromBigInteger(x);
    }

    public SecP256R1FieldElement() {
        this.x = Nat256.create();
    }

    protected SecP256R1FieldElement(int[] x) {
        this.x = x;
    }

    @Override
    public boolean isZero() {
        return Nat256.isZero(this.x);
    }

    @Override
    public boolean isOne() {
        return Nat256.isOne(this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat256.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP256R1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.add(this.x, ((SecP256R1FieldElement)b).x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        int[] z = Nat256.create();
        SecP256R1Field.addOne(this.x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.subtract(this.x, ((SecP256R1FieldElement)b).x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.multiply(this.x, ((SecP256R1FieldElement)b).x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat256.create();
        SecP256R1Field.inv(((SecP256R1FieldElement)b).x, z);
        SecP256R1Field.multiply(z, this.x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement negate() {
        int[] z = Nat256.create();
        SecP256R1Field.negate(this.x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement square() {
        int[] z = Nat256.create();
        SecP256R1Field.square(this.x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement invert() {
        int[] z = Nat256.create();
        SecP256R1Field.inv(this.x, z);
        return new SecP256R1FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this;
        }
        int[] tt0 = Nat256.createExt();
        int[] t1 = Nat256.create();
        int[] t2 = Nat256.create();
        SecP256R1Field.square(x1, t1, tt0);
        SecP256R1Field.multiply(t1, x1, t1, tt0);
        SecP256R1Field.squareN(t1, 2, t2, tt0);
        SecP256R1Field.multiply(t2, t1, t2, tt0);
        SecP256R1Field.squareN(t2, 4, t1, tt0);
        SecP256R1Field.multiply(t1, t2, t1, tt0);
        SecP256R1Field.squareN(t1, 8, t2, tt0);
        SecP256R1Field.multiply(t2, t1, t2, tt0);
        SecP256R1Field.squareN(t2, 16, t1, tt0);
        SecP256R1Field.multiply(t1, t2, t1, tt0);
        SecP256R1Field.squareN(t1, 32, t1, tt0);
        SecP256R1Field.multiply(t1, x1, t1, tt0);
        SecP256R1Field.squareN(t1, 96, t1, tt0);
        SecP256R1Field.multiply(t1, x1, t1, tt0);
        SecP256R1Field.squareN(t1, 94, t1, tt0);
        SecP256R1Field.square(t1, t2, tt0);
        return Nat256.eq(x1, t2) ? new SecP256R1FieldElement(t1) : null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP256R1FieldElement)) {
            return false;
        }
        SecP256R1FieldElement o = (SecP256R1FieldElement)other;
        return Nat256.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
}

