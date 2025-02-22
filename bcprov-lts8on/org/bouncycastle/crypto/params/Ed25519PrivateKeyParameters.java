/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class Ed25519PrivateKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 32;
    public static final int SIGNATURE_SIZE = 64;
    private final byte[] data = new byte[32];
    private Ed25519PublicKeyParameters cachedPublicKey;

    public Ed25519PrivateKeyParameters(SecureRandom random) {
        super(true);
        Ed25519.generatePrivateKey(random, this.data);
    }

    public Ed25519PrivateKeyParameters(byte[] buf) {
        this(Ed25519PrivateKeyParameters.validate(buf), 0);
    }

    public Ed25519PrivateKeyParameters(byte[] buf, int off) {
        super(true);
        System.arraycopy(buf, off, this.data, 0, 32);
    }

    public Ed25519PrivateKeyParameters(InputStream input) throws IOException {
        super(true);
        if (32 != Streams.readFully(input, this.data)) {
            throw new EOFException("EOF encountered in middle of Ed25519 private key");
        }
    }

    public void encode(byte[] buf, int off) {
        System.arraycopy(this.data, 0, buf, off, 32);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Ed25519PublicKeyParameters generatePublicKey() {
        byte[] byArray = this.data;
        synchronized (this.data) {
            if (null == this.cachedPublicKey) {
                this.cachedPublicKey = new Ed25519PublicKeyParameters(Ed25519.generatePublicKey(this.data, 0));
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.cachedPublicKey;
        }
    }

    public void sign(int algorithm, byte[] ctx, byte[] msg, int msgOff, int msgLen, byte[] sig, int sigOff) {
        Ed25519PublicKeyParameters publicKey = this.generatePublicKey();
        byte[] pk = new byte[32];
        publicKey.encode(pk, 0);
        switch (algorithm) {
            case 0: {
                if (null != ctx) {
                    throw new IllegalArgumentException("ctx");
                }
                Ed25519.sign(this.data, 0, pk, 0, msg, msgOff, msgLen, sig, sigOff);
                break;
            }
            case 1: {
                if (null == ctx) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (ctx.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                Ed25519.sign(this.data, 0, pk, 0, ctx, msg, msgOff, msgLen, sig, sigOff);
                break;
            }
            case 2: {
                if (null == ctx) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (ctx.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                if (64 != msgLen) {
                    throw new IllegalArgumentException("msgLen");
                }
                Ed25519.signPrehash(this.data, 0, pk, 0, ctx, msg, msgOff, sig, sigOff);
                break;
            }
            default: {
                throw new IllegalArgumentException("algorithm");
            }
        }
    }

    private static byte[] validate(byte[] buf) {
        if (buf.length != 32) {
            throw new IllegalArgumentException("'buf' must have length 32");
        }
        return buf;
    }
}

