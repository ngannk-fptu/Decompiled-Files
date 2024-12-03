/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHKeyParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Integers;

public class DHPublicKeyParameters
extends DHKeyParameters {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private BigInteger y;

    public DHPublicKeyParameters(BigInteger y, DHParameters params) {
        super(false, params);
        this.y = this.validate(y, params);
    }

    private BigInteger validate(BigInteger y, DHParameters dhParams) {
        if (y == null) {
            throw new NullPointerException("y value cannot be null");
        }
        BigInteger p = dhParams.getP();
        if (y.compareTo(TWO) < 0 || y.compareTo(p.subtract(TWO)) > 0) {
            throw new IllegalArgumentException("invalid DH public key");
        }
        BigInteger q = dhParams.getQ();
        if (q == null) {
            return y;
        }
        if (p.testBit(0) && p.bitLength() - 1 == q.bitLength() && p.shiftRight(1).equals(q) ? 1 == DHPublicKeyParameters.legendre(y, p) : ONE.equals(y.modPow(q, p))) {
            return y;
        }
        throw new IllegalArgumentException("Y value does not appear to be in correct group");
    }

    public BigInteger getY() {
        return this.y;
    }

    @Override
    public int hashCode() {
        return this.y.hashCode() ^ super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DHPublicKeyParameters)) {
            return false;
        }
        DHPublicKeyParameters other = (DHPublicKeyParameters)obj;
        return other.getY().equals(this.y) && super.equals(obj);
    }

    private static int legendre(BigInteger a, BigInteger b) {
        int bitLength = b.bitLength();
        int[] A = Nat.fromBigInteger(bitLength, a);
        int[] B = Nat.fromBigInteger(bitLength, b);
        int r = 0;
        int len = B.length;
        while (true) {
            int cmp;
            if (A[0] == 0) {
                Nat.shiftDownWord(len, A, 0);
                continue;
            }
            int shift = Integers.numberOfTrailingZeros(A[0]);
            if (shift > 0) {
                Nat.shiftDownBits(len, A, shift, 0);
                int bits = B[0];
                r ^= (bits ^ bits >>> 1) & shift << 1;
            }
            if ((cmp = Nat.compare(len, A, B)) == 0) break;
            if (cmp < 0) {
                r ^= A[0] & B[0];
                int[] t = A;
                A = B;
                B = t;
            }
            while (A[len - 1] == 0) {
                --len;
            }
            Nat.sub(len, A, B, A);
        }
        return Nat.isOne(len, B) ? 1 - (r & 2) : 0;
    }
}

