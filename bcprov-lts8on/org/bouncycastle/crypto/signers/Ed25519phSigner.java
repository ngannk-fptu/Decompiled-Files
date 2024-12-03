/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;

public class Ed25519phSigner
implements Signer {
    private final Digest prehash = Ed25519.createPrehash();
    private final byte[] context;
    private boolean forSigning;
    private Ed25519PrivateKeyParameters privateKey;
    private Ed25519PublicKeyParameters publicKey;

    public Ed25519phSigner(byte[] context) {
        if (null == context) {
            throw new NullPointerException("'context' cannot be null");
        }
        this.context = Arrays.clone(context);
    }

    @Override
    public void init(boolean forSigning, CipherParameters parameters) {
        this.forSigning = forSigning;
        if (forSigning) {
            this.privateKey = (Ed25519PrivateKeyParameters)parameters;
            this.publicKey = null;
        } else {
            this.privateKey = null;
            this.publicKey = (Ed25519PublicKeyParameters)parameters;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("Ed25519", 128, parameters, forSigning));
        this.reset();
    }

    @Override
    public void update(byte b) {
        this.prehash.update(b);
    }

    @Override
    public void update(byte[] buf, int off, int len) {
        this.prehash.update(buf, off, len);
    }

    @Override
    public byte[] generateSignature() {
        if (!this.forSigning || null == this.privateKey) {
            throw new IllegalStateException("Ed25519phSigner not initialised for signature generation.");
        }
        byte[] msg = new byte[64];
        if (64 != this.prehash.doFinal(msg, 0)) {
            throw new IllegalStateException("Prehash digest failed");
        }
        byte[] signature = new byte[64];
        this.privateKey.sign(2, this.context, msg, 0, 64, signature, 0);
        return signature;
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning || null == this.publicKey) {
            throw new IllegalStateException("Ed25519phSigner not initialised for verification");
        }
        if (64 != signature.length) {
            this.prehash.reset();
            return false;
        }
        byte[] msg = new byte[64];
        if (64 != this.prehash.doFinal(msg, 0)) {
            throw new IllegalStateException("Prehash digest failed");
        }
        return this.publicKey.verify(2, this.context, msg, 0, 64, signature, 0);
    }

    @Override
    public void reset() {
        this.prehash.reset();
    }
}

