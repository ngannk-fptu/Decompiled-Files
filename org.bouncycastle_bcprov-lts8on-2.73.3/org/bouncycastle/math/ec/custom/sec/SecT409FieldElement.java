/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT409Field;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat448;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT409FieldElement
extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT409FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 409) {
            throw new IllegalArgumentException("x value invalid for SecT409FieldElement");
        }
        this.x = SecT409Field.fromBigInteger(x);
    }

    public SecT409FieldElement() {
        this.x = Nat448.create64();
    }

    protected SecT409FieldElement(long[] x) {
        this.x = x;
    }

    @Override
    public boolean isOne() {
        return Nat448.isOne64(this.x);
    }

    @Override
    public boolean isZero() {
        return Nat448.isZero64(this.x);
    }

    @Override
    public boolean testBitZero() {
        return (this.x[0] & 1L) != 0L;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat448.toBigInteger64(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecT409Field";
    }

    @Override
    public int getFieldSize() {
        return 409;
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat448.create64();
        SecT409Field.add(this.x, ((SecT409FieldElement)b).x, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        long[] z = Nat448.create64();
        SecT409Field.addOne(this.x, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        return this.add(b);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat448.create64();
        SecT409Field.multiply(this.x, ((SecT409FieldElement)b).x, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiplyPlusProduct(b, x, y);
    }

    @Override
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT409FieldElement)b).x;
        long[] xx = ((SecT409FieldElement)x).x;
        long[] yx = ((SecT409FieldElement)y).x;
        long[] tt = Nat.create64(13);
        SecT409Field.multiplyAddToExt(ax, bx, tt);
        SecT409Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat448.create64();
        SecT409Field.reduce(tt, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        return this.multiply(b.invert());
    }

    @Override
    public ECFieldElement negate() {
        return this;
    }

    @Override
    public ECFieldElement square() {
        long[] z = Nat448.create64();
        SecT409Field.square(this.x, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.squarePlusProduct(x, y);
    }

    @Override
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT409FieldElement)x).x;
        long[] yx = ((SecT409FieldElement)y).x;
        long[] tt = Nat.create64(13);
        SecT409Field.squareAddToExt(ax, tt);
        SecT409Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat448.create64();
        SecT409Field.reduce(tt, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat448.create64();
        SecT409Field.squareN(this.x, pow, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement halfTrace() {
        long[] z = Nat448.create64();
        SecT409Field.halfTrace(this.x, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public boolean hasFastTrace() {
        return true;
    }

    @Override
    public int trace() {
        return SecT409Field.trace(this.x);
    }

    @Override
    public ECFieldElement invert() {
        long[] z = Nat448.create64();
        SecT409Field.invert(this.x, z);
        return new SecT409FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        long[] z = Nat448.create64();
        SecT409Field.sqrt(this.x, z);
        return new SecT409FieldElement(z);
    }

    public int getRepresentation() {
        return 2;
    }

    public int getM() {
        return 409;
    }

    public int getK1() {
        return 87;
    }

    public int getK2() {
        return 0;
    }

    public int getK3() {
        return 0;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT409FieldElement)) {
            return false;
        }
        SecT409FieldElement o = (SecT409FieldElement)other;
        return Nat448.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 0x3E68E7 ^ Arrays.hashCode(this.x, 0, 7);
    }
}

