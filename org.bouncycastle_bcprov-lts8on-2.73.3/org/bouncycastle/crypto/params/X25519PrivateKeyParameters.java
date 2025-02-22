/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class X25519PrivateKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 32;
    public static final int SECRET_SIZE = 32;
    private final byte[] data = new byte[32];

    public X25519PrivateKeyParameters(SecureRandom random) {
        super(true);
        X25519.generatePrivateKey(random, this.data);
    }

    public X25519PrivateKeyParameters(byte[] buf) {
        this(X25519PrivateKeyParameters.validate(buf), 0);
    }

    public X25519PrivateKeyParameters(byte[] buf, int off) {
        super(true);
        System.arraycopy(buf, off, this.data, 0, 32);
    }

    public X25519PrivateKeyParameters(InputStream input) throws IOException {
        super(true);
        if (32 != Streams.readFully(input, this.data)) {
            throw new EOFException("EOF encountered in middle of X25519 private key");
        }
    }

    public void encode(byte[] buf, int off) {
        System.arraycopy(this.data, 0, buf, off, 32);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    public X25519PublicKeyParameters generatePublicKey() {
        byte[] publicKey = new byte[32];
        X25519.generatePublicKey(this.data, 0, publicKey, 0);
        return new X25519PublicKeyParameters(publicKey, 0);
    }

    public void generateSecret(X25519PublicKeyParameters publicKey, byte[] buf, int off) {
        byte[] encoded = new byte[32];
        publicKey.encode(encoded, 0);
        if (!X25519.calculateAgreement(this.data, 0, encoded, 0, buf, off)) {
            throw new IllegalStateException("X25519 agreement failed");
        }
    }

    private static byte[] validate(byte[] buf) {
        if (buf.length != 32) {
            throw new IllegalArgumentException("'buf' must have length 32");
        }
        return buf;
    }
}

