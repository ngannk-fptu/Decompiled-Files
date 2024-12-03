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
import org.bouncycastle.math.ec.custom.sec.SecT233FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT233K1Point;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT233K1Curve
extends ECCurve.AbstractF2m {
    private static final int SECT233K1_DEFAULT_COORDS = 6;
    private static final ECFieldElement[] SECT233K1_AFFINE_ZS = new ECFieldElement[]{new SecT233FieldElement(ECConstants.ONE)};
    protected SecT233K1Point infinity = new SecT233K1Point(this, null, null);

    public SecT233K1Curve() {
        super(233, 74, 0, 0);
        this.a = this.fromBigInteger(BigInteger.valueOf(0L));
        this.b = this.fromBigInteger(BigInteger.valueOf(1L));
        this.order = new BigInteger(1, Hex.decodeStrict("8000000000000000000000000000069D5BB915BCD46EFB1AD5F173ABDF"));
        this.cofactor = BigInteger.valueOf(4L);
        this.coord = 6;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecT233K1Curve();
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
        return 233;
    }

    @Override
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecT233FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecT233K1Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecT233K1Point(this, x, y, zs);
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
        return 233;
    }

    public boolean isTrinomial() {
        return true;
    }

    public int getK1() {
        return 74;
    }

    public int getK2() {
        return 0;
    }

    public int getK3() {
        return 0;
    }

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        int FE_LONGS = 4;
        final long[] table = new long[len * 4 * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            Nat256.copy64(((SecT233FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat256.copy64(((SecT233FieldElement)p.getRawYCoord()).x, 0, table, pos += 4);
            pos += 4;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                long[] x = Nat256.create64();
                long[] y = Nat256.create64();
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    long MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < 4; ++j) {
                        int n = j;
                        x[n] = x[n] ^ table[pos + j] & MASK;
                        int n2 = j;
                        y[n2] = y[n2] ^ table[pos + 4 + j] & MASK;
                    }
                    pos += 8;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                long[] x = Nat256.create64();
                long[] y = Nat256.create64();
                int pos = index * 4 * 2;
                for (int j = 0; j < 4; ++j) {
                    x[j] = table[pos + j];
                    y[j] = table[pos + 4 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(long[] x, long[] y) {
                return SecT233K1Curve.this.createRawPoint(new SecT233FieldElement(x), new SecT233FieldElement(y), SECT233K1_AFFINE_ZS);
            }
        };
    }
}

