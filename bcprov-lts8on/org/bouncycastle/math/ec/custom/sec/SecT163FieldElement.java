/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT163Field;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT163FieldElement
extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT163FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 163) {
            throw new IllegalArgumentException("x value invalid for SecT163FieldElement");
        }
        this.x = SecT163Field.fromBigInteger(x);
    }

    public SecT163FieldElement() {
        this.x = Nat192.create64();
    }

    protected SecT163FieldElement(long[] x) {
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
        return "SecT163Field";
    }

    @Override
    public int getFieldSize() {
        return 163;
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat192.create64();
        SecT163Field.add(this.x, ((SecT163FieldElement)b).x, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        long[] z = Nat192.create64();
        SecT163Field.addOne(this.x, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        return this.add(b);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat192.create64();
        SecT163Field.multiply(this.x, ((SecT163FieldElement)b).x, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiplyPlusProduct(b, x, y);
    }

    @Override
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT163FieldElement)b).x;
        long[] xx = ((SecT163FieldElement)x).x;
        long[] yx = ((SecT163FieldElement)y).x;
        long[] tt = Nat192.createExt64();
        SecT163Field.multiplyAddToExt(ax, bx, tt);
        SecT163Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat192.create64();
        SecT163Field.reduce(tt, z);
        return new SecT163FieldElement(z);
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
        SecT163Field.square(this.x, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.squarePlusProduct(x, y);
    }

    @Override
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT163FieldElement)x).x;
        long[] yx = ((SecT163FieldElement)y).x;
        long[] tt = Nat192.createExt64();
        SecT163Field.squareAddToExt(ax, tt);
        SecT163Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat192.create64();
        SecT163Field.reduce(tt, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat192.create64();
        SecT163Field.squareN(this.x, pow, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public ECFieldElement halfTrace() {
        long[] z = Nat192.create64();
        SecT163Field.halfTrace(this.x, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public boolean hasFastTrace() {
        return true;
    }

    @Override
    public int trace() {
        return SecT163Field.trace(this.x);
    }

    @Override
    public ECFieldElement invert() {
        long[] z = Nat192.create64();
        SecT163Field.invert(this.x, z);
        return new SecT163FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        long[] z = Nat192.create64();
        SecT163Field.sqrt(this.x, z);
        return new SecT163FieldElement(z);
    }

    public int getRepresentation() {
        return 3;
    }

    public int getM() {
        return 163;
    }

    public int getK1() {
        return 3;
    }

    public int getK2() {
        return 6;
    }

    public int getK3() {
        return 7;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT163FieldElement)) {
            return false;
        }
        SecT163FieldElement o = (SecT163FieldElement)other;
        return Nat192.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 0x27FB3 ^ Arrays.hashCode(this.x, 0, 3);
    }
}

