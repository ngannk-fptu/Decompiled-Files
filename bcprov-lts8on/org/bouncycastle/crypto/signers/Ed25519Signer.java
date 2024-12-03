/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.util.Arrays;

public class Ed25519Signer
implements Signer {
    private final Buffer buffer = new Buffer();
    private boolean forSigning;
    private Ed25519PrivateKeyParameters privateKey;
    private Ed25519PublicKeyParameters publicKey;

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
        this.buffer.write(b);
    }

    @Override
    public void update(byte[] buf, int off, int len) {
        this.buffer.write(buf, off, len);
    }

    @Override
    public byte[] generateSignature() {
        if (!this.forSigning || null == this.privateKey) {
            throw new IllegalStateException("Ed25519Signer not initialised for signature generation.");
        }
        return this.buffer.generateSignature(this.privateKey);
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning || null == this.publicKey) {
            throw new IllegalStateException("Ed25519Signer not initialised for verification");
        }
        return this.buffer.verifySignature(this.publicKey, signature);
    }

    @Override
    public void reset() {
        this.buffer.reset();
    }

    private static final class Buffer
    extends ByteArrayOutputStream {
        private Buffer() {
        }

        synchronized byte[] generateSignature(Ed25519PrivateKeyParameters privateKey) {
            byte[] signature = new byte[64];
            privateKey.sign(0, null, this.buf, 0, this.count, signature, 0);
            this.reset();
            return signature;
        }

        synchronized boolean verifySignature(Ed25519PublicKeyParameters publicKey, byte[] signature) {
            if (64 != signature.length) {
                this.reset();
                return false;
            }
            boolean result = publicKey.verify(0, null, this.buf, 0, this.count, signature, 0);
            this.reset();
            return result;
        }

        @Override
        public synchronized void reset() {
            Arrays.fill(this.buf, 0, this.count, (byte)0);
            this.count = 0;
        }
    }
}

