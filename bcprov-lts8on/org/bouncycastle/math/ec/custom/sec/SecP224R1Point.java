/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP224R1Field;
import org.bouncycastle.math.ec.custom.sec.SecP224R1FieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP224R1Point
extends ECPoint.AbstractFp {
    SecP224R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        super(curve, x, y);
    }

    SecP224R1Point(ECCurve curve, ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        super(curve, x, y, zs);
    }

    @Override
    protected ECPoint detach() {
        return new SecP224R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
    }

    @Override
    public ECPoint add(ECPoint b) {
        int[] S1;
        int[] U1;
        int[] S2;
        int[] U2;
        if (this.isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return this;
        }
        if (this == b) {
            return this.twice();
        }
        ECCurve curve = this.getCurve();
        SecP224R1FieldElement X1 = (SecP224R1FieldElement)this.x;
        SecP224R1FieldElement Y1 = (SecP224R1FieldElement)this.y;
        SecP224R1FieldElement X2 = (SecP224R1FieldElement)b.getXCoord();
        SecP224R1FieldElement Y2 = (SecP224R1FieldElement)b.getYCoord();
        SecP224R1FieldElement Z1 = (SecP224R1FieldElement)this.zs[0];
        SecP224R1FieldElement Z2 = (SecP224R1FieldElement)b.getZCoord(0);
        int[] tt1 = Nat224.createExt();
        int[] t2 = Nat224.create();
        int[] t3 = Nat224.create();
        int[] t4 = Nat224.create();
        boolean Z1IsOne = Z1.isOne();
        if (Z1IsOne) {
            U2 = X2.x;
            S2 = Y2.x;
        } else {
            S2 = t3;
            SecP224R1Field.square(Z1.x, S2);
            U2 = t2;
            SecP224R1Field.multiply(S2, X2.x, U2);
            SecP224R1Field.multiply(S2, Z1.x, S2);
            SecP224R1Field.multiply(S2, Y2.x, S2);
        }
        boolean Z2IsOne = Z2.isOne();
        if (Z2IsOne) {
            U1 = X1.x;
            S1 = Y1.x;
        } else {
            S1 = t4;
            SecP224R1Field.square(Z2.x, S1);
            U1 = tt1;
            SecP224R1Field.multiply(S1, X1.x, U1);
            SecP224R1Field.multiply(S1, Z2.x, S1);
            SecP224R1Field.multiply(S1, Y1.x, S1);
        }
        int[] H = Nat224.create();
        SecP224R1Field.subtract(U1, U2, H);
        int[] R = t2;
        SecP224R1Field.subtract(S1, S2, R);
        if (Nat224.isZero(H)) {
            if (Nat224.isZero(R)) {
                return this.twice();
            }
            return curve.getInfinity();
        }
        int[] HSquared = t3;
        SecP224R1Field.square(H, HSquared);
        int[] G = Nat224.create();
        SecP224R1Field.multiply(HSquared, H, G);
        int[] V = t3;
        SecP224R1Field.multiply(HSquared, U1, V);
        SecP224R1Field.negate(G, G);
        Nat224.mul(S1, G, tt1);
        int c = Nat224.addBothTo(V, V, G);
        SecP224R1Field.reduce32(c, G);
        SecP224R1FieldElement X3 = new SecP224R1FieldElement(t4);
        SecP224R1Field.square(R, X3.x);
        SecP224R1Field.subtract(X3.x, G, X3.x);
        SecP224R1FieldElement Y3 = new SecP224R1FieldElement(G);
        SecP224R1Field.subtract(V, X3.x, Y3.x);
        SecP224R1Field.multiplyAddToExt(Y3.x, R, tt1);
        SecP224R1Field.reduce(tt1, Y3.x);
        SecP224R1FieldElement Z3 = new SecP224R1FieldElement(H);
        if (!Z1IsOne) {
            SecP224R1Field.multiply(Z3.x, Z1.x, Z3.x);
        }
        if (!Z2IsOne) {
            SecP224R1Field.multiply(Z3.x, Z2.x, Z3.x);
        }
        ECFieldElement[] zs = new ECFieldElement[]{Z3};
        return new SecP224R1Point(curve, X3, Y3, zs);
    }

    @Override
    public ECPoint twice() {
        if (this.isInfinity()) {
            return this;
        }
        ECCurve curve = this.getCurve();
        SecP224R1FieldElement Y1 = (SecP224R1FieldElement)this.y;
        if (Y1.isZero()) {
            return curve.getInfinity();
        }
        SecP224R1FieldElement X1 = (SecP224R1FieldElement)this.x;
        SecP224R1FieldElement Z1 = (SecP224R1FieldElement)this.zs[0];
        int[] t1 = Nat224.create();
        int[] t2 = Nat224.create();
        int[] Y1Squared = Nat224.create();
        SecP224R1Field.square(Y1.x, Y1Squared);
        int[] T = Nat224.create();
        SecP224R1Field.square(Y1Squared, T);
        boolean Z1IsOne = Z1.isOne();
        int[] Z1Squared = Z1.x;
        if (!Z1IsOne) {
            Z1Squared = t2;
            SecP224R1Field.square(Z1.x, Z1Squared);
        }
        SecP224R1Field.subtract(X1.x, Z1Squared, t1);
        int[] M = t2;
        SecP224R1Field.add(X1.x, Z1Squared, M);
        SecP224R1Field.multiply(M, t1, M);
        int c = Nat224.addBothTo(M, M, M);
        SecP224R1Field.reduce32(c, M);
        int[] S = Y1Squared;
        SecP224R1Field.multiply(Y1Squared, X1.x, S);
        c = Nat.shiftUpBits(7, S, 2, 0);
        SecP224R1Field.reduce32(c, S);
        c = Nat.shiftUpBits(7, T, 3, 0, t1);
        SecP224R1Field.reduce32(c, t1);
        SecP224R1FieldElement X3 = new SecP224R1FieldElement(T);
        SecP224R1Field.square(M, X3.x);
        SecP224R1Field.subtract(X3.x, S, X3.x);
        SecP224R1Field.subtract(X3.x, S, X3.x);
        SecP224R1FieldElement Y3 = new SecP224R1FieldElement(S);
        SecP224R1Field.subtract(S, X3.x, Y3.x);
        SecP224R1Field.multiply(Y3.x, M, Y3.x);
        SecP224R1Field.subtract(Y3.x, t1, Y3.x);
        SecP224R1FieldElement Z3 = new SecP224R1FieldElement(M);
        SecP224R1Field.twice(Y1.x, Z3.x);
        if (!Z1IsOne) {
            SecP224R1Field.multiply(Z3.x, Z1.x, Z3.x);
        }
        return new SecP224R1Point(curve, X3, Y3, new ECFieldElement[]{Z3});
    }

    @Override
    public ECPoint twicePlus(ECPoint b) {
        if (this == b) {
            return this.threeTimes();
        }
        if (this.isInfinity()) {
            return b;
        }
        if (b.isInfinity()) {
            return this.twice();
        }
        ECFieldElement Y1 = this.y;
        if (Y1.isZero()) {
            return b;
        }
        return this.twice().add(b);
    }

    @Override
    public ECPoint threeTimes() {
        if (this.isInfinity() || this.y.isZero()) {
            return this;
        }
        return this.twice().add(this);
    }

    @Override
    public ECPoint negate() {
        if (this.isInfinity()) {
            return this;
        }
        return new SecP224R1Point(this.curve, this.x, this.y.negate(), this.zs);
    }
}

