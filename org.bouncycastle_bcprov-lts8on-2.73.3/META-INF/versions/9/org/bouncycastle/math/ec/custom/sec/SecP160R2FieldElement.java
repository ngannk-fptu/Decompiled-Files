/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP160R2Field;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP160R2FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFAC73"));
    protected int[] x;

    public SecP160R2FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP160R2FieldElement");
        }
        this.x = SecP160R2Field.fromBigInteger(x);
    }

    public SecP160R2FieldElement() {
        this.x = Nat160.create();
    }

    protected SecP160R2FieldElement(int[] x) {
        this.x = x;
    }

    @Override
    public boolean isZero() {
        return Nat160.isZero(this.x);
    }

    @Override
    public boolean isOne() {
        return Nat160.isOne(this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat160.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat160.toBigInteger(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP160R2Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat160.create();
        SecP160R2Field.add(this.x, ((SecP160R2FieldElement)b).x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        int[] z = Nat160.create();
        SecP160R2Field.addOne(this.x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat160.create();
        SecP160R2Field.subtract(this.x, ((SecP160R2FieldElement)b).x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat160.create();
        SecP160R2Field.multiply(this.x, ((SecP160R2FieldElement)b).x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat160.create();
        SecP160R2Field.inv(((SecP160R2FieldElement)b).x, z);
        SecP160R2Field.multiply(z, this.x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement negate() {
        int[] z = Nat160.create();
        SecP160R2Field.negate(this.x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement square() {
        int[] z = Nat160.create();
        SecP160R2Field.square(this.x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement invert() {
        int[] z = Nat160.create();
        SecP160R2Field.inv(this.x, z);
        return new SecP160R2FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] x1 = this.x;
        if (Nat160.isZero(x1) || Nat160.isOne(x1)) {
            return this;
        }
        int[] x2 = Nat160.create();
        SecP160R2Field.square(x1, x2);
        SecP160R2Field.multiply(x2, x1, x2);
        int[] x3 = Nat160.create();
        SecP160R2Field.square(x2, x3);
        SecP160R2Field.multiply(x3, x1, x3);
        int[] x4 = Nat160.create();
        SecP160R2Field.square(x3, x4);
        SecP160R2Field.multiply(x4, x1, x4);
        int[] x7 = Nat160.create();
        SecP160R2Field.squareN(x4, 3, x7);
        SecP160R2Field.multiply(x7, x3, x7);
        int[] x14 = x4;
        SecP160R2Field.squareN(x7, 7, x14);
        SecP160R2Field.multiply(x14, x7, x14);
        int[] x17 = x7;
        SecP160R2Field.squareN(x14, 3, x17);
        SecP160R2Field.multiply(x17, x3, x17);
        int[] x31 = Nat160.create();
        SecP160R2Field.squareN(x17, 14, x31);
        SecP160R2Field.multiply(x31, x14, x31);
        int[] x62 = x14;
        SecP160R2Field.squareN(x31, 31, x62);
        SecP160R2Field.multiply(x62, x31, x62);
        int[] x124 = x31;
        SecP160R2Field.squareN(x62, 62, x124);
        SecP160R2Field.multiply(x124, x62, x124);
        int[] x127 = x62;
        SecP160R2Field.squareN(x124, 3, x127);
        SecP160R2Field.multiply(x127, x3, x127);
        int[] t1 = x127;
        SecP160R2Field.squareN(t1, 18, t1);
        SecP160R2Field.multiply(t1, x17, t1);
        SecP160R2Field.squareN(t1, 2, t1);
        SecP160R2Field.multiply(t1, x1, t1);
        SecP160R2Field.squareN(t1, 3, t1);
        SecP160R2Field.multiply(t1, x2, t1);
        SecP160R2Field.squareN(t1, 6, t1);
        SecP160R2Field.multiply(t1, x3, t1);
        SecP160R2Field.squareN(t1, 2, t1);
        SecP160R2Field.multiply(t1, x1, t1);
        int[] t2 = x2;
        SecP160R2Field.square(t1, t2);
        return Nat160.eq(x1, t2) ? new SecP160R2FieldElement(t1) : null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP160R2FieldElement)) {
            return false;
        }
        SecP160R2FieldElement o = (SecP160R2FieldElement)other;
        return Nat160.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
    }
}

