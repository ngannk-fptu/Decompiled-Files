/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Properties;

public class RSAKeyParameters
extends AsymmetricKeyParameter {
    private static final BigIntegers.Cache validated = new BigIntegers.Cache();
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private BigInteger modulus;
    private BigInteger exponent;

    public RSAKeyParameters(boolean isPrivate, BigInteger modulus, BigInteger exponent) {
        this(isPrivate, modulus, exponent, false);
    }

    public RSAKeyParameters(boolean isPrivate, BigInteger modulus, BigInteger exponent, boolean isInternal) {
        super(isPrivate);
        if (!isPrivate && (exponent.intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA publicExponent is even");
        }
        this.modulus = validated.contains(modulus) ? modulus : this.validate(modulus, isInternal);
        this.exponent = exponent;
    }

    private BigInteger validate(BigInteger modulus, boolean isInternal) {
        Primes.MROutput mr;
        int modBitLength;
        if (isInternal) {
            validated.add(modulus);
            return modulus;
        }
        if ((modulus.intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA modulus is even");
        }
        if (Properties.isOverrideSet("org.bouncycastle.rsa.allow_unsafe_mod")) {
            return modulus;
        }
        int maxBitLength = Properties.asInteger("org.bouncycastle.rsa.max_size", 15360);
        if (maxBitLength < (modBitLength = modulus.bitLength())) {
            throw new IllegalArgumentException("modulus value out of range");
        }
        if (!modulus.gcd(SMALL_PRIMES_PRODUCT).equals(ONE)) {
            throw new IllegalArgumentException("RSA modulus has a small prime factor");
        }
        int bits = modulus.bitLength() / 2;
        int iterations = Properties.asInteger("org.bouncycastle.rsa.max_mr_tests", RSAKeyParameters.getMRIterations(bits));
        if (iterations > 0 && !(mr = Primes.enhancedMRProbablePrimeTest(modulus, CryptoServicesRegistrar.getSecureRandom(), iterations)).isProvablyComposite()) {
            throw new IllegalArgumentException("RSA modulus is not composite");
        }
        validated.add(modulus);
        return modulus;
    }

    private static int getMRIterations(int bits) {
        int iterations = bits >= 1536 ? 3 : (bits >= 1024 ? 4 : (bits >= 512 ? 7 : 50));
        return iterations;
    }

    public BigInteger getModulus() {
        return this.modulus;
    }

    public BigInteger getExponent() {
        return this.exponent;
    }
}

