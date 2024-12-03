/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAValidationParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

public class DSAParametersGenerator {
    private static final BigInteger ZERO = BigInteger.valueOf(0L);
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private Digest digest;
    private int L;
    private int N;
    private int certainty;
    private int iterations;
    private SecureRandom random;
    private boolean use186_3;
    private int usageIndex;

    public DSAParametersGenerator() {
        this(DigestFactory.createSHA1());
    }

    public DSAParametersGenerator(Digest digest) {
        this.digest = digest;
    }

    public void init(int size, int certainty, SecureRandom random) {
        this.L = size;
        this.N = DSAParametersGenerator.getDefaultN(size);
        this.certainty = certainty;
        this.iterations = Math.max(DSAParametersGenerator.getMinimumIterations(this.L), (certainty + 1) / 2);
        this.random = random;
        this.use186_3 = false;
        this.usageIndex = -1;
    }

    public void init(DSAParameterGenerationParameters params) {
        int L = params.getL();
        int N = params.getN();
        if (L < 1024 || L > 3072 || L % 1024 != 0) {
            throw new IllegalArgumentException("L values must be between 1024 and 3072 and a multiple of 1024");
        }
        if (L == 1024 && N != 160) {
            throw new IllegalArgumentException("N must be 160 for L = 1024");
        }
        if (L == 2048 && N != 224 && N != 256) {
            throw new IllegalArgumentException("N must be 224 or 256 for L = 2048");
        }
        if (L == 3072 && N != 256) {
            throw new IllegalArgumentException("N must be 256 for L = 3072");
        }
        if (this.digest.getDigestSize() * 8 < N) {
            throw new IllegalStateException("Digest output size too small for value of N");
        }
        this.L = L;
        this.N = N;
        this.certainty = params.getCertainty();
        this.iterations = Math.max(DSAParametersGenerator.getMinimumIterations(L), (this.certainty + 1) / 2);
        this.random = params.getRandom();
        this.use186_3 = true;
        this.usageIndex = params.getUsageIndex();
    }

    public DSAParameters generateParameters() {
        return this.use186_3 ? this.generateParameters_FIPS186_3() : this.generateParameters_FIPS186_2();
    }

    /*
     * Unable to fully structure code
     */
    private DSAParameters generateParameters_FIPS186_2() {
        seed = new byte[20];
        part1 = new byte[20];
        part2 = new byte[20];
        u = new byte[20];
        n = (this.L - 1) / 160;
        w = new byte[this.L / 8];
        if (!(this.digest instanceof SHA1Digest)) {
            throw new IllegalStateException("can only use SHA-1 for generating FIPS 186-2 parameters");
        }
        block0: while (true) {
            this.random.nextBytes(seed);
            DSAParametersGenerator.hash(this.digest, seed, part1, 0);
            System.arraycopy(seed, 0, part2, 0, seed.length);
            DSAParametersGenerator.inc(part2);
            DSAParametersGenerator.hash(this.digest, part2, part2, 0);
            for (i = 0; i != u.length; ++i) {
                u[i] = (byte)(part1[i] ^ part2[i]);
            }
            u[0] = (byte)(u[0] | -128);
            u[19] = (byte)(u[19] | 1);
            q = new BigInteger(1, u);
            if (!this.isProbablePrime(q)) continue;
            offset = Arrays.clone(seed);
            DSAParametersGenerator.inc(offset);
            counter = 0;
            while (true) {
                if (counter < 4096) ** break;
                continue block0;
                for (k = 1; k <= n; ++k) {
                    DSAParametersGenerator.inc(offset);
                    DSAParametersGenerator.hash(this.digest, offset, w, w.length - k * part1.length);
                }
                remaining = w.length - n * part1.length;
                DSAParametersGenerator.inc(offset);
                DSAParametersGenerator.hash(this.digest, offset, part1, 0);
                System.arraycopy(part1, part1.length - remaining, w, 0, remaining);
                w[0] = (byte)(w[0] | -128);
                x = new BigInteger(1, w);
                c = x.mod(q.shiftLeft(1));
                p = x.subtract(c.subtract(DSAParametersGenerator.ONE));
                if (p.bitLength() == this.L && this.isProbablePrime(p)) {
                    g = DSAParametersGenerator.calculateGenerator_FIPS186_2(p, q, this.random);
                    return new DSAParameters(p, q, g, new DSAValidationParameters(seed, counter));
                }
                ++counter;
            }
            break;
        }
    }

