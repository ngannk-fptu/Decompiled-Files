/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public final class BigIntegers {
    public static final BigInteger ZERO = BigInteger.valueOf(0L);
    public static final BigInteger ONE = BigInteger.valueOf(1L);
    public static final BigInteger TWO = BigInteger.valueOf(2L);
    private static final BigInteger THREE = BigInteger.valueOf(3L);
    private static final int MAX_ITERATIONS = 1000;
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);
    private static final int MAX_SMALL = BigInteger.valueOf(743L).bitLength();

    public static byte[] asUnsignedByteArray(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes[0] == 0 && bytes.length != 1) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return bytes;
    }

    public static byte[] asUnsignedByteArray(int length, BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length == length) {
            return bytes;
        }
        int start = bytes[0] == 0 && bytes.length != 1 ? 1 : 0;
        int count = bytes.length - start;
        if (count > length) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        byte[] tmp = new byte[length];
        System.arraycopy(bytes, start, tmp, tmp.length - count, count);
        return tmp;
    }

    public static void asUnsignedByteArray(BigInteger value, byte[] buf, int off, int len) {
        byte[] bytes = value.toByteArray();
        if (bytes.length == len) {
            System.arraycopy(bytes, 0, buf, off, len);
            return;
        }
        int start = bytes[0] == 0 && bytes.length != 1 ? 1 : 0;
        int count = bytes.length - start;
        if (count > len) {
            throw new IllegalArgumentException("standard length exceeded for value");
        }
        int padLen = len - count;
        Arrays.fill(buf, off, off + padLen, (byte)0);
        System.arraycopy(bytes, start, buf, off + padLen, count);
    }

    public static BigInteger createRandomInRange(BigInteger min, BigInteger max, SecureRandom random) {
        int cmp = min.compareTo(max);
        if (cmp >= 0) {
            if (cmp > 0) {
                throw new IllegalArgumentException("'min' may not be greater than 'max'");
            }
            return min;
        }
        if (min.bitLength() > max.bitLength() / 2) {
            return BigIntegers.createRandomInRange(ZERO, max.subtract(min), random).add(min);
        }
        for (int i = 0; i < 1000; ++i) {
            BigInteger x = BigIntegers.createRandomBigInteger(max.bitLength(), random);
            if (x.compareTo(min) < 0 || x.compareTo(max) > 0) continue;
            return x;
        }
        return BigIntegers.createRandomBigInteger(max.subtract(min).bitLength() - 1, random).add(min);
    }

    public static BigInteger fromUnsignedByteArray(byte[] buf) {
        return new BigInteger(1, buf);
    }

    public static BigInteger fromUnsignedByteArray(byte[] buf, int off, int length) {
        byte[] mag = buf;
        if (off != 0 || length != buf.length) {
            mag = new byte[length];
            System.arraycopy(buf, off, mag, 0, length);
        }
        return new BigInteger(1, mag);
    }

    public static byte byteValueExact(BigInteger x) {
        if (x.bitLength() > 7) {
            throw new ArithmeticException("BigInteger out of int range");
        }
        return x.byteValue();
    }

    public static short shortValueExact(BigInteger x) {
        if (x.bitLength() > 15) {
            throw new ArithmeticException("BigInteger out of int range");
        }
        return x.shortValue();
    }

    public static int intValueExact(BigInteger x) {
        if (x.bitLength() > 31) {
            throw new ArithmeticException("BigInteger out of int range");
        }
        return x.intValue();
    }

    public static long longValueExact(BigInteger x) {
        if (x.bitLength() > 63) {
            throw new ArithmeticException("BigInteger out of long range");
        }
        return x.longValue();
    }

    public static BigInteger modOddInverse(BigInteger M, BigInteger X) {
        int len;
        int[] z;
        int[] x;
        int bits;
        int[] m;
        if (!M.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (M.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (X.signum() < 0 || X.compareTo(M) >= 0) {
            X = X.mod(M);
        }
        if (0 == Mod.modOddInverse(m = Nat.fromBigInteger(bits = M.bitLength(), M), x = Nat.fromBigInteger(bits, X), z = Nat.create(len = m.length))) {
            throw new ArithmeticException("BigInteger not invertible.");
        }
        return Nat.toBigInteger(len, z);
    }

    public static BigInteger modOddInverseVar(BigInteger M, BigInteger X) {
        int len;
        int[] z;
        int[] x;
        if (!M.testBit(0)) {
            throw new IllegalArgumentException("'M' must be odd");
        }
        if (M.signum() != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (M.equals(ONE)) {
            return ZERO;
        }
        if (X.signum() < 0 || X.compareTo(M) >= 0) {
            X = X.mod(M);
        }
        if (X.equals(ONE)) {
            return ONE;
        }
        int bits = M.bitLength();
        int[] m = Nat.fromBigInteger(bits, M);
        if (!Mod.modOddInverseVar(m, x = Nat.fromBigInteger(bits, X), z = Nat.create(len = m.length))) {
            throw new ArithmeticException("BigInteger not invertible.");
        }
        return Nat.toBigInteger(len, z);
    }

    public static int getUnsignedByteLength(BigInteger n) {
        if (n.equals(ZERO)) {
            return 1;
        }
        return (n.bitLength() + 7) / 8;
    }

    public static BigInteger createRandomBigInteger(int bitLength, SecureRandom random) {
        return new BigInteger(1, BigIntegers.createRandom(bitLength, random));
    }

    public static BigInteger createRandomPrime(int bitLength, int certainty, SecureRandom random) {
        BigInteger rv;
        if (bitLength < 2) {
            throw new IllegalArgumentException("bitLength < 2");
        }
        if (bitLength == 2) {
            return random.nextInt() < 0 ? TWO : THREE;
        }
        do {
            byte[] base = BigIntegers.createRandom(bitLength, random);
            int xBits = 8 * base.length - bitLength;
            byte lead = (byte)(1 << 7 - xBits);
            base[0] = (byte)(base[0] | lead);
            int n = base.length - 1;
            base[n] = (byte)(base[n] | 1);
            rv = new BigInteger(1, base);
            if (bitLength <= MAX_SMALL) continue;
            while (!rv.gcd(SMALL_PRIMES_PRODUCT).equals(ONE)) {
                rv = rv.add(TWO);
            }
        } while (!rv.isProbablePrime(certainty));
        return rv;
    }

    private static byte[] createRandom(int bitLength, SecureRandom random) throws IllegalArgumentException {
        if (bitLength < 1) {
            throw new IllegalArgumentException("bitLength must be at least 1");
        }
        int nBytes = (bitLength + 7) / 8;
        byte[] rv = new byte[nBytes];
        random.nextBytes(rv);
        int xBits = 8 * nBytes - bitLength;
        rv[0] = (byte)(rv[0] & (byte)(255 >>> xBits));
        return rv;
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class Cache {
        private final Map<BigInteger, Boolean> values = new WeakHashMap<BigInteger, Boolean>();
        private final BigInteger[] preserve = new BigInteger[8];
        private int preserveCounter = 0;

        public synchronized void add(BigInteger value) {
            this.values.put(value, Boolean.TRUE);
            this.preserve[this.preserveCounter] = value;
            this.preserveCounter = (this.preserveCounter + 1) % this.preserve.length;
        }

        public synchronized boolean contains(BigInteger value) {
            return this.values.containsKey(value);
        }

        public synchronized int size() {
            return this.values.size();
        }

        public synchronized void clear() {
            this.values.clear();
            for (int i = 0; i != this.preserve.length; ++i) {
                this.preserve[i] = null;
            }
        }
    }
}

