/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT131Field;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT131FieldElement
extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT131FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 131) {
            throw new IllegalArgumentException("x value invalid for SecT131FieldElement");
        }
        this.x = SecT131Field.fromBigInteger(x);
    }

    public SecT131FieldElement() {
        this.x = Nat192.create64();
    }

    protected SecT131FieldElement(long[] x) {
        this.x = x;
    }

    @Override
    public boolean isOne() {
        return Nat192.isOne64(this.x);
    }

    @Override
    public boolean isZero() {
        return Nat192.isZero64(this.x);
    }

    @Override
    public boolean testBitZero() {
        return (this.x[0] & 1L) != 0L;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat192.toBigInteger64(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecT131Field";
    }

    @Override
    public int getFieldSize() {
        return 131;
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat192.create64();
        SecT131Field.add(this.x, ((SecT131FieldElement)b).x, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        long[] z = Nat192.create64();
        SecT131Field.addOne(this.x, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        return this.add(b);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat192.create64();
        SecT131Field.multiply(this.x, ((SecT131FieldElement)b).x, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiplyPlusProduct(b, x, y);
    }

    @Override
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT131FieldElement)b).x;
        long[] xx = ((SecT131FieldElement)x).x;
        long[] yx = ((SecT131FieldElement)y).x;
        long[] tt = Nat.create64(5);
        SecT131Field.multiplyAddToExt(ax, bx, tt);
        SecT131Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat192.create64();
        SecT131Field.reduce(tt, z);
        return new SecT131FieldElement(z);
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
        long[] z = Nat192.create64();
        SecT131Field.square(this.x, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.squarePlusProduct(x, y);
    }

    @Override
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT131FieldElement)x).x;
        long[] yx = ((SecT131FieldElement)y).x;
        long[] tt = Nat.create64(5);
        SecT131Field.squareAddToExt(ax, tt);
        SecT131Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat192.create64();
        SecT131Field.reduce(tt, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat192.create64();
        SecT131Field.squareN(this.x, pow, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public ECFieldElement halfTrace() {
        long[] z = Nat192.create64();
        SecT131Field.halfTrace(this.x, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public boolean hasFastTrace() {
        return true;
    }

    @Override
    public int trace() {
        return SecT131Field.trace(this.x);
    }

    @Override
    public ECFieldElement invert() {
        long[] z = Nat192.create64();
        SecT131Field.invert(this.x, z);
        return new SecT131FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        long[] z = Nat192.create64();
        SecT131Field.sqrt(this.x, z);
        return new SecT131FieldElement(z);
    }

    public int getRepresentation() {
        return 3;
    }

    public int getM() {
        return 131;
    }

    public int getK1() {
        return 2;
    }

    public int getK2() {
        return 3;
    }

    public int getK3() {
        return 8;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT131FieldElement)) {
            return false;
        }
        SecT131FieldElement o = (SecT131FieldElement)other;
        return Nat192.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 0x202F8 ^ Arrays.hashCode(this.x, 0, 3);
    }
}

