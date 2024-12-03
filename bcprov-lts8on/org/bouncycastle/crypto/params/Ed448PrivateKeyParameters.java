/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class Ed448PrivateKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 57;
    public static final int SIGNATURE_SIZE = 114;
    private final byte[] data = new byte[57];
    private Ed448PublicKeyParameters cachedPublicKey;

    public Ed448PrivateKeyParameters(SecureRandom random) {
        super(true);
        Ed448.generatePrivateKey(random, this.data);
    }

    public Ed448PrivateKeyParameters(byte[] buf) {
        this(Ed448PrivateKeyParameters.validate(buf), 0);
    }

    public Ed448PrivateKeyParameters(byte[] buf, int off) {
        super(true);
        System.arraycopy(buf, off, this.data, 0, 57);
    }

    public Ed448PrivateKeyParameters(InputStream input) throws IOException {
        super(true);
        if (57 != Streams.readFully(input, this.data)) {
            throw new EOFException("EOF encountered in middle of Ed448 private key");
        }
    }

    public void encode(byte[] buf, int off) {
        System.arraycopy(this.data, 0, buf, off, 57);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Ed448PublicKeyParameters generatePublicKey() {
        byte[] byArray = this.data;
        synchronized (this.data) {
            if (null == this.cachedPublicKey) {
                this.cachedPublicKey = new Ed448PublicKeyParameters(Ed448.generatePublicKey(this.data, 0));
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.cachedPublicKey;
        }
    }

    public void sign(int algorithm, byte[] ctx, byte[] msg, int msgOff, int msgLen, byte[] sig, int sigOff) {
        Ed448PublicKeyParameters publicKey = this.generatePublicKey();
        byte[] pk = new byte[57];
        publicKey.encode(pk, 0);
        switch (algorithm) {
            case 0: {
                if (null == ctx) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (ctx.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                Ed448.sign(this.data, 0, pk, 0, ctx, msg, msgOff, msgLen, sig, sigOff);
                break;
            }
            case 1: {
                if (null == ctx) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (ctx.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                if (64 != msgLen) {
                    throw new IllegalArgumentException("msgLen");
                }
                Ed448.signPrehash(this.data, 0, pk, 0, ctx, msg, msgOff, sig, sigOff);
                break;
            }
            default: {
                throw new IllegalArgumentException("algorithm");
            }
        }
    }

    private static byte[] validate(byte[] buf) {
        if (buf.length != 57) {
            throw new IllegalArgumentException("'buf' must have length 57");
        }
        return buf;
    }
}

