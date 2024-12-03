/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT239Field;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT239FieldElement
extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT239FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 239) {
            throw new IllegalArgumentException("x value invalid for SecT239FieldElement");
        }
        this.x = SecT239Field.fromBigInteger(x);
    }

    public SecT239FieldElement() {
        this.x = Nat256.create64();
    }

    protected SecT239FieldElement(long[] x) {
        this.x = x;
    }

    @Override
    public boolean isOne() {
        return Nat256.isOne64(this.x);
    }

    @Override
    public boolean isZero() {
        return Nat256.isZero64(this.x);
    }

    @Override
    public boolean testBitZero() {
        return (this.x[0] & 1L) != 0L;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger64(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecT239Field";
    }

    @Override
    public int getFieldSize() {
        return 239;
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat256.create64();
        SecT239Field.add(this.x, ((SecT239FieldElement)b).x, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        long[] z = Nat256.create64();
        SecT239Field.addOne(this.x, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        return this.add(b);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat256.create64();
        SecT239Field.multiply(this.x, ((SecT239FieldElement)b).x, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiplyPlusProduct(b, x, y);
    }

    @Override
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT239FieldElement)b).x;
        long[] xx = ((SecT239FieldElement)x).x;
        long[] yx = ((SecT239FieldElement)y).x;
        long[] tt = Nat256.createExt64();
        SecT239Field.multiplyAddToExt(ax, bx, tt);
        SecT239Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat256.create64();
        SecT239Field.reduce(tt, z);
        return new SecT239FieldElement(z);
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
        long[] z = Nat256.create64();
        SecT239Field.square(this.x, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.squarePlusProduct(x, y);
    }

    @Override
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT239FieldElement)x).x;
        long[] yx = ((SecT239FieldElement)y).x;
        long[] tt = Nat256.createExt64();
        SecT239Field.squareAddToExt(ax, tt);
        SecT239Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat256.create64();
        SecT239Field.reduce(tt, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat256.create64();
        SecT239Field.squareN(this.x, pow, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public ECFieldElement halfTrace() {
        long[] z = Nat256.create64();
        SecT239Field.halfTrace(this.x, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public boolean hasFastTrace() {
        return true;
    }

    @Override
    public int trace() {
        return SecT239Field.trace(this.x);
    }

    @Override
    public ECFieldElement invert() {
        long[] z = Nat256.create64();
        SecT239Field.invert(this.x, z);
        return new SecT239FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        long[] z = Nat256.create64();
        SecT239Field.sqrt(this.x, z);
        return new SecT239FieldElement(z);
    }

    public int getRepresentation() {
        return 2;
    }

    public int getM() {
        return 239;
    }

    public int getK1() {
        return 158;
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
        if (!(other instanceof SecT239FieldElement)) {
            return false;
        }
        SecT239FieldElement o = (SecT239FieldElement)other;
        return Nat256.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 0x16CAFFE ^ Arrays.hashCode(this.x, 0, 4);
    }
}

