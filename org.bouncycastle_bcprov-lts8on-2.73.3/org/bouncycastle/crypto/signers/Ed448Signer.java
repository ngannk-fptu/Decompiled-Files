/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.signers.Utils;
import org.bouncycastle.util.Arrays;

public class Ed448Signer
implements Signer {
    private final Buffer buffer = new Buffer();
    private final byte[] context;
    private boolean forSigning;
    private Ed448PrivateKeyParameters privateKey;
    private Ed448PublicKeyParameters publicKey;

    public Ed448Signer(byte[] context) {
        if (null == context) {
            throw new NullPointerException("'context' cannot be null");
        }
        this.context = Arrays.clone(context);
    }

    @Override
    public void init(boolean forSigning, CipherParameters parameters) {
        this.forSigning = forSigning;
        if (forSigning) {
            this.privateKey = (Ed448PrivateKeyParameters)parameters;
            this.publicKey = null;
        } else {
            this.privateKey = null;
            this.publicKey = (Ed448PublicKeyParameters)parameters;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("Ed448", 224, parameters, forSigning));
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
            throw new IllegalStateException("Ed448Signer not initialised for signature generation.");
        }
        return this.buffer.generateSignature(this.privateKey, this.context);
    }

    @Override
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning || null == this.publicKey) {
            throw new IllegalStateException("Ed448Signer not initialised for verification");
        }
        return this.buffer.verifySignature(this.publicKey, this.context, signature);
    }

    @Override
    public void reset() {
        this.buffer.reset();
    }

    private static final class Buffer
    extends ByteArrayOutputStream {
        private Buffer() {
        }

        synchronized byte[] generateSignature(Ed448PrivateKeyParameters privateKey, byte[] ctx) {
            byte[] signature = new byte[114];
            privateKey.sign(0, ctx, this.buf, 0, this.count, signature, 0);
            this.reset();
            return signature;
        }

        synchronized boolean verifySignature(Ed448PublicKeyParameters publicKey, byte[] ctx, byte[] signature) {
            if (114 != signature.length) {
                this.reset();
                return false;
            }
            boolean result = publicKey.verify(0, ctx, this.buf, 0, this.count, signature, 0);
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

