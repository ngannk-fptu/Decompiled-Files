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
import org.bouncycastle.math.ec.custom.sec.SecP521R1Field;
import org.bouncycastle.math.ec.custom.sec.SecP521R1FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP521R1Point;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP521R1Curve
extends ECCurve.AbstractFp {
    public static final BigInteger q = SecP521R1FieldElement.Q;
    private static final int SECP521R1_DEFAULT_COORDS = 2;
    private static final ECFieldElement[] SECP521R1_AFFINE_ZS = new ECFieldElement[]{new SecP521R1FieldElement(ECConstants.ONE)};
    protected SecP521R1Point infinity = new SecP521R1Point(this, null, null);

    public SecP521R1Curve() {
        super(q);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("0051953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00")));
        this.order = new BigInteger(1, Hex.decodeStrict("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecP521R1Curve();
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
        return new SecP521R1FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecP521R1Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecP521R1Point(this, x, y, zs);
    }

    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        int FE_INTS = 17;
        final int[] table = new int[len * 17 * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            Nat.copy(17, ((SecP521R1FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat.copy(17, ((SecP521R1FieldElement)p.getRawYCoord()).x, 0, table, pos += 17);
            pos += 17;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                int[] x = Nat.create(17);
                int[] y = Nat.create(17);
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    int MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < 17; ++j) {
                        int n = j;
                        x[n] = x[n] ^ table[pos + j] & MASK;
                        int n2 = j;
                        y[n2] = y[n2] ^ table[pos + 17 + j] & MASK;
                    }
                    pos += 34;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                int[] x = Nat.create(17);
                int[] y = Nat.create(17);
                int pos = index * 17 * 2;
                for (int j = 0; j < 17; ++j) {
                    int n = j;
                    x[n] = x[n] ^ table[pos + j];
                    int n2 = j;
                    y[n2] = y[n2] ^ table[pos + 17 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(int[] x, int[] y) {
                return SecP521R1Curve.this.createRawPoint(new SecP521R1FieldElement(x), new SecP521R1FieldElement(y), SECP521R1_AFFINE_ZS);
            }
        };
    }

    @Override
    public ECFieldElement randomFieldElement(SecureRandom r) {
        int[] x = Nat.create(17);
        SecP521R1Field.random(r, x);
        return new SecP521R1FieldElement(x);
    }

    @Override
    public ECFieldElement randomFieldElementMult(SecureRandom r) {
        int[] x = Nat.create(17);
        SecP521R1Field.randomMult(r, x);
        return new SecP521R1FieldElement(x);
    }
}

