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
import org.bouncycastle.math.ec.custom.sec.SecT571FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT571R1Point;
import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT571R1Curve
extends ECCurve.AbstractF2m {
    private static final int SECT571R1_DEFAULT_COORDS = 6;
    private static final ECFieldElement[] SECT571R1_AFFINE_ZS = new ECFieldElement[]{new SecT571FieldElement(ECConstants.ONE)};
    protected SecT571R1Point infinity = new SecT571R1Point(this, null, null);
    static final SecT571FieldElement SecT571R1_B = new SecT571FieldElement(new BigInteger(1, Hex.decodeStrict("02F40E7E2221F295DE297117B7F3D62F5C6A97FFCB8CEFF1CD6BA8CE4A9A18AD84FFABBD8EFA59332BE7AD6756A66E294AFD185A78FF12AA520E4DE739BACA0C7FFEFF7F2955727A")));
    static final SecT571FieldElement SecT571R1_B_SQRT = (SecT571FieldElement)SecT571R1_B.sqrt();

    public SecT571R1Curve() {
        super(571, 2, 5, 10);
        this.a = this.fromBigInteger(BigInteger.valueOf(1L));
        this.b = SecT571R1_B;
        this.order = new BigInteger(1, Hex.decodeStrict("03FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE661CE18FF55987308059B186823851EC7DD9CA1161DE93D5174D66E8382E9BB2FE84E47"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecT571R1Curve();
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
    public int getFieldSize() {
        return 571;
    }

    @Override
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecT571FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecT571R1Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecT571R1Point(this, x, y, zs);
    }

    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override
    public boolean isKoblitz() {
        return false;
    }

    public int getM() {
        return 571;
    }

    public boolean isTrinomial() {
        return false;
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

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        int FE_LONGS = 9;
        final long[] table = new long[len * 9 * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            Nat576.copy64(((SecT571FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat576.copy64(((SecT571FieldElement)p.getRawYCoord()).x, 0, table, pos += 9);
            pos += 9;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                long[] x = Nat576.create64();
                long[] y = Nat576.create64();
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    long MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < 9; ++j) {
                        int n = j;
                        x[n] = x[n] ^ table[pos + j] & MASK;
                        int n2 = j;
                        y[n2] = y[n2] ^ table[pos + 9 + j] & MASK;
                    }
                    pos += 18;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                long[] x = Nat576.create64();
                long[] y = Nat576.create64();
                int pos = index * 9 * 2;
                for (int j = 0; j < 9; ++j) {
                    x[j] = table[pos + j];
                    y[j] = table[pos + 9 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(long[] x, long[] y) {
                return SecT571R1Curve.this.createRawPoint(new SecT571FieldElement(x), new SecT571FieldElement(y), SECT571R1_AFFINE_ZS);
            }
        };
    }
}

