/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT113Field;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT113FieldElement
extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT113FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 113) {
            throw new IllegalArgumentException("x value invalid for SecT113FieldElement");
        }
        this.x = SecT113Field.fromBigInteger(x);
    }

    public SecT113FieldElement() {
        this.x = Nat128.create64();
    }

    protected SecT113FieldElement(long[] x) {
        this.x = x;
    }

    @Override
    public boolean isOne() {
        return Nat128.isOne64(this.x);
    }

    @Override
    public boolean isZero() {
        return Nat128.isZero64(this.x);
    }

    @Override
    public boolean testBitZero() {
        return (this.x[0] & 1L) != 0L;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat128.toBigInteger64(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecT113Field";
    }

    @Override
    public int getFieldSize() {
        return 113;
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat128.create64();
        SecT113Field.add(this.x, ((SecT113FieldElement)b).x, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        long[] z = Nat128.create64();
        SecT113Field.addOne(this.x, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        return this.add(b);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat128.create64();
        SecT113Field.multiply(this.x, ((SecT113FieldElement)b).x, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiplyPlusProduct(b, x, y);
    }

    @Override
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT113FieldElement)b).x;
        long[] xx = ((SecT113FieldElement)x).x;
        long[] yx = ((SecT113FieldElement)y).x;
        long[] tt = Nat128.createExt64();
        SecT113Field.multiplyAddToExt(ax, bx, tt);
        SecT113Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat128.create64();
        SecT113Field.reduce(tt, z);
        return new SecT113FieldElement(z);
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
        long[] z = Nat128.create64();
        SecT113Field.square(this.x, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.squarePlusProduct(x, y);
    }

    @Override
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT113FieldElement)x).x;
        long[] yx = ((SecT113FieldElement)y).x;
        long[] tt = Nat128.createExt64();
        SecT113Field.squareAddToExt(ax, tt);
        SecT113Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat128.create64();
        SecT113Field.reduce(tt, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat128.create64();
        SecT113Field.squareN(this.x, pow, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public ECFieldElement halfTrace() {
        long[] z = Nat128.create64();
        SecT113Field.halfTrace(this.x, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public boolean hasFastTrace() {
        return true;
    }

    @Override
    public int trace() {
        return SecT113Field.trace(this.x);
    }

    @Override
    public ECFieldElement invert() {
        long[] z = Nat128.create64();
        SecT113Field.invert(this.x, z);
        return new SecT113FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        long[] z = Nat128.create64();
        SecT113Field.sqrt(this.x, z);
        return new SecT113FieldElement(z);
    }

    public int getRepresentation() {
        return 2;
    }

    public int getM() {
        return 113;
    }

    public int getK1() {
        return 9;
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
        if (!(other instanceof SecT113FieldElement)) {
            return false;
        }
        SecT113FieldElement o = (SecT113FieldElement)other;
        return Nat128.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 0x1B971 ^ Arrays.hashCode(this.x, 0, 2);
    }
}

