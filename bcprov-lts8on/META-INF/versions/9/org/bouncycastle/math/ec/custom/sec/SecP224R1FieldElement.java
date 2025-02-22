/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP224R1Field;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP224R1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001"));
    protected int[] x;

    public SecP224R1FieldElement(BigInteger x) {
        if (x == null || x.signum() < 0 || x.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP224R1FieldElement");
        }
        this.x = SecP224R1Field.fromBigInteger(x);
    }

    public SecP224R1FieldElement() {
        this.x = Nat224.create();
    }

    protected SecP224R1FieldElement(int[] x) {
        this.x = x;
    }

    @Override
    public boolean isZero() {
        return Nat224.isZero(this.x);
    }

    @Override
    public boolean isOne() {
        return Nat224.isOne(this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat224.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat224.toBigInteger(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP224R1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.add(this.x, ((SecP224R1FieldElement)b).x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement addOne() {
        int[] z = Nat224.create();
        SecP224R1Field.addOne(this.x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.subtract(this.x, ((SecP224R1FieldElement)b).x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.multiply(this.x, ((SecP224R1FieldElement)b).x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement divide(ECFieldElement b) {
        int[] z = Nat224.create();
        SecP224R1Field.inv(((SecP224R1FieldElement)b).x, z);
        SecP224R1Field.multiply(z, this.x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement negate() {
        int[] z = Nat224.create();
        SecP224R1Field.negate(this.x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement square() {
        int[] z = Nat224.create();
        SecP224R1Field.square(this.x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement invert() {
        int[] z = Nat224.create();
        SecP224R1Field.inv(this.x, z);
        return new SecP224R1FieldElement(z);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] c = this.x;
        if (Nat224.isZero(c) || Nat224.isOne(c)) {
            return this;
        }
        int[] nc = Nat224.create();
        SecP224R1Field.negate(c, nc);
        int[] r = Mod.random(SecP224R1Field.P);
        int[] t = Nat224.create();
        if (!SecP224R1FieldElement.isSquare(c)) {
            return null;
        }
        while (!SecP224R1FieldElement.trySqrt(nc, r, t)) {
            SecP224R1Field.addOne(r, r);
        }
        SecP224R1Field.square(t, r);
        return Nat224.eq(c, r) ? new SecP224R1FieldElement(t) : null;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SecP224R1FieldElement)) {
            return false;
        }
        SecP224R1FieldElement o = (SecP224R1FieldElement)other;
        return Nat224.eq(this.x, o.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 7);
    }

    private static boolean isSquare(int[] x) {
        int[] t1 = Nat224.create();
        int[] t2 = Nat224.create();
        Nat224.copy(x, t1);
        for (int i = 0; i < 7; ++i) {
            Nat224.copy(t1, t2);
            SecP224R1Field.squareN(t1, 1 << i, t1);
            SecP224R1Field.multiply(t1, t2, t1);
        }
        SecP224R1Field.squareN(t1, 95, t1);
        return Nat224.isOne(t1);
    }

    private static void RM(int[] nc, int[] d0, int[] e0, int[] d1, int[] e1, int[] f1, int[] t) {
        SecP224R1Field.multiply(e1, e0, t);
        SecP224R1Field.multiply(t, nc, t);
        SecP224R1Field.multiply(d1, d0, f1);
        SecP224R1Field.add(f1, t, f1);
        SecP224R1Field.multiply(d1, e0, t);
        Nat224.copy(f1, d1);
        SecP224R1Field.multiply(e1, d0, e1);
        SecP224R1Field.add(e1, t, e1);
        SecP224R1Field.square(e1, f1);
        SecP224R1Field.multiply(f1, nc, f1);
    }

    private static void RP(int[] nc, int[] d1, int[] e1, int[] f1, int[] t) {
        Nat224.copy(nc, f1);
        int[] d0 = Nat224.create();
        int[] e0 = Nat224.create();
        for (int i = 0; i < 7; ++i) {
            Nat224.copy(d1, d0);
            Nat224.copy(e1, e0);
            int j = 1 << i;
            while (--j >= 0) {
                SecP224R1FieldElement.RS(d1, e1, f1, t);
            }
            SecP224R1FieldElement.RM(nc, d0, e0, d1, e1, f1, t);
        }
    }

    private static void RS(int[] d, int[] e, int[] f, int[] t) {
        SecP224R1Field.multiply(e, d, e);
        SecP224R1Field.twice(e, e);
        SecP224R1Field.square(d, t);
        SecP224R1Field.add(f, t, d);
        SecP224R1Field.multiply(f, t, f);
        int c = Nat.shiftUpBits(7, f, 2, 0);
        SecP224R1Field.reduce32(c, f);
    }

    private static boolean trySqrt(int[] nc, int[] r, int[] t) {
        int[] d1 = Nat224.create();
        Nat224.copy(r, d1);
        int[] e1 = Nat224.create();
        e1[0] = 1;
        int[] f1 = Nat224.create();
        SecP224R1FieldElement.RP(nc, d1, e1, f1, t);
        int[] d0 = Nat224.create();
        int[] e0 = Nat224.create();
        for (int k = 1; k < 96; ++k) {
            Nat224.copy(d1, d0);
            Nat224.copy(e1, e0);
            SecP224R1FieldElement.RS(d1, e1, f1, t);
            if (!Nat224.isZero(d1)) continue;
            SecP224R1Field.inv(e0, t);
            SecP224R1Field.multiply(t, d0, t);
            return true;
        }
        return false;
    }
}

