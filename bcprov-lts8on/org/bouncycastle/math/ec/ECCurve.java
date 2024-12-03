/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.math.ec.AbstractECLookupTable;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECLookupTable;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.GLVMultiplier;
import org.bouncycastle.math.ec.LongArray;
import org.bouncycastle.math.ec.PreCompCallback;
import org.bouncycastle.math.ec.PreCompInfo;
import org.bouncycastle.math.ec.Tnaf;
import org.bouncycastle.math.ec.WNafL2RMultiplier;
import org.bouncycastle.math.ec.WTauNafMultiplier;
import org.bouncycastle.math.ec.endo.ECEndomorphism;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.FiniteFields;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ECCurve {
    public static final int COORD_AFFINE = 0;
    public static final int COORD_HOMOGENEOUS = 1;
    public static final int COORD_JACOBIAN = 2;
    public static final int COORD_JACOBIAN_CHUDNOVSKY = 3;
    public static final int COORD_JACOBIAN_MODIFIED = 4;
    public static final int COORD_LAMBDA_AFFINE = 5;
    public static final int COORD_LAMBDA_PROJECTIVE = 6;
    public static final int COORD_SKEWED = 7;
    protected FiniteField field;
    protected ECFieldElement a;
    protected ECFieldElement b;
    protected BigInteger order;
    protected BigInteger cofactor;
    protected int coord = 0;
    protected ECEndomorphism endomorphism = null;
    protected ECMultiplier multiplier = null;

    public static int[] getAllCoordinateSystems() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    }

    protected ECCurve(FiniteField field) {
        this.field = field;
    }

    public abstract int getFieldSize();

    public abstract ECFieldElement fromBigInteger(BigInteger var1);

    public abstract boolean isValidFieldElement(BigInteger var1);

    public abstract ECFieldElement randomFieldElement(SecureRandom var1);

    public abstract ECFieldElement randomFieldElementMult(SecureRandom var1);

    public synchronized Config configure() {
        return new Config(this.coord, this.endomorphism, this.multiplier);
    }

    public ECPoint validatePoint(BigInteger x, BigInteger y) {
        ECPoint p = this.createPoint(x, y);
        if (!p.isValid()) {
            throw new IllegalArgumentException("Invalid point coordinates");
        }
        return p;
    }

    public ECPoint createPoint(BigInteger x, BigInteger y) {
        return this.createRawPoint(this.fromBigInteger(x), this.fromBigInteger(y));
    }

    protected abstract ECCurve cloneCurve();

    protected abstract ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2);

    protected abstract ECPoint createRawPoint(ECFieldElement var1, ECFieldElement var2, ECFieldElement[] var3);

    protected ECMultiplier createDefaultMultiplier() {
        if (this.endomorphism instanceof GLVEndomorphism) {
            return new GLVMultiplier(this, (GLVEndomorphism)this.endomorphism);
        }
        return new WNafL2RMultiplier();
    }

    public boolean supportsCoordinateSystem(int coord) {
        return coord == 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PreCompInfo getPreCompInfo(ECPoint point, String name) {
        Hashtable table;
        this.checkPoint(point);
        Object object = point;
        synchronized (object) {
            table = point.preCompTable;
        }
        if (null == table) {
            return null;
        }
        object = table;
        synchronized (object) {
            return (PreCompInfo)table.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PreCompInfo precompute(ECPoint point, String name, PreCompCallback callback) {
        Hashtable<String, PreCompInfo> table;
        this.checkPoint(point);
        Object object = point;
        synchronized (object) {
            table = point.preCompTable;
            if (null == table) {
                point.preCompTable = table = new Hashtable<String, PreCompInfo>(4);
            }
        }
        object = table;
        synchronized (object) {
            PreCompInfo existing = (PreCompInfo)table.get(name);
            PreCompInfo result = callback.precompute(existing);
            if (result != existing) {
                table.put(name, result);
            }
            return result;
        }
    }

    public ECPoint importPoint(ECPoint p) {
        if (this == p.getCurve()) {
            return p;
        }
        if (p.isInfinity()) {
            return this.getInfinity();
        }
        p = p.normalize();
        return this.createPoint(p.getXCoord().toBigInteger(), p.getYCoord().toBigInteger());
    }

    public void normalizeAll(ECPoint[] points) {
        this.normalizeAll(points, 0, points.length, null);
    }

    public void normalizeAll(ECPoint[] points, int off, int len, ECFieldElement iso) {
        this.checkPoints(points, off, len);
        switch (this.getCoordinateSystem()) {
            case 0: 
            case 5: {
                if (iso != null) {
                    throw new IllegalArgumentException("'iso' not valid for affine coordinates");
                }
                return;
            }
        }
        ECFieldElement[] zs = new ECFieldElement[len];
        int[] indices = new int[len];
        int count = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            if (null == p || iso == null && p.isNormalized()) continue;
            zs[count] = p.getZCoord(0);
            indices[count++] = off + i;
        }
        if (count == 0) {
            return;
        }
        ECAlgorithms.montgomeryTrick(zs, 0, count, iso);
        for (int j = 0; j < count; ++j) {
            int index = indices[j];
            points[index] = points[index].normalize(zs[j]);
        }
    }

    public abstract ECPoint getInfinity();

    public FiniteField getField() {
        return this.field;
    }

    public ECFieldElement getA() {
        return this.a;
    }

    public ECFieldElement getB() {
        return this.b;
    }

    public BigInteger getOrder() {
        return this.order;
    }

    public BigInteger getCofactor() {
        return this.cofactor;
    }

    public int getCoordinateSystem() {
        return this.coord;
    }

    protected abstract ECPoint decompressPoint(int var1, BigInteger var2);

    public ECEndomorphism getEndomorphism() {
        return this.endomorphism;
    }

    public ECMultiplier getMultiplier() {
        if (this.multiplier == null) {
            this.multiplier = this.createDefaultMultiplier();
        }
        return this.multiplier;
    }

    public ECPoint decodePoint(byte[] encoded) {
        ECPoint p = null;
        int expectedLength = (this.getFieldSize() + 7) / 8;
        byte type = encoded[0];
        switch (type) {
            case 0: {
                if (encoded.length != 1) {
                    throw new IllegalArgumentException("Incorrect length for infinity encoding");
                }
                p = this.getInfinity();
                break;
            }
            case 2: 
            case 3: {
                if (encoded.length != expectedLength + 1) {
                    throw new IllegalArgumentException("Incorrect length for compressed encoding");
                }
                int yTilde = type & 1;
                BigInteger X = BigIntegers.fromUnsignedByteArray(encoded, 1, expectedLength);
                p = this.decompressPoint(yTilde, X);
                if (p.implIsValid(true, true)) break;
                throw new IllegalArgumentException("Invalid point");
            }
            case 4: {
                if (encoded.length != 2 * expectedLength + 1) {
                    throw new IllegalArgumentException("Incorrect length for uncompressed encoding");
                }
                BigInteger X = BigIntegers.fromUnsignedByteArray(encoded, 1, expectedLength);
                BigInteger Y = BigIntegers.fromUnsignedByteArray(encoded, 1 + expectedLength, expectedLength);
                p = this.validatePoint(X, Y);
                break;
            }
            case 6: 
            case 7: {
                if (encoded.length != 2 * expectedLength + 1) {
                    throw new IllegalArgumentException("Incorrect length for hybrid encoding");
                }
                BigInteger X = BigIntegers.fromUnsignedByteArray(encoded, 1, expectedLength);
                BigInteger Y = BigIntegers.fromUnsignedByteArray(encoded, 1 + expectedLength, expectedLength);
                if (Y.testBit(0) != (type == 7)) {
                    throw new IllegalArgumentException("Inconsistent Y coordinate in hybrid encoding");
                }
                p = this.validatePoint(X, Y);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid point encoding 0x" + Integer.toString(type, 16));
            }
        }
        if (type != 0 && p.isInfinity()) {
            throw new IllegalArgumentException("Invalid infinity encoding");
        }
        return p;
    }

    public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
        final int FE_BYTES = this.getFieldSize() + 7 >>> 3;
        final byte[] table = new byte[len * FE_BYTES * 2];
        int pos = 0;
        for (int i = 0; i < len; ++i) {
            ECPoint p = points[off + i];
            byte[] px = p.getRawXCoord().toBigInteger().toByteArray();
            byte[] py = p.getRawYCoord().toBigInteger().toByteArray();
            int pxStart = px.length > FE_BYTES ? 1 : 0;
            int pxLen = px.length - pxStart;
            int pyStart = py.length > FE_BYTES ? 1 : 0;
            int pyLen = py.length - pyStart;
            System.arraycopy(px, pxStart, table, pos + FE_BYTES - pxLen, pxLen);
            System.arraycopy(py, pyStart, table, (pos += FE_BYTES) + FE_BYTES - pyLen, pyLen);
            pos += FE_BYTES;
        }
        return new AbstractECLookupTable(){

            @Override
            public int getSize() {
                return len;
            }

            @Override
            public ECPoint lookup(int index) {
                byte[] x = new byte[FE_BYTES];
                byte[] y = new byte[FE_BYTES];
                int pos = 0;
                for (int i = 0; i < len; ++i) {
                    int MASK = (i ^ index) - 1 >> 31;
                    for (int j = 0; j < FE_BYTES; ++j) {
                        int n = j;
                        x[n] = (byte)(x[n] ^ table[pos + j] & MASK);
                        int n2 = j;
                        y[n2] = (byte)(y[n2] ^ table[pos + FE_BYTES + j] & MASK);
                    }
                    pos += FE_BYTES * 2;
                }
                return this.createPoint(x, y);
            }

            @Override
            public ECPoint lookupVar(int index) {
                byte[] x = new byte[FE_BYTES];
                byte[] y = new byte[FE_BYTES];
                int pos = index * FE_BYTES * 2;
                for (int j = 0; j < FE_BYTES; ++j) {
                    x[j] = table[pos + j];
                    y[j] = table[pos + FE_BYTES + j];
                }
                return this.createPoint(x, y);
            }

            private ECPoint createPoint(byte[] x, byte[] y) {
                return ECCurve.this.createRawPoint(ECCurve.this.fromBigInteger(new BigInteger(1, x)), ECCurve.this.fromBigInteger(new BigInteger(1, y)));
            }
        };
    }

    protected void checkPoint(ECPoint point) {
        if (null == point || this != point.getCurve()) {
            throw new IllegalArgumentException("'point' must be non-null and on this curve");
        }
    }

    protected void checkPoints(ECPoint[] points) {
        this.checkPoints(points, 0, points.length);
    }

    protected void checkPoints(ECPoint[] points, int off, int len) {
        if (points == null) {
            throw new IllegalArgumentException("'points' cannot be null");
        }
        if (off < 0 || len < 0 || off > points.length - len) {
            throw new IllegalArgumentException("invalid range specified for 'points'");
        }
        for (int i = 0; i < len; ++i) {
            ECPoint point = points[off + i];
            if (null == point || this == point.getCurve()) continue;
            throw new IllegalArgumentException("'points' entries must be null or on this curve");
        }
    }

    public boolean equals(ECCurve other) {
        return this == other || null != other && this.getField().equals(other.getField()) && this.getA().toBigInteger().equals(other.getA().toBigInteger()) && this.getB().toBigInteger().equals(other.getB().toBigInteger());
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof ECCurve && this.equals((ECCurve)obj);
    }

    public int hashCode() {
        return this.getField().hashCode() ^ Integers.rotateLeft(this.getA().toBigInteger().hashCode(), 8) ^ Integers.rotateLeft(this.getB().toBigInteger().hashCode(), 16);
    }

    private static int getNumberOfIterations(int bits, int certainty) {
        if (bits >= 1536) {
            return certainty <= 100 ? 3 : (certainty <= 128 ? 4 : 4 + (certainty - 128 + 1) / 2);
        }
        if (bits >= 1024) {
            return certainty <= 100 ? 4 : (certainty <= 112 ? 5 : 5 + (certainty - 112 + 1) / 2);
        }
        if (bits >= 512) {
            return certainty <= 80 ? 5 : (certainty <= 100 ? 7 : 7 + (certainty - 100 + 1) / 2);
        }
        return certainty <= 80 ? 40 : 40 + (certainty - 80 + 1) / 2;
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static abstract class AbstractF2m
    extends ECCurve {
        private BigInteger[] si = null;

        public static BigInteger inverse(int m, int[] ks, BigInteger x) {
            return new LongArray(x).modInverse(m, ks).toBigInteger();
        }

        private static FiniteField buildField(int m, int k1, int k2, int k3) {
            int[] nArray;
            if ((k2 | k3) == 0) {
                int[] nArray2 = new int[3];
                nArray2[0] = 0;
                nArray2[1] = k1;
                nArray = nArray2;
                nArray2[2] = m;
            } else {
                int[] nArray3 = new int[5];
                nArray3[0] = 0;
                nArray3[1] = k1;
                nArray3[2] = k2;
                nArray3[3] = k3;
                nArray = nArray3;
                nArray3[4] = m;
            }
            int[] exponents = nArray;
            return FiniteFields.getBinaryExtensionField(exponents);
        }

        protected AbstractF2m(int m, int k1, int k2, int k3) {
            super(AbstractF2m.buildField(m, k1, k2, k3));
        }

        @Override
        public ECPoint createPoint(BigInteger x, BigInteger y) {
            ECFieldElement X = this.fromBigInteger(x);
            ECFieldElement Y = this.fromBigInteger(y);
            int coord = this.getCoordinateSystem();
            switch (coord) {
                case 5: 
                case 6: {
                    if (X.isZero()) {
                        if (Y.square().equals(this.getB())) break;
                        throw new IllegalArgumentException();
                    }
                    Y = Y.divide(X).add(X);
                    break;
                }
            }
            return this.createRawPoint(X, Y);
        }

        @Override
        public boolean isValidFieldElement(BigInteger x) {
            return x != null && x.signum() >= 0 && x.bitLength() <= this.getFieldSize();
        }

        @Override
        public ECFieldElement randomFieldElement(SecureRandom r) {
            int m = this.getFieldSize();
            return this.fromBigInteger(BigIntegers.createRandomBigInteger(m, r));
        }

        @Override
        public ECFieldElement randomFieldElementMult(SecureRandom r) {
            int m = this.getFieldSize();
            ECFieldElement fe1 = this.fromBigInteger(AbstractF2m.implRandomFieldElementMult(r, m));
            ECFieldElement fe2 = this.fromBigInteger(AbstractF2m.implRandomFieldElementMult(r, m));
            return fe1.multiply(fe2);
        }

        @Override
        protected ECPoint decompressPoint(int yTilde, BigInteger X1) {
            ECFieldElement x = this.fromBigInteger(X1);
            ECFieldElement y = null;
            if (x.isZero()) {
                y = this.getB().sqrt();
            } else {
                ECFieldElement beta = x.square().invert().multiply(this.getB()).add(this.getA()).add(x);
                ECFieldElement z = this.solveQuadraticEquation(beta);
                if (z != null) {
                    if (z.testBitZero() != (yTilde == 1)) {
                        z = z.addOne();
                    }
                    switch (this.getCoordinateSystem()) {
                        case 5: 
                        case 6: {
                            y = z.add(x);
                            break;
                        }
                        default: {
                            y = z.multiply(x);
                        }
                    }
                }
            }
            if (y == null) {
                throw new IllegalArgumentException("Invalid point compression");
            }
            return this.createRawPoint(x, y);
        }

        protected ECFieldElement solveQuadraticEquation(ECFieldElement beta) {
            ECFieldElement z;
            ECFieldElement gamma;
            ECFieldElement.AbstractF2m betaF2m = (ECFieldElement.AbstractF2m)beta;
            boolean fastTrace = betaF2m.hasFastTrace();
            if (fastTrace && 0 != betaF2m.trace()) {
                return null;
            }
            int m = this.getFieldSize();
            if (0 != (m & 1)) {
                ECFieldElement r = betaF2m.halfTrace();
                if (fastTrace || r.square().add(r).add(beta).isZero()) {
                    return r;
                }
                return null;
            }
            if (beta.isZero()) {
                return beta;
            }
            ECFieldElement zeroElement = this.fromBigInteger(ECConstants.ZERO);
            Random rand = new Random();
            do {
                ECFieldElement t = this.fromBigInteger(new BigInteger(m, rand));
                z = zeroElement;
                ECFieldElement w = beta;
                for (int i = 1; i < m; ++i) {
                    ECFieldElement w2 = w.square();
                    z = z.square().add(w2.multiply(t));
                    w = w2.add(beta);
                }
                if (w.isZero()) continue;
                return null;
            } while ((gamma = z.square().add(z)).isZero());
            return z;
        }

        synchronized BigInteger[] getSi() {
            if (this.si == null) {
                this.si = Tnaf.getSi(this);
            }
            return this.si;
        }

        public boolean isKoblitz() {
            return this.order != null && this.cofactor != null && this.b.isOne() && (this.a.isZero() || this.a.isOne());
        }

        private static BigInteger implRandomFieldElementMult(SecureRandom r, int m) {
            BigInteger x;
            while ((x = BigIntegers.createRandomBigInteger(m, r)).signum() <= 0) {
            }
            return x;
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static abstract class AbstractFp
    extends ECCurve {
        protected AbstractFp(BigInteger q) {
            super(FiniteFields.getPrimeField(q));
        }

        @Override
        public boolean isValidFieldElement(BigInteger x) {
            return x != null && x.signum() >= 0 && x.compareTo(this.getField().getCharacteristic()) < 0;
        }

        @Override
        public ECFieldElement randomFieldElement(SecureRandom r) {
            BigInteger p = this.getField().getCharacteristic();
            ECFieldElement fe1 = this.fromBigInteger(AbstractFp.implRandomFieldElement(r, p));
            ECFieldElement fe2 = this.fromBigInteger(AbstractFp.implRandomFieldElement(r, p));
            return fe1.multiply(fe2);
        }

        @Override
        public ECFieldElement randomFieldElementMult(SecureRandom r) {
            BigInteger p = this.getField().getCharacteristic();
            ECFieldElement fe1 = this.fromBigInteger(AbstractFp.implRandomFieldElementMult(r, p));
            ECFieldElement fe2 = this.fromBigInteger(AbstractFp.implRandomFieldElementMult(r, p));
            return fe1.multiply(fe2);
        }

        @Override
        protected ECPoint decompressPoint(int yTilde, BigInteger X1) {
            ECFieldElement x = this.fromBigInteger(X1);
            ECFieldElement rhs = x.square().add(this.a).multiply(x).add(this.b);
            ECFieldElement y = rhs.sqrt();
            if (y == null) {
                throw new IllegalArgumentException("Invalid point compression");
            }
            if (y.testBitZero() != (yTilde == 1)) {
                y = y.negate();
            }
            return this.createRawPoint(x, y);
        }

        private static BigInteger implRandomFieldElement(SecureRandom r, BigInteger p) {
            BigInteger x;
            while ((x = BigIntegers.createRandomBigInteger(p.bitLength(), r)).compareTo(p) >= 0) {
            }
            return x;
        }

        private static BigInteger implRandomFieldElementMult(SecureRandom r, BigInteger p) {
            BigInteger x;
            while ((x = BigIntegers.createRandomBigInteger(p.bitLength(), r)).signum() <= 0 || x.compareTo(p) >= 0) {
            }
            return x;
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public class Config {
        protected int coord;
        protected ECEndomorphism endomorphism;
        protected ECMultiplier multiplier;

        Config(int coord, ECEndomorphism endomorphism, ECMultiplier multiplier) {
            this.coord = coord;
            this.endomorphism = endomorphism;
            this.multiplier = multiplier;
        }

        public Config setCoordinateSystem(int coord) {
            this.coord = coord;
            return this;
        }

        public Config setEndomorphism(ECEndomorphism endomorphism) {
            this.endomorphism = endomorphism;
            return this;
        }

        public Config setMultiplier(ECMultiplier multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public ECCurve create() {
            if (!ECCurve.this.supportsCoordinateSystem(this.coord)) {
                throw new IllegalStateException("unsupported coordinate system");
            }
            ECCurve c = ECCurve.this.cloneCurve();
            if (c == ECCurve.this) {
                throw new IllegalStateException("implementation returned current curve");
            }
            ECCurve eCCurve = c;
            synchronized (eCCurve) {
                c.coord = this.coord;
                c.endomorphism = this.endomorphism;
                c.multiplier = this.multiplier;
            }
            return c;
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class F2m
    extends AbstractF2m {
        private static final int F2M_DEFAULT_COORDS = 6;
        private int m;
        private int k1;
        private int k2;
        private int k3;
        private ECPoint.F2m infinity;

        public F2m(int m, int k, BigInteger a, BigInteger b, BigInteger order, BigInteger cofactor) {
            this(m, k, 0, 0, a, b, order, cofactor);
        }

        public F2m(int m, int k1, int k2, int k3, BigInteger a, BigInteger b, BigInteger order, BigInteger cofactor) {
            super(m, k1, k2, k3);
            this.m = m;
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.order = order;
            this.cofactor = cofactor;
            this.infinity = new ECPoint.F2m(this, null, null);
            this.a = this.fromBigInteger(a);
            this.b = this.fromBigInteger(b);
            this.coord = 6;
        }

        protected F2m(int m, int k1, int k2, int k3, ECFieldElement a, ECFieldElement b, BigInteger order, BigInteger cofactor) {
            super(m, k1, k2, k3);
            this.m = m;
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.order = order;
            this.cofactor = cofactor;
            this.infinity = new ECPoint.F2m(this, null, null);
            this.a = a;
            this.b = b;
            this.coord = 6;
        }

        @Override
        protected ECCurve cloneCurve() {
            return new F2m(this.m, this.k1, this.k2, this.k3, this.a, this.b, this.order, this.cofactor);
        }

        @Override
        public boolean supportsCoordinateSystem(int coord) {
            switch (coord) {
                case 0: 
                case 1: 
                case 6: {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected ECMultiplier createDefaultMultiplier() {
            if (this.isKoblitz()) {
                return new WTauNafMultiplier();
            }
            return super.createDefaultMultiplier();
        }

        @Override
        public int getFieldSize() {
            return this.m;
        }

        @Override
        public ECFieldElement fromBigInteger(BigInteger x) {
            int[] nArray;
            if (x == null || x.signum() < 0 || x.bitLength() > this.m) {
                throw new IllegalArgumentException("x value invalid in F2m field element");
            }
            if ((this.k2 | this.k3) == 0) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = this.k1;
            } else {
                int[] nArray3 = new int[3];
                nArray3[0] = this.k1;
                nArray3[1] = this.k2;
                nArray = nArray3;
                nArray3[2] = this.k3;
            }
            int[] ks = nArray;
            return new ECFieldElement.F2m(this.m, ks, new LongArray(x));
        }

        @Override
        protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
            return new ECPoint.F2m(this, x, y);
        }

        @Override
        protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
            return new ECPoint.F2m(this, x, y, zs);
        }

        @Override
        public ECPoint getInfinity() {
            return this.infinity;
        }

        public int getM() {
            return this.m;
        }

        public boolean isTrinomial() {
            return this.k2 == 0 && this.k3 == 0;
        }

        public int getK1() {
            return this.k1;
        }

        public int getK2() {
            return this.k2;
        }

        public int getK3() {
            return this.k3;
        }

        @Override
        public ECLookupTable createCacheSafeLookupTable(ECPoint[] points, int off, final int len) {
            int[] nArray;
            final int FE_LONGS = this.m + 63 >>> 6;
            if (this.isTrinomial()) {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = this.k1;
            } else {
                int[] nArray3 = new int[3];
                nArray3[0] = this.k1;
                nArray3[1] = this.k2;
                nArray = nArray3;
                nArray3[2] = this.k3;
            }
            final int[] ks = nArray;
            final long[] table = new long[len * FE_LONGS * 2];
            int pos = 0;
            for (int i = 0; i < len; ++i) {
                ECPoint p = points[off + i];
                ((ECFieldElement.F2m)p.getRawXCoord()).x.copyTo(table, pos);
                ((ECFieldElement.F2m)p.getRawYCoord()).x.copyTo(table, pos += FE_LONGS);
                pos += FE_LONGS;
            }
            return new AbstractECLookupTable(){

                @Override
                public int getSize() {
                    return len;
                }

                @Override
                public ECPoint lookup(int index) {
                    long[] x = Nat.create64(FE_LONGS);
                    long[] y = Nat.create64(FE_LONGS);
                    int pos = 0;
                    for (int i = 0; i < len; ++i) {
                        long MASK = (i ^ index) - 1 >> 31;
                        for (int j = 0; j < FE_LONGS; ++j) {
                            int n = j;
                            x[n] = x[n] ^ table[pos + j] & MASK;
                            int n2 = j;
                            y[n2] = y[n2] ^ table[pos + FE_LONGS + j] & MASK;
                        }
                        pos += FE_LONGS * 2;
                    }
                    return this.createPoint(x, y);
                }

                @Override
                public ECPoint lookupVar(int index) {
                    long[] x = Nat.create64(FE_LONGS);
                    long[] y = Nat.create64(FE_LONGS);
                    int pos = index * FE_LONGS * 2;
                    for (int j = 0; j < FE_LONGS; ++j) {
                        x[j] = table[pos + j];
                        y[j] = table[pos + FE_LONGS + j];
                    }
                    return this.createPoint(x, y);
                }

                private ECPoint createPoint(long[] x, long[] y) {
                    ECFieldElement.F2m X = new ECFieldElement.F2m(m, ks, new LongArray(x));
                    ECFieldElement.F2m Y = new ECFieldElement.F2m(m, ks, new LongArray(y));
                    return this.createRawPoint(X, Y);
                }
            };
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class Fp
    extends AbstractFp {
        private static final int FP_DEFAULT_COORDS = 4;
        private static final Set<BigInteger> knownQs = Collections.synchronizedSet(new HashSet());
        private static final BigIntegers.Cache validatedQs = new BigIntegers.Cache();
        BigInteger q;
        BigInteger r;
        ECPoint.Fp infinity;

        public Fp(BigInteger q, BigInteger a, BigInteger b, BigInteger order, BigInteger cofactor) {
            this(q, a, b, order, cofactor, false);
        }

        public Fp(BigInteger q, BigInteger a, BigInteger b, BigInteger order, BigInteger cofactor, boolean isInternal) {
            super(q);
            if (isInternal) {
                this.q = q;
                knownQs.add(q);
            } else if (knownQs.contains(q) || validatedQs.contains(q)) {
                this.q = q;
            } else {
                int maxBitLength = Properties.asInteger("org.bouncycastle.ec.fp_max_size", 1042);
                int certainty = Properties.asInteger("org.bouncycastle.ec.fp_certainty", 100);
                int qBitLength = q.bitLength();
                if (maxBitLength < qBitLength) {
                    throw new IllegalArgumentException("Fp q value out of range");
                }
                if (Primes.hasAnySmallFactors(q) || !Primes.isMRProbablePrime(q, CryptoServicesRegistrar.getSecureRandom(), ECCurve.getNumberOfIterations(qBitLength, certainty))) {
                    throw new IllegalArgumentException("Fp q value not prime");
                }
                validatedQs.add(q);
                this.q = q;
            }
            this.r = ECFieldElement.Fp.calculateResidue(q);
            this.infinity = new ECPoint.Fp(this, null, null);
            this.a = this.fromBigInteger(a);
            this.b = this.fromBigInteger(b);
            this.order = order;
            this.cofactor = cofactor;
            this.coord = 4;
        }

        protected Fp(BigInteger q, BigInteger r, ECFieldElement a, ECFieldElement b, BigInteger order, BigInteger cofactor) {
            super(q);
            this.q = q;
            this.r = r;
            this.infinity = new ECPoint.Fp(this, null, null);
            this.a = a;
            this.b = b;
            this.order = order;
            this.cofactor = cofactor;
            this.coord = 4;
        }

        @Override
        protected ECCurve cloneCurve() {
            return new Fp(this.q, this.r, this.a, this.b, this.order, this.cofactor);
        }

        @Override
        public boolean supportsCoordinateSystem(int coord) {
            switch (coord) {
                case 0: 
                case 1: 
                case 2: 
                case 4: {
                    return true;
                }
            }
            return false;
        }

        public BigInteger getQ() {
            return this.q;
        }

        @Override
        public int getFieldSize() {
            return this.q.bitLength();
        }

        @Override
        public ECFieldElement fromBigInteger(BigInteger x) {
            if (x == null || x.signum() < 0 || x.compareTo(this.q) >= 0) {
                throw new IllegalArgumentException("x value invalid for Fp field element");
            }
            return new ECFieldElement.Fp(this.q, this.r, x);
        }

        @Override
        protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y) {
            return new ECPoint.Fp(this, x, y);
        }

        @Override
        protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs) {
            return new ECPoint.Fp(this, x, y, zs);
        }

        @Override
        public ECPoint importPoint(ECPoint p) {
            if (this != p.getCurve() && this.getCoordinateSystem() == 2 && !p.isInfinity()) {
                switch (p.getCurve().getCoordinateSystem()) {
                    case 2: 
                    case 3: 
                    case 4: {
                        return new ECPoint.Fp(this, this.fromBigInteger(p.x.toBigInteger()), this.fromBigInteger(p.y.toBigInteger()), new ECFieldElement[]{this.fromBigInteger(p.zs[0].toBigInteger())});
                    }
                }
            }
            return super.importPoint(p);
        }

        @Override
        public ECPoint getInfinity() {
            return this.infinity;
        }
    }
}

