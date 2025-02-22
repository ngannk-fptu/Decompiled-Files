/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class GenericSigner
implements Signer {
    private final AsymmetricBlockCipher engine;
    private final Digest digest;
    private boolean forSigning;

    public GenericSigner(AsymmetricBlockCipher engine, Digest digest) {
        this.engine = engine;
        this.digest = digest;
    }

    @Override
    public void init(boolean forSigning, CipherParameters parameters) {
        this.forSigning = forSigning;
        AsymmetricKeyParameter k = parameters instanceof ParametersWithRandom ? (AsymmetricKeyParameter)((ParametersWithRandom)parameters).getParameters() : (AsymmetricKeyParameter)parameters;
        if (forSigning && !k.isPrivate()) {
            throw new IllegalArgumentException("signing requires private key");
        }
        if (!forSigning && k.isPrivate()) {
            throw new IllegalArgumentException("verification requires public key");
        }
        this.reset();
        this.engine.init(forSigning, parameters);
    }

    @Override
    public void update(byte input) {
        this.digest.update(input);
    }

    @Override
    public void update(byte[] input, int inOff, int length) {
        this.digest.update(input, inOff, length);
    }

    @Override
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        if (!this.forSigning) {
            throw new IllegalStateException("GenericSigner not initialised for signature generation.");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        return this.engine.processBlock(hash, 0, hash.length);
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning) {
            throw new IllegalStateException("GenericSigner not initialised for verification");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] sig = this.engine.processBlock(signature, 0, signature.length);
            if (sig.length < hash.length) {
                byte[] tmp = new byte[hash.length];
                System.arraycopy(sig, 0, tmp, tmp.length - sig.length, sig.length);
                sig = tmp;
            }
            return Arrays.constantTimeAreEqual(sig, hash);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void reset() {
        this.digest.reset();
    }
}

