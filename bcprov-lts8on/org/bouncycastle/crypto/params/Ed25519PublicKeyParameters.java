/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.io.Streams;

public final class Ed25519PublicKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 32;
    private final Ed25519.PublicPoint publicPoint;

    public Ed25519PublicKeyParameters(byte[] buf) {
        this(Ed25519PublicKeyParameters.validate(buf), 0);
    }

    public Ed25519PublicKeyParameters(byte[] buf, int off) {
        super(false);
        this.publicPoint = Ed25519PublicKeyParameters.parse(buf, off);
    }

    public Ed25519PublicKeyParameters(InputStream input) throws IOException {
        super(false);
        byte[] data = new byte[32];
        if (32 != Streams.readFully(input, data)) {
            throw new EOFException("EOF encountered in middle of Ed25519 public key");
        }
        this.publicPoint = Ed25519PublicKeyParameters.parse(data, 0);
    }

    public Ed25519PublicKeyParameters(Ed25519.PublicPoint publicPoint) {
        super(false);
        if (publicPoint == null) {
            throw new NullPointerException("'publicPoint' cannot be null");
        }
        this.publicPoint = publicPoint;
    }

    public void encode(byte[] buf, int off) {
        Ed25519.encodePublicPoint(this.publicPoint, buf, off);
    }

    public byte[] getEncoded() {
        byte[] data = new byte[32];
        this.encode(data, 0);
        return data;
    }

    public boolean verify(int algorithm, byte[] ctx, byte[] msg, int msgOff, int msgLen, byte[] sig, int sigOff) {
        switch (algorithm) {
            case 0: {
                if (null != ctx) {
                    throw new IllegalArgumentException("ctx");
                }
                return Ed25519.verify(sig, sigOff, this.publicPoint, msg, msgOff, msgLen);
            }
            case 1: {
                if (null == ctx) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (ctx.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                return Ed25519.verify(sig, sigOff, this.publicPoint, ctx, msg, msgOff, msgLen);
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
                return Ed25519.verifyPrehash(sig, sigOff, this.publicPoint, ctx, msg, msgOff);
            }
        }
        throw new IllegalArgumentException("algorithm");
    }

    private static Ed25519.PublicPoint parse(byte[] buf, int off) {
        Ed25519.PublicPoint publicPoint = Ed25519.validatePublicKeyPartialExport(buf, off);
        if (publicPoint == null) {
            throw new IllegalArgumentException("invalid public key");
        }
        return publicPoint;
    }

    private static byte[] validate(byte[] buf) {
        if (buf.length != 32) {
            throw new IllegalArgumentException("'buf' must have length 32");
        }
        return buf;
    }
}

