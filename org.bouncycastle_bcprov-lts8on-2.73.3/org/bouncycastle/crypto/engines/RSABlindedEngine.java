/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.RSACoreEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindedEngine
implements AsymmetricBlockCipher {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private RSACoreEngine core = new RSACoreEngine();
    private RSAKeyParameters key;
    private SecureRandom random;

    @Override
    public void init(boolean forEncryption, CipherParameters param) {
        this.core.init(forEncryption, param);
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            this.key = (RSAKeyParameters)rParam.getParameters();
            this.random = this.key instanceof RSAPrivateCrtKeyParameters ? rParam.getRandom() : null;
        } else {
            this.key = (RSAKeyParameters)param;
            this.random = this.key instanceof RSAPrivateCrtKeyParameters ? CryptoServicesRegistrar.getSecureRandom() : null;
        }
    }

    @Override
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }

    @Override
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }

    @Override
    public byte[] processBlock(byte[] in, int inOff, int inLen) {
        BigInteger result;
        if (this.key == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        BigInteger input = this.core.convertInput(in, inOff, inLen);
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            RSAPrivateCrtKeyParameters k = (RSAPrivateCrtKeyParameters)this.key;
            BigInteger e = k.getPublicExponent();
            if (e != null) {
                BigInteger rInv;
                BigInteger m = k.getModulus();
                BigInteger r = BigIntegers.createRandomInRange(ONE, m.subtract(ONE), this.random);
                BigInteger blindedInput = r.modPow(e, m).multiply(input).mod(m);
                BigInteger blindedResult = this.core.processBlock(blindedInput);
                result = blindedResult.multiply(rInv = BigIntegers.modOddInverse(m, r)).mod(m);
                if (!input.equals(result.modPow(e, m))) {
                    throw new IllegalStateException("RSA engine faulty decryption/signing detected");
                }
            } else {
                result = this.core.processBlock(input);
            }
        } else {
            result = this.core.processBlock(input);
        }
        return this.core.convertOutput(result);
    }
}

