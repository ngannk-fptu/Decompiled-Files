/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.RSACoreEngine;

public class RSAEngine
implements AsymmetricBlockCipher {
    private RSACoreEngine core;

    @Override
    public void init(boolean forEncryption, CipherParameters param) {
        if (this.core == null) {
            this.core = new RSACoreEngine();
        }
        this.core.init(forEncryption, param);
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
        if (this.core == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        return this.core.convertOutput(this.core.processBlock(this.core.convertInput(in, inOff, inLen)));
    }
}

