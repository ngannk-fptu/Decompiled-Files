/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;

public class DSADigestSigner
implements Signer {
    private final DSA dsa;
    private final Digest digest;
    private final DSAEncoding encoding;
    private boolean forSigning;

    public DSADigestSigner(DSA dsa, Digest digest) {
        this.dsa = dsa;
        this.digest = digest;
        this.encoding = StandardDSAEncoding.INSTANCE;
    }

    public DSADigestSigner(DSAExt dsa, Digest digest, DSAEncoding encoding) {
        this.dsa = dsa;
        this.digest = digest;
        this.encoding = encoding;
    }

    @Override
    public void init(boolean forSigning, CipherParameters parameters) {
        this.forSigning = forSigning;
        AsymmetricKeyParameter k = parameters instanceof ParametersWithRandom ? (AsymmetricKeyParameter)((ParametersWithRandom)parameters).getParameters() : (AsymmetricKeyParameter)parameters;
        if (forSigning && !k.isPrivate()) {
            throw new IllegalArgumentException("Signing Requires Private Key.");
        }
        if (!forSigning && k.isPrivate()) {
            throw new IllegalArgumentException("Verification Requires Public Key.");
        }
        this.reset();
        this.dsa.init(forSigning, parameters);
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
    public byte[] generateSignature() {
        if (!this.forSigning) {
            throw new IllegalStateException("DSADigestSigner not initialised for signature generation.");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        BigInteger[] sig = this.dsa.generateSignature(hash);
        try {
            return this.encoding.encode(this.getOrder(), sig[0], sig[1]);
        }
        catch (Exception e) {
            throw new IllegalStateException("unable to encode signature");
        }
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning) {
            throw new IllegalStateException("DSADigestSigner not initialised for verification");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            BigInteger[] sig = this.encoding.decode(this.getOrder(), signature);
            return this.dsa.verifySignature(hash, sig[0], sig[1]);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void reset() {
        this.digest.reset();
    }

    protected BigInteger getOrder() {
        return this.dsa instanceof DSAExt ? ((DSAExt)this.dsa).getOrder() : null;
    }
}

