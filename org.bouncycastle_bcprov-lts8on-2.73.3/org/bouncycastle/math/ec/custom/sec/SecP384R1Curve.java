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
import org.bouncycastle.math.ec.custom.sec.SecP384R1Field;
import org.bouncycastle.math.ec.custom.sec.SecP384R1FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP384R1Point;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP384R1Curve
extends ECCurve.AbstractFp {
    public static final BigInteger q = SecP384R1FieldElement.Q;
    private static final int SECP384R1_DEFAULT_COORDS = 2;
    private static final ECFieldElement[] SECP384R1_AFFINE_ZS = new ECFieldElement[]{new SecP384R1FieldElement(ECConstants.ONE)};
    protected SecP384R1Point infinity = new SecP384R1Point(this, null, null);

    public SecP384R1Curve() {
        super(q);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF")));
        this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecP384R1Curve();
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
        return new SecP384R1FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecP384R1Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecP384R1Point(this, x, y, zs);
    }

    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        int FE_INTS = 12;
        final int[] table = new int[len * 12 * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            Nat.copy(12, ((SecP384R1FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat.copy(12, ((SecP384R1FieldElement)p.getRawYCoord()).x, 0, table, pos += 12);
            pos += 12;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                int[] x = Nat.create(12);
                int[] y = Nat.create(12);
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    int MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < 12; ++j) {
                        int n = j;
                        x[n] = x[n] ^ table[pos + j] & MASK;
                        int n2 = j;
                        y[n2] = y[n2] ^ table[pos + 12 + j] & MASK;
                    }
                    pos += 24;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                int[] x = Nat.create(12);
                int[] y = Nat.create(12);
                int pos = index * 12 * 2;
                for (int j = 0; j < 12; ++j) {
                    x[j] = table[pos + j];
                    y[j] = table[pos + 12 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(int[] x, int[] y) {
                return SecP384R1Curve.this.createRawPoint(new SecP384R1FieldElement(x), new SecP384R1FieldElement(y), SECP384R1_AFFINE_ZS);
            }
        };
    }

    @Override
    public ECFieldElement randomFieldElement(SecureRandom r) {
        int[] x = Nat.create(12);
        SecP384R1Field.random(r, x);
        return new SecP384R1FieldElement(x);
    }

    @Override
    public ECFieldElement randomFieldElementMult(SecureRandom r) {
        int[] x = Nat.create(12);
        SecP384R1Field.randomMult(r, x);
        return new SecP384R1FieldElement(x);
    }
}

