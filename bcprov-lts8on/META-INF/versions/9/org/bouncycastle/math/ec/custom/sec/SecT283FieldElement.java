/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT283Field;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat320;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT283FieldElement
extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT283FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 283) {
            throw new IllegalArgumentException("x value invalid for SecT283FieldElement");
        }
        this.x = SecT283Field.fromBigInteger(x);
    }

    public SecT283FieldElement() {
        this.x = Nat320.create64();
    }

    protected SecT283FieldElement(long[] x) {
        this.x = x;
    }

    @Override
    public boolean isOne() {
        return Nat320.isOne64(this.x);
    }

    @Override
    public boolean isZero() {
        return Nat320.isZero64(this.x);
    }

    @Override
    public boolean testBitZero() {
        return (this.x[0] & 1L) != 0L;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat320.toBigInteger64(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecT283Field";
    }

    @Override
    public int getFieldSize() {
        return 283;
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat320.create64();
        SecT283Field.add(this.x, ((SecT283FieldElement)b).x, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        long[] z = Nat320.create64();
        SecT283Field.addOne(this.x, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        return this.add(b);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat320.create64();
        SecT283Field.multiply(this.x, ((SecT283FieldElement)b).x, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiplyPlusProduct(b, x, y);
    }

    @Override
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT283FieldElement)b).x;
        long[] xx = ((SecT283FieldElement)x).x;
        long[] yx = ((SecT283FieldElement)y).x;
        long[] tt = Nat.create64(9);
        SecT283Field.multiplyAddToExt(ax, bx, tt);
        SecT283Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat320.create64();
        SecT283Field.reduce(tt, z);
        return new SecT283FieldElement(z);
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
        long[] z = Nat320.create64();
        SecT283Field.square(this.x, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.squarePlusProduct(x, y);
    }

    @Override
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT283FieldElement)x).x;
        long[] yx = ((SecT283FieldElement)y).x;
        long[] tt = Nat.create64(9);
        SecT283Field.squareAddToExt(ax, tt);
        SecT283Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat320.create64();
        SecT283Field.reduce(tt, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat320.create64();
        SecT283Field.squareN(this.x, pow, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public ECFieldElement halfTrace() {
        long[] z = Nat320.create64();
        SecT283Field.halfTrace(this.x, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public boolean hasFastTrace() {
        return true;
    }

    @Override
    public int trace() {
        return SecT283Field.trace(this.x);
    }

    @Override
    public ECFieldElement invert() {
        long[] z = Nat320.create64();
        SecT283Field.invert(this.x, z);
        return new SecT283FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        long[] z = Nat320.create64();
        SecT283Field.sqrt(this.x, z);
        return new SecT283FieldElement(z);
    }

    public int getRepresentation() {
        return 3;
    }

    public int getM() {
        return 283;
    }

    public int getK1() {
        return 5;
    }

    public int getK2() {
        return 7;
    }

    public int getK3() {
        return 12;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT283FieldElement)) {
            return false;
        }
        SecT283FieldElement o = (SecT283FieldElement)other;
        return Nat320.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 0x2B33AB ^ Arrays.hashCode(this.x, 0, 5);
    }
}

