/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT571Field;
import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT571FieldElement
extends ECFieldElement.AbstractF2m {
    protected long[] x;

    public SecT571FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.bitLength() > 571) {
            throw new IllegalArgumentException("x value invalid for SecT571FieldElement");
        }
        this.x = SecT571Field.fromBigInteger(x);
    }

    public SecT571FieldElement() {
        this.x = Nat576.create64();
    }

    protected SecT571FieldElement(long[] x) {
        this.x = x;
    }

    @Override
    public boolean isOne() {
        return Nat576.isOne64(this.x);
    }

    @Override
    public boolean isZero() {
        return Nat576.isZero64(this.x);
    }

    @Override
    public boolean testBitZero() {
        return (this.x[0] & 1L) != 0L;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat576.toBigInteger64(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecT571Field";
    }

    @Override
    public int getFieldSize() {
        return 571;
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        long[] z = Nat576.create64();
        SecT571Field.add(this.x, ((SecT571FieldElement)b).x, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        long[] z = Nat576.create64();
        SecT571Field.addOne(this.x, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        return this.add(b);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        long[] z = Nat576.create64();
        SecT571Field.multiply(this.x, ((SecT571FieldElement)b).x, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiplyPlusProduct(b, x, y);
    }

    @Override
    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] bx = ((SecT571FieldElement)b).x;
        long[] xx = ((SecT571FieldElement)x).x;
        long[] yx = ((SecT571FieldElement)y).x;
        long[] tt = Nat576.createExt64();
        SecT571Field.multiplyAddToExt(ax, bx, tt);
        SecT571Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat576.create64();
        SecT571Field.reduce(tt, z);
        return new SecT571FieldElement(z);
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
        long[] z = Nat576.create64();
        SecT571Field.square(this.x, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.squarePlusProduct(x, y);
    }

    @Override
    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        long[] ax = this.x;
        long[] xx = ((SecT571FieldElement)x).x;
        long[] yx = ((SecT571FieldElement)y).x;
        long[] tt = Nat576.createExt64();
        SecT571Field.squareAddToExt(ax, tt);
        SecT571Field.multiplyAddToExt(xx, yx, tt);
        long[] z = Nat576.create64();
        SecT571Field.reduce(tt, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public ECFieldElement squarePow(int pow) {
        if (pow < 1) {
            return this;
        }
        long[] z = Nat576.create64();
        SecT571Field.squareN(this.x, pow, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public ECFieldElement halfTrace() {
        long[] z = Nat576.create64();
        SecT571Field.halfTrace(this.x, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public boolean hasFastTrace() {
        return true;
    }

    @Override
    public int trace() {
        return SecT571Field.trace(this.x);
    }

    @Override
    public ECFieldElement invert() {
        long[] z = Nat576.create64();
        SecT571Field.invert(this.x, z);
        return new SecT571FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        long[] z = Nat576.create64();
        SecT571Field.sqrt(this.x, z);
        return new SecT571FieldElement(z);
    }

    public int getRepresentation() {
        return 3;
    }

    public int getM() {
        return 571;
    }

    public int getK1() {
        return 2;
    }

    public int getK2() {
        return 5;
    }

    public int getK3() {
        return 10;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecT571FieldElement)) {
            return false;
        }
        SecT571FieldElement o = (SecT571FieldElement)other;
        return Nat576.eq64(this.x, o.x);
    }

    public int hashCode() {
        return 0x5724CC ^ Arrays.hashCode(this.x, 0, 9);
    }
}

