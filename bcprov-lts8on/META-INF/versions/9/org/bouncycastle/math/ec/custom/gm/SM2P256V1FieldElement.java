/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.gm;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Field;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SM2P256V1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF"));
    protected int[] x;

    public SM2P256V1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SM2P256V1FieldElement");
        }
        this.x = SM2P256V1Field.fromBigInteger(x);
    }

    public SM2P256V1FieldElement() {
        this.x = Nat256.create();
    }

    protected SM2P256V1FieldElement(int[] x) {
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
        return "SM2P256V1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat256.create();
        SM2P256V1Field.add(this.x, ((SM2P256V1FieldElement)b).x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        int[] z = Nat256.create();
        SM2P256V1Field.addOne(this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat256.create();
        SM2P256V1Field.subtract(this.x, ((SM2P256V1FieldElement)b).x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat256.create();
        SM2P256V1Field.multiply(this.x, ((SM2P256V1FieldElement)b).x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat256.create();
        SM2P256V1Field.inv(((SM2P256V1FieldElement)b).x, z);
        SM2P256V1Field.multiply(z, this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement negate() {
        int[] z = Nat256.create();
        SM2P256V1Field.negate(this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement square() {
        int[] z = Nat256.create();
        SM2P256V1Field.square(this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement invert() {
        int[] z = Nat256.create();
        SM2P256V1Field.inv(this.x, z);
        return new SM2P256V1FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat256.isZero(x1) || Nat256.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat256.create();
        SM2P256V1Field.square(x1, x2);
        SM2P256V1Field.multiply(x2, x1, x2);
        int[] x4 = Nat256.create();
        SM2P256V1Field.squareN(x2, 2, x4);
        SM2P256V1Field.multiply(x4, x2, x4);
        int[] x6 = Nat256.create();
        SM2P256V1Field.squareN(x4, 2, x6);
        SM2P256V1Field.multiply(x6, x2, x6);
        int[] x12 = x2;
        SM2P256V1Field.squareN(x6, 6, x12);
        SM2P256V1Field.multiply(x12, x6, x12);
        int[] x24 = Nat256.create();
        SM2P256V1Field.squareN(x12, 12, x24);
        SM2P256V1Field.multiply(x24, x12, x24);
        int[] x30 = x12;
        SM2P256V1Field.squareN(x24, 6, x30);
        SM2P256V1Field.multiply(x30, x6, x30);
        int[] x31 = x6;
        SM2P256V1Field.square(x30, x31);
        SM2P256V1Field.multiply(x31, x1, x31);
        int[] t1 = x24;
        SM2P256V1Field.squareN(x31, 31, t1);
        int[] x62 = x30;
        SM2P256V1Field.multiply(t1, x31, x62);
        SM2P256V1Field.squareN(t1, 32, t1);
        SM2P256V1Field.multiply(t1, x62, t1);
        SM2P256V1Field.squareN(t1, 62, t1);
        SM2P256V1Field.multiply(t1, x62, t1);
        SM2P256V1Field.squareN(t1, 4, t1);
        SM2P256V1Field.multiply(t1, x4, t1);
        SM2P256V1Field.squareN(t1, 32, t1);
        SM2P256V1Field.multiply(t1, x1, t1);
        SM2P256V1Field.squareN(t1, 62, t1);
        int[] t2 = x4;
        SM2P256V1Field.square(t1, t2);
        return Nat256.eq(x1, t2) ? new SM2P256V1FieldElement(t1) : null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SM2P256V1FieldElement)) {
            return false;
        }
        SM2P256V1FieldElement o = (SM2P256V1FieldElement)other;
        return Nat256.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
}

