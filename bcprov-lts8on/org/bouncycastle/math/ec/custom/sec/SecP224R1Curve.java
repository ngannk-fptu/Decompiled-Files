/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP224R1Field;
import org.bouncycastle.math.ec.custom.sec.SecP224R1FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP224R1Point;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP224R1Curve
extends ECCurve.AbstractFp {
    public static final BigInteger q = SecP224R1FieldElement.Q;
    private static final int SECP224R1_DEFAULT_COORDS = 2;
    private static final ECFieldElement[] SECP224R1_AFFINE_ZS = new ECFieldElement[]{new SecP224R1FieldElement(ECConstants.ONE)};
    protected SecP224R1Point infinity = new SecP224R1Point(this, null, null);

    public SecP224R1Curve() {
        super(q);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("B4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4")));
        this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecP224R1Curve();
    }

    @Override
    public boolean supportsCoordinateSystem(int coord) {
        switch (coord) {
            case 2: {
                return true;
            }
        }
        return false;
    }

    public BigInteger getQ() {
        return q;
    }

    @Override
    public int getFieldSize() {
        return q.bitLength();
    }

    @Override
    public ECFieldElement fromBigInteger(BigInteger x) {
        return new SecP224R1FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecP224R1Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecP224R1Point(this, x, y, zs);
    }

    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        int FE_INTS = 7;
        final int[] table = new int[len * 7 * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            Nat224.copy(((SecP224R1FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat224.copy(((SecP224R1FieldElement)p.getRawYCoord()).x, 0, table, pos += 7);
            pos += 7;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                int[] x = Nat224.create();
                int[] y = Nat224.create();
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    int MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < 7; ++j) {
                        int n = j;
                        x[n] = x[n] ^ table[pos + j] & MASK;
                        int n2 = j;
                        y[n2] = y[n2] ^ table[pos + 7 + j] & MASK;
                    }
                    pos += 14;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                int[] x = Nat224.create();
                int[] y = Nat224.create();
                int pos = index * 7 * 2;
                for (int j = 0; j < 7; ++j) {
                    x[j] = table[pos + j];
                    y[j] = table[pos + 7 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(int[] x, int[] y) {
                return SecP224R1Curve.this.createRawPoint(new SecP224R1FieldElement(x), new SecP224R1FieldElement(y), SECP224R1_AFFINE_ZS);
            }
        };
    }

    @Override
    public ECFieldElement randomFieldElement(SecureRandom r) {
        int[] x = Nat224.create();
        SecP224R1Field.random(r, x);
        return new SecP224R1FieldElement(x);
    }

    @Override
    public ECFieldElement randomFieldElementMult(SecureRandom r) {
        int[] x = Nat224.create();
        SecP224R1Field.randomMult(r, x);
        return new SecP224R1FieldElement(x);
    }
}

