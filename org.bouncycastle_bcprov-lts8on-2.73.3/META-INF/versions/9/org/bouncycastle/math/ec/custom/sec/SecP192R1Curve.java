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
import org.bouncycastle.math.ec.custom.sec.SecP192R1Field;
import org.bouncycastle.math.ec.custom.sec.SecP192R1FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP192R1Point;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SecP192R1Curve
extends ECCurve.AbstractFp {
    public static final BigInteger q = SecP192R1FieldElement.Q;
    private static final int SECP192R1_DEFAULT_COORDS = 2;
    private static final ECFieldElement[] SECP192R1_AFFINE_ZS = new ECFieldElement[]{new SecP192R1FieldElement(ECConstants.ONE)};
    protected SecP192R1Point infinity = new SecP192R1Point(this, null, null);

    public SecP192R1Curve() {
        super(q);
        this.a = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC")));
        this.b = this.fromBigInteger(new BigInteger(1, Hex.decodeStrict("64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1")));
        this.order = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831"));
        this.cofactor = BigInteger.valueOf(1L);
        this.coord = 2;
    }

    @Override
    protected ECCurve cloneCurve() {
        return new SecP192R1Curve();
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
        return new SecP192R1FieldElement(x);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
        return new SecP192R1Point(this, x, y);
    }

    @Override
    protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
        return new SecP192R1Point(this, x, y, zs);
    }

    @Override
    public ECPoint getInfinity() {
        return this.infinity;
    }

    @Override
    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        int FE_INTS = 6;
        final int[] table = new int[len * 6 * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            Nat192.copy(((SecP192R1FieldElement)p.getRawXCoord()).x, 0, table, pos);
            Nat192.copy(((SecP192R1FieldElement)p.getRawYCoord()).x, 0, table, pos += 6);
            pos += 6;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                int[] x = Nat192.create();
                int[] y = Nat192.create();
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    int MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < 6; ++j) {
                        int n = j;
                        x[n] = x[n] ^ table[pos + j] & MASK;
                        int n2 = j;
                        y[n2] = y[n2] ^ table[pos + 6 + j] & MASK;
                    }
                    pos += 12;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                int[] x = Nat192.create();
                int[] y = Nat192.create();
                int pos = index * 6 * 2;
                for (int j = 0; j < 6; ++j) {
                    x[j] = table[pos + j];
                    y[j] = table[pos + 6 + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(int[] x, int[] y) {
                return SecP192R1Curve.this.createRawPoint(new SecP192R1FieldElement(x), new SecP192R1FieldElement(y), SECP192R1_AFFINE_ZS);
            }
        };
    }

    @Override
    public ECFieldElement randomFieldElement(SecureRandom r) {
        int[] x = Nat192.create();
        SecP192R1Field.random(r, x);
        return new SecP192R1FieldElement(x);
    }

    @Override
    public ECFieldElement randomFieldElementMult(SecureRandom r) {
        int[] x = Nat192.create();
        SecP192R1Field.randomMult(r, x);
        return new SecP192R1FieldElement(x);
    }
}

