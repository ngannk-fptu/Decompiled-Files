/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.RSACoreEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindingEngine
implements AsymmetricBlockCipher {
    private RSACoreEngine core = new RSACoreEngine();
    private RSAKeyParameters key;
    private BigInteger blindingFactor;
    private boolean forEncryption;

    @Override
    public void init(boolean forEncryption, CipherParameters param) {
        RSABlindingParameters p;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            p = (RSABlindingParameters)rParam.getParameters();
        } else {
            p = (RSABlindingParameters)param;
        }
        this.core.init(forEncryption, p.getPublicKey());
        this.forEncryption = forEncryption;
        this.key = p.getPublicKey();
        this.blindingFactor = p.getBlindingFactor();
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
        BigInteger msg = this.core.convertInput(in, inOff, inLen);
        msg = this.forEncryption ? this.blindMessage(msg) : this.unblindMessage(msg);
        return this.core.convertOutput(msg);
    }

    private BigInteger blindMessage(BigInteger msg) {
        BigInteger blindMsg = this.blindingFactor;
        blindMsg = msg.multiply(blindMsg.modPow(this.key.getExponent(), this.key.getModulus()));
        blindMsg = blindMsg.mod(this.key.getModulus());
        return blindMsg;
    }

    private BigInteger unblindMessage(BigInteger blindedMsg) {
        BigInteger m = this.key.getModulus();
        BigInteger msg = blindedMsg;
        BigInteger blindFactorInverse = BigIntegers.modOddInverse(m, this.blindingFactor);
        msg = msg.multiply(blindFactorInverse);
        msg = msg.mod(m);
        return msg;
    }
}

