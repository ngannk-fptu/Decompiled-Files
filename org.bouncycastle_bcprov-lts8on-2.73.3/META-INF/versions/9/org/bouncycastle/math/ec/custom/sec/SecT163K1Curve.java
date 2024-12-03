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
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.WTauNafMultiplier;
import org.bouncycastle.math.ec.custom.sec.SecT163FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT163K1Point;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT163K1Curve
extends ECCurve.AbstractF2m {
    private static final int SECT163K1_DEFAULT_COORDS = 6;
    private static final ECFieldElement[] SECT163K1_AFFINE_ZS = new ECFieldElement[]{new SecT163FieldElement(ECConstants.ONE)};
    protected SecT163K1Point infinity = new SecT163K1Point(this, null, null);

    public SecT163K1Curve() {
        super(163, 3, 6, 7);
        this.b = this.a = this.fromBigInteger(BigInteger.valueOf(1L));
        this.order = new BigInteger(1, Hex.decodeStrict("04000000000000000000020108A2E0CC0D99F8A5EF"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecT163K1Curve();
    }

    @Override
    public boolean supportsCoordinateSystem(int coord) {
        switch (coord) {
            case 6: {
                return true;
            }
        }
        return false;
    }

    @Override
    protected ECMultiplier createDefaultMultiplier() {
        return new WTauNafMultiplier();
    }

    @Override
    public int getFieldSize() {
        return 163;
    }

    @Override
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecT163FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecT163K1Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecT163K1Point(this, x, y, zs);
    }

    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override
    public boolean isKoblitz() {
        return true;
    }

    public int getM() {
        return 163;
    }

    public boolean isTrinomial() {
        return false;
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

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        int FE_LONGS = 3;
        final long[] table = new long[len * 3 * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            Nat192.copy64(((SecT163FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat192.copy64(((SecT163FieldElement)p.getRawYCoord()).x, 0, table, pos += 3);
            pos += 3;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                long[] x = Nat192.create64();
                long[] y = Nat192.create64();
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    long MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < 3; ++j) {
                        int n = j;
                        x[n] = x[n] ^ table[pos + j] & MASK;
                        int n2 = j;
                        y[n2] = y[n2] ^ table[pos + 3 + j] & MASK;
                    }
                    pos += 6;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                long[] x = Nat192.create64();
                long[] y = Nat192.create64();
                int pos = index * 3 * 2;
                for (int j = 0; j < 3; ++j) {
                    x[j] = table[pos + j];
                    y[j] = table[pos + 3 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(long[] x, long[] y) {
                return SecT163K1Curve.this.createRawPoint(new SecT163FieldElement(x), new SecT163FieldElement(y), SECT163K1_AFFINE_ZS);
            }
        };
    }
}