    private static BigInteger calculateGenerator_FIPS186_2(BigInteger p, BigInteger q, SecureRandom r) {
        BigInteger h;
        BigInteger g;
        BigInteger e = p.subtract(ONE).divide(q);
        BigInteger pSub2 = p.subtract(TWO);
        while ((g = (h = BigIntegers.createRandomInRange(TWO, pSub2, r)).modPow(e, p)).bitLength() <= 1) {
        }
        return g;
    }

    /*
     * Unable to fully structure code
     */
    private DSAParameters generateParameters_FIPS186_3() {
        d = this.digest;
        outlen = d.getDigestSize() * 8;
        seedlen = this.N;
        seed = new byte[seedlen / 8];
        n = (this.L - 1) / outlen;
        b = (this.L - 1) % outlen;
        w = new byte[this.L / 8];
        output = new byte[d.getDigestSize()];
        block0: while (true) {
            this.random.nextBytes(seed);
            DSAParametersGenerator.hash(d, seed, output, 0);
            U = new BigInteger(1, output).mod(DSAParametersGenerator.ONE.shiftLeft(this.N - 1));
            q = U.setBit(0).setBit(this.N - 1);
            if (!this.isProbablePrime(q)) continue;
            offset = Arrays.clone(seed);
            counterLimit = 4 * this.L;
            counter = 0;
            while (true) {
                if (counter < counterLimit) ** break;
                continue block0;
                for (j = 1; j <= n; ++j) {
                    DSAParametersGenerator.inc(offset);
                    DSAParametersGenerator.hash(d, offset, w, w.length - j * output.length);
                }
                remaining = w.length - n * output.length;
                DSAParametersGenerator.inc(offset);
                DSAParametersGenerator.hash(d, offset, output, 0);
                System.arraycopy(output, output.length - remaining, w, 0, remaining);
                w[0] = (byte)(w[0] | -128);
                X = new BigInteger(1, w);
                c = X.mod(q.shiftLeft(1));
                p = X.subtract(c.subtract(DSAParametersGenerator.ONE));
                if (p.bitLength() == this.L && this.isProbablePrime(p)) {
                    if (this.usageIndex >= 0 && (g = DSAParametersGenerator.calculateGenerator_FIPS186_3_Verifiable(d, p, q, seed, this.usageIndex)) != null) {
                        return new DSAParameters(p, q, g, new DSAValidationParameters(seed, counter, this.usageIndex));
                    }
                    g = DSAParametersGenerator.calculateGenerator_FIPS186_3_Unverifiable(p, q, this.random);
                    return new DSAParameters(p, q, g, new DSAValidationParameters(seed, counter));
                }
                ++counter;
            }
            break;
        }
    }

    private boolean isProbablePrime(BigInteger x) {
        return x.isProbablePrime(this.certainty);
    }

    private static BigInteger calculateGenerator_FIPS186_3_Unverifiable(BigInteger p, BigInteger q, SecureRandom r) {
        return DSAParametersGenerator.calculateGenerator_FIPS186_2(p, q, r);
    }

    private static BigInteger calculateGenerator_FIPS186_3_Verifiable(Digest d, BigInteger p, BigInteger q, byte[] seed, int index) {
        BigInteger e = p.subtract(ONE).divide(q);
        byte[] ggen = Hex.decodeStrict("6767656E");
        byte[] U = new byte[seed.length + ggen.length + 1 + 2];
        System.arraycopy(seed, 0, U, 0, seed.length);
        System.arraycopy(ggen, 0, U, seed.length, ggen.length);
        U[U.length - 3] = (byte)index;
        byte[] w = new byte[d.getDigestSize()];
        for (int count = 1; count < 65536; ++count) {
            DSAParametersGenerator.inc(U);
            DSAParametersGenerator.hash(d, U, w, 0);
            BigInteger W = new BigInteger(1, w);
            BigInteger g = W.modPow(e, p);
            if (g.compareTo(TWO) < 0) continue;
            return g;
        }
        return null;
    }

    private static void hash(Digest d, byte[] input, byte[] output, int outputPos) {
        d.update(input, 0, input.length);
        d.doFinal(output, outputPos);
    }

    private static int getDefaultN(int L) {
        return L > 1024 ? 256 : 160;
    }

    private static int getMinimumIterations(int L) {
        return L <= 1024 ? 40 : 48 + 8 * ((L - 1) / 1024);
    }

    private static void inc(byte[] buf) {
        for (int i = buf.length - 1; i >= 0; --i) {
            byte b;
            buf[i] = b = (byte)(buf[i] + 1 & 0xFF);
            if (b != 0) break;
        }
    }
}

