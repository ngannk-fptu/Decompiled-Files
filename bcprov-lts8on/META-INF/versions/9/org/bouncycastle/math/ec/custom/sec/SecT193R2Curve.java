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
import org.bouncycastle.math.ec.custom.sec.SecT193FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecT193R2Point;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecT193R2Curve
extends ECCurve.AbstractF2m {
    private static final int SECT193R2_DEFAULT_COORDS = 6;
    private static final ECFieldElement[] SECT193R2_AFFINE_ZS = new ECFieldElement[]{new SecT193FieldElement(ECConstants.ONE)};
    protected SecT193R2Point infinity = new SecT193R2Point(this, null, null);

    public SecT193R2Curve() {
        super(193, 15, 0, 0);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("0163F35A5137C2CE3EA6ED8667190B0BC43ECD69977702709B")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("00C9BB9E8927D4D64C377E2AB2856A5B16E3EFB7F61D4316AE")));
        this.order = new BigInteger(1, Hex.decodeStrict("010000000000000000000000015AAB561B005413CCD4EE99D5"));
        this.cofactor = BigInteger.valueOf(2L);
        this.coord = 6;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecT193R2Curve();
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
        return 193;
    }

    @Override
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecT193FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecT193R2Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecT193R2Point(this, x, y, zs);
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
        return 193;
    }

    public boolean isTrinomial() {
        return true;
    }

    public int getK1() {
        return 15;
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
            Nat256.copy64(((SecT193FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat256.copy64(((SecT193FieldElement)p.getRawYCoord()).x, 0, table, pos += 4);
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
                    int n = j;
                    x[n] = x[n] ^ table[pos + j];
                    int n2 = j;
                    y[n2] = y[n2] ^ table[pos + 4 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(long[] x, long[] y) {
                return SecT193R2Curve.this.createRawPoint(new SecT193FieldElement(x), new SecT193FieldElement(y), SECT193R2_AFFINE_ZS);
            }
        };
    }
}

