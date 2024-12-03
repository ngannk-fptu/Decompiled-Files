/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecT113FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT113R2Point;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.encoders.Hex;

public class SecT113R2Curve
extends ECCurve.AbstractF2m {
    private static final int SECT113R2_DEFAULT_COORDS = 6;
    private static final ECFieldElement[] SECT113R2_AFFINE_ZS = new ECFieldElement[]{new SecT113FieldElement(ECConstants.ONE)};
    protected SecT113R2Point infinity = new SecT113R2Point(this, null, null);

    public SecT113R2Curve() {
        super(113, 9, 0, 0);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("00689918DBEC7E5A0DD6DFC0AA55C7")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("0095E9A9EC9B297BD4BF36E059184F")));
        this.order = new BigInteger(1, Hex.decodeStrict("010000000000000108789B2496AF93"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }

    protected ECCurve cloneCurve() {
        return new SecT113R2Curve();
    }

    public boolean supportsCoordinateSystem(int n) {
        switch (n) {
            case 6: {
                return true;
            }
        }
        return false;
    }

    public int getFieldSize() {
        return 113;
    }

    public ECFieldElement fromBigInteger(BigInteger bigInteger) {
        return new SecT113FieldElement(bigInteger);
    }

    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        return new SecT113R2Point(this, eCFieldElement, eCFieldElement2);
    }

    protected ECPoint createRawPoint(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArray) {
        return new SecT113R2Point(this, eCFieldElement, eCFieldElement2, eCFieldElementArray);
    }

    public ECPoint getInfinity() {
        return this.infinity;
    }

    public boolean isKoblitz() {
        return false;
    }

    public int getM() {
        return 113;
    }

    public boolean isTrinomial() {
        return true;
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

    public ECLookupTable createCacheSafeLookupTable(ECPoint[] eCPointArray, int n, int n2) {
        long[] lArray = new long[n2 * 2 * 2];
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            ECPoint eCPoint = eCPointArray[n + i];
            Nat128.copy64(((SecT113FieldElement)eCPoint.getRawXCoord()).x, 0, lArray, n3);
            Nat128.copy64(((SecT113FieldElement)eCPoint.getRawYCoord()).x, 0, lArray, n3 += 2);
            n3 += 2;
        }
        return new AbstractECLookupTable(){

            public int getSize() {
                return n2;
            }

            public ECPoint lookup(int n) {
                long[] lArray3 = Nat128.create64();
                long[] lArray2 = Nat128.create64();
                int n22 = 0;
                for (int i = 0; i < n2; ++i) {
                    long l = (i ^ n) - 1 >> 31;
                    for (int j = 0; j < 2; ++j) {
                        int n3 = j;
                        lArray3[n3] = lArray3[n3] ^ lArray[n22 + j] & l;
                        int n4 = j;
                        lArray2[n4] = lArray2[n4] ^ lArray[n22 + 2 + j] & l;
                    }
                    n22 += 4;
                }
                return this.createPoint(lArray3, lArray2);
            }

            public ECPoint lookupVar(int n) {
                long[] lArray3 = Nat128.create64();
                long[] lArray2 = Nat128.create64();
                int n22 = n * 2 * 2;
                for (int i = 0; i < 2; ++i) {
                    lArray3[i] = lArray[n22 + i];
                    lArray2[i] = lArray[n22 + 2 + i];
                }
                return this.createPoint(lArray3, lArray2);
            }

            private ECPoint createPoint(long[] lArray3, long[] lArray2) {
                return SecT113R2Curve.this.createRawPoint(new SecT113FieldElement(lArray3), new SecT113FieldElement(lArray2), SECT113R2_AFFINE_ZS);
            }
        };
    }
}

