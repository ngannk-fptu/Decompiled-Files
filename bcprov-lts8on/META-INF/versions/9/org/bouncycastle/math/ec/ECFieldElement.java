/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import java.util.Random;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.LongArray;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ECFieldElement
implements ECConstants {
    public abstract BigInteger toBigInteger();

    public abstract String getFieldName();

    public abstract int getFieldSize();

    public abstract ECFieldElement add(ECFieldElement var1);

    public abstract ECFieldElement addOne();

    public abstract ECFieldElement subtract(ECFieldElement var1);

    public abstract ECFieldElement multiply(ECFieldElement var1);

    public abstract ECFieldElement divide(ECFieldElement var1);

    public abstract ECFieldElement negate();

    public abstract ECFieldElement square();

    public abstract ECFieldElement invert();

    public abstract ECFieldElement sqrt();

    public int bitLength() {
        return this.toBigInteger().bitLength();
    }

    public boolean isOne() {
        return this.bitLength() == 1;
    }

    public boolean isZero() {
        return 0 == this.toBigInteger().signum();
    }

    public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiply(b).subtract(x.multiply(y));
    }

    public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
        return this.multiply(b).add(x.multiply(y));
    }

    public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
        return this.square().subtract(x.multiply(y));
    }

    public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
        return this.square().add(x.multiply(y));
    }

    public ECFieldElement squarePow(int pow) {
        ECFieldElement r = this;
        for (int i = 0; i < pow; ++i) {
            r = r.square();
        }
        return r;
    }

    public boolean testBitZero() {
        return this.toBigInteger().testBit(0);
    }

    public String toString() {
        return this.toBigInteger().toString(16);
    }

    public byte[] getEncoded() {
        return BigIntegers.asUnsignedByteArray((this.getFieldSize() + 7) / 8, this.toBigInteger());
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class F2m
    extends AbstractF2m {
        public static final int GNB = 1;
        public static final int TPB = 2;
        public static final int PPB = 3;
        private int representation;
        private int m;
        private int[] ks;
        LongArray x;

        F2m(int m, int[] ks, LongArray x) {
            this.m = m;
            this.representation = ks.length == 1 ? 2 : 3;
            this.ks = ks;
            this.x = x;
        }

        @Override
        public int bitLength() {
            return this.x.degree();
        }

        @Override
        public boolean isOne() {
            return this.x.isOne();
        }

        @Override
        public boolean isZero() {
            return this.x.isZero();
        }

        @Override
        public boolean testBitZero() {
            return this.x.testBitZero();
        }

        @Override
        public BigInteger toBigInteger() {
            return this.x.toBigInteger();
        }

        @Override
        public String getFieldName() {
            return "F2m";
        }

        @Override
        public int getFieldSize() {
            return this.m;
        }

        @Override
        public ECFieldElement add(ECFieldElement b) {
            LongArray iarrClone = (LongArray)this.x.clone();
            F2m bF2m = (F2m)b;
            iarrClone.addShiftedByWords(bF2m.x, 0);
            return new F2m(this.m, this.ks, iarrClone);
        }

        @Override
        public ECFieldElement addOne() {
            return new F2m(this.m, this.ks, this.x.addOne());
        }

        @Override
        public ECFieldElement subtract(ECFieldElement b) {
            return this.add(b);
        }

        @Override
        public ECFieldElement multiply(ECFieldElement b) {
            return new F2m(this.m, this.ks, this.x.modMultiply(((F2m)b).x, this.m, this.ks));
        }

        @Override
        public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            return this.multiplyPlusProduct(b, x, y);
        }

        @Override
        public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            LongArray ax = this.x;
            LongArray bx = ((F2m)b).x;
            LongArray xx = ((F2m)x).x;
            LongArray yx = ((F2m)y).x;
            LongArray ab = ax.multiply(bx, this.m, this.ks);
            LongArray xy = xx.multiply(yx, this.m, this.ks);
            if (ab == ax || ab == bx) {
                ab = (LongArray)ab.clone();
            }
            ab.addShiftedByWords(xy, 0);
            ab.reduce(this.m, this.ks);
            return new F2m(this.m, this.ks, ab);
        }

        @Override
        public ECFieldElement divide(ECFieldElement b) {
            ECFieldElement bInv = b.invert();
            return this.multiply(bInv);
        }

        @Override
        public ECFieldElement negate() {
            return this;
        }

        @Override
        public ECFieldElement square() {
            return new F2m(this.m, this.ks, this.x.modSquare(this.m, this.ks));
        }

        @Override
        public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
            return this.squarePlusProduct(x, y);
        }

        @Override
        public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
            LongArray ax = this.x;
            LongArray xx = ((F2m)x).x;
            LongArray yx = ((F2m)y).x;
            LongArray aa = ax.square(this.m, this.ks);
            LongArray xy = xx.multiply(yx, this.m, this.ks);
            if (aa == ax) {
                aa = (LongArray)aa.clone();
            }
            aa.addShiftedByWords(xy, 0);
            aa.reduce(this.m, this.ks);
            return new F2m(this.m, this.ks, aa);
        }

        @Override
        public ECFieldElement squarePow(int pow) {
            return pow < 1 ? this : new F2m(this.m, this.ks, this.x.modSquareN(pow, this.m, this.ks));
        }

        @Override
        public ECFieldElement invert() {
            return new F2m(this.m, this.ks, this.x.modInverse(this.m, this.ks));
        }

        @Override
        public ECFieldElement sqrt() {
            return this.x.isZero() || this.x.isOne() ? this : this.squarePow(this.m - 1);
        }

        public int getRepresentation() {
            return this.representation;
        }

        public int getM() {
            return this.m;
        }

        public int getK1() {
            return this.ks[0];
        }

        public int getK2() {
            return this.ks.length >= 2 ? this.ks[1] : 0;
        }

        public int getK3() {
            return this.ks.length >= 3 ? this.ks[2] : 0;
        }

        public boolean equals(Object anObject) {
            if (anObject == this) {
                return true;
            }
            if (!(anObject instanceof F2m)) {
                return false;
            }
            F2m b = (F2m)anObject;
            return this.m == b.m && this.representation == b.representation && Arrays.areEqual(this.ks, b.ks) && this.x.equals(b.x);
        }

        public int hashCode() {
            return this.x.hashCode() ^ this.m ^ Arrays.hashCode(this.ks);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static abstract class AbstractF2m
    extends ECFieldElement {
        public ECFieldElement halfTrace() {
            int m = this.getFieldSize();
            if ((m & 1) == 0) {
                throw new IllegalStateException("Half-trace only defined for odd m");
            }
            int n = m + 1 >>> 1;
            int k = 31 - Integers.numberOfLeadingZeros(n);
            int nk = 1;
            ECFieldElement ht = this;
            while (k > 0) {
                ht = ht.squarePow(nk << 1).add(ht);
                if (0 == ((nk = n >>> --k) & 1)) continue;
                ht = ht.squarePow(2).add(this);
            }
            return ht;
        }

        public boolean hasFastTrace() {
            return false;
        }

        public int trace() {
            int m = this.getFieldSize();
            int k = 31 - Integers.numberOfLeadingZeros(m);
            int mk = 1;
            ECFieldElement tr = this;
            while (k > 0) {
                tr = tr.squarePow(mk).add(tr);
                if (0 == ((mk = m >>> --k) & 1)) continue;
                tr = tr.square().add(this);
            }
            if (tr.isZero()) {
                return 0;
            }
            if (tr.isOne()) {
                return 1;
            }
            throw new IllegalStateException("Internal error in trace calculation");
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class Fp
    extends AbstractFp {
        BigInteger q;
        BigInteger r;
        BigInteger x;

        static BigInteger calculateResidue(BigInteger p) {
            BigInteger firstWord;
            int bitLength = p.bitLength();
            if (bitLength >= 96 && (firstWord = p.shiftRight(bitLength - 64)).longValue() == -1L) {
                return ONE.shiftLeft(bitLength).subtract(p);
            }
            return null;
        }

        Fp(BigInteger q, BigInteger r, BigInteger x) {
            this.q = q;
            this.r = r;
            this.x = x;
        }

        @Override
        public BigInteger toBigInteger() {
            return this.x;
        }

        @Override
        public String getFieldName() {
            return "Fp";
        }

        @Override
        public int getFieldSize() {
            return this.q.bitLength();
        }

        public BigInteger getQ() {
            return this.q;
        }

        @Override
        public ECFieldElement add(ECFieldElement b) {
            return new Fp(this.q, this.r, this.modAdd(this.x, b.toBigInteger()));
        }

        @Override
        public ECFieldElement addOne() {
            BigInteger x2 = this.x.add(ECConstants.ONE);
            if (x2.compareTo(this.q) == 0) {
                x2 = ECConstants.ZERO;
            }
            return new Fp(this.q, this.r, x2);
        }

        @Override
        public ECFieldElement subtract(ECFieldElement b) {
            return new Fp(this.q, this.r, this.modSubtract(this.x, b.toBigInteger()));
        }

        @Override
        public ECFieldElement multiply(ECFieldElement b) {
            return new Fp(this.q, this.r, this.modMult(this.x, b.toBigInteger()));
        }

        @Override
        public ECFieldElement multiplyMinusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.x;
            BigInteger bx = b.toBigInteger();
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger ab = ax.multiply(bx);
            BigInteger xy = xx.multiply(yx);
            return new Fp(this.q, this.r, this.modReduce(ab.subtract(xy)));
        }

        @Override
        public ECFieldElement multiplyPlusProduct(ECFieldElement b, ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.x;
            BigInteger bx = b.toBigInteger();
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger ab = ax.multiply(bx);
            BigInteger xy = xx.multiply(yx);
            return new Fp(this.q, this.r, this.modReduce(ab.add(xy)));
        }

        @Override
        public ECFieldElement divide(ECFieldElement b) {
            return new Fp(this.q, this.r, this.modMult(this.x, this.modInverse(b.toBigInteger())));
        }

        @Override
        public ECFieldElement negate() {
            return this.x.signum() == 0 ? this : new Fp(this.q, this.r, this.q.subtract(this.x));
        }

        @Override
        public ECFieldElement square() {
            return new Fp(this.q, this.r, this.modMult(this.x, this.x));
        }

        @Override
        public ECFieldElement squareMinusProduct(ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.x;
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger aa = ax.multiply(ax);
            BigInteger xy = xx.multiply(yx);
            return new Fp(this.q, this.r, this.modReduce(aa.subtract(xy)));
        }

        @Override
        public ECFieldElement squarePlusProduct(ECFieldElement x, ECFieldElement y) {
            BigInteger ax = this.x;
            BigInteger xx = x.toBigInteger();
            BigInteger yx = y.toBigInteger();
            BigInteger aa = ax.multiply(ax);
            BigInteger xy = xx.multiply(yx);
            return new Fp(this.q, this.r, this.modReduce(aa.add(xy)));
        }

        @Override
        public ECFieldElement invert() {
            return new Fp(this.q, this.r, this.modInverse(this.x));
        }

        @Override
        public ECFieldElement sqrt() {
            if (this.isZero() || this.isOne()) {
                return this;
            }
            if (!this.q.testBit(0)) {
                throw new RuntimeException("not done yet");
            }
            if (this.q.testBit(1)) {
                BigInteger e = this.q.shiftRight(2).add(ECConstants.ONE);
                return this.checkSqrt(new Fp(this.q, this.r, this.x.modPow(e, this.q)));
            }
            if (this.q.testBit(2)) {
                BigInteger t1 = this.x.modPow(this.q.shiftRight(3), this.q);
                BigInteger t2 = this.modMult(t1, this.x);
                BigInteger t3 = this.modMult(t2, t1);
                if (t3.equals(ECConstants.ONE)) {
                    return this.checkSqrt(new Fp(this.q, this.r, t2));
                }
                BigInteger t4 = ECConstants.TWO.modPow(this.q.shiftRight(2), this.q);
                BigInteger y = this.modMult(t2, t4);
                return this.checkSqrt(new Fp(this.q, this.r, y));
            }
            BigInteger legendreExponent = this.q.shiftRight(1);
            if (!this.x.modPow(legendreExponent, this.q).equals(ECConstants.ONE)) {
                return null;
            }
            BigInteger X = this.x;
            BigInteger fourX = this.modDouble(this.modDouble(X));
            BigInteger k = legendreExponent.add(ECConstants.ONE);
            BigInteger qMinusOne = this.q.subtract(ECConstants.ONE);
            Random rand = new Random();
            while (true) {
                BigInteger P;
                if ((P = new BigInteger(this.q.bitLength(), rand)).compareTo(this.q) >= 0 || !this.modReduce(P.multiply(P).subtract(fourX)).modPow(legendreExponent, this.q).equals(qMinusOne)) {
                    continue;
                }
                BigInteger[] result = this.lucasSequence(P, X, k);
                BigInteger U = result[0];
                BigInteger V = result[1];
                if (this.modMult(V, V).equals(fourX)) {
                    return new Fp(this.q, this.r, this.modHalfAbs(V));
                }
                if (!U.equals(ECConstants.ONE) && !U.equals(qMinusOne)) break;
            }
            return null;
        }

        private ECFieldElement checkSqrt(ECFieldElement z) {
            return z.square().equals(this) ? z : null;
        }

        private BigInteger[] lucasSequence(BigInteger P, BigInteger Q, BigInteger k) {
            int j;
            int n = k.bitLength();
            int s = k.getLowestSetBit();
            BigInteger Uh = ECConstants.ONE;
            BigInteger Vl = ECConstants.TWO;
            BigInteger Vh = P;
            BigInteger Ql = ECConstants.ONE;
            BigInteger Qh = ECConstants.ONE;
            for (j = n - 1; j >= s + 1; --j) {
                Ql = this.modMult(Ql, Qh);
                if (k.testBit(j)) {
                    Qh = this.modMult(Ql, Q);
                    Uh = this.modMult(Uh, Vh);
                    Vl = this.modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)));
                    Vh = this.modReduce(Vh.multiply(Vh).subtract(Qh.shiftLeft(1)));
                    continue;
                }
                Qh = Ql;
                Uh = this.modReduce(Uh.multiply(Vl).subtract(Ql));
                Vh = this.modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)));
                Vl = this.modReduce(Vl.multiply(Vl).subtract(Ql.shiftLeft(1)));
            }
            Ql = this.modMult(Ql, Qh);
            Qh = this.modMult(Ql, Q);
            Uh = this.modReduce(Uh.multiply(Vl).subtract(Ql));
            Vl = this.modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)));
            Ql = this.modMult(Ql, Qh);
            for (j = 1; j <= s; ++j) {
                Uh = this.modMult(Uh, Vl);
                Vl = this.modReduce(Vl.multiply(Vl).subtract(Ql.shiftLeft(1)));
                Ql = this.modMult(Ql, Ql);
            }
            return new BigInteger[]{Uh, Vl};
        }

        protected BigInteger modAdd(BigInteger x1, BigInteger x2) {
            BigInteger x3 = x1.add(x2);
            if (x3.compareTo(this.q) >= 0) {
                x3 = x3.subtract(this.q);
            }
            return x3;
        }

        protected BigInteger modDouble(BigInteger x) {
            BigInteger _2x = x.shiftLeft(1);
            if (_2x.compareTo(this.q) >= 0) {
                _2x = _2x.subtract(this.q);
            }
            return _2x;
        }

        protected BigInteger modHalf(BigInteger x) {
            if (x.testBit(0)) {
                x = this.q.add(x);
            }
            return x.shiftRight(1);
        }

        protected BigInteger modHalfAbs(BigInteger x) {
            if (x.testBit(0)) {
                x = this.q.subtract(x);
            }
            return x.shiftRight(1);
        }

        protected BigInteger modInverse(BigInteger x) {
            return BigIntegers.modOddInverse(this.q, x);
        }

        protected BigInteger modMult(BigInteger x1, BigInteger x2) {
            return this.modReduce(x1.multiply(x2));
        }

        protected BigInteger modReduce(BigInteger x) {
            if (this.r != null) {
                boolean negative;
                boolean bl = negative = x.signum() < 0;
                if (negative) {
                    x = x.abs();
                }
                int qLen = this.q.bitLength();
                boolean rIsOne = this.r.equals(ECConstants.ONE);
                while (x.bitLength() > qLen + 1) {
                    BigInteger u = x.shiftRight(qLen);
                    BigInteger v = x.subtract(u.shiftLeft(qLen));
                    if (!rIsOne) {
                        u = u.multiply(this.r);
                    }
                    x = u.add(v);
                }
                while (x.compareTo(this.q) >= 0) {
                    x = x.subtract(this.q);
                }
                if (negative && x.signum() != 0) {
                    x = this.q.subtract(x);
                }
            } else {
                x = x.mod(this.q);
            }
            return x;
        }

        protected BigInteger modSubtract(BigInteger x1, BigInteger x2) {
            BigInteger x3 = x1.subtract(x2);
            if (x3.signum() < 0) {
                x3 = x3.add(this.q);
            }
            return x3;
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof Fp)) {
                return false;
            }
            Fp o = (Fp)other;
            return this.q.equals(o.q) && this.x.equals(o.x);
        }

        public int hashCode() {
            return this.q.hashCode() ^ this.x.hashCode();
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static abstract class AbstractFp
    extends ECFieldElement {
    }
}

