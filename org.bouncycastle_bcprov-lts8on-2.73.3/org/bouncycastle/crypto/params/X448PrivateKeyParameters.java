/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.math.ec.rfc7748.X448;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class X448PrivateKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 56;
    public static final int SECRET_SIZE = 56;
    private final byte[] data = new byte[56];

    public X448PrivateKeyParameters(SecureRandom random) {
        super(true);
        X448.generatePrivateKey(random, this.data);
    }

    public X448PrivateKeyParameters(byte[] buf) {
        this(X448PrivateKeyParameters.validate(buf), 0);
    }

    public X448PrivateKeyParameters(byte[] buf, int off) {
        super(true);
        System.arraycopy(buf, off, this.data, 0, 56);
    }

    public X448PrivateKeyParameters(InputStream input) throws IOException {
        super(true);
        if (56 != Streams.readFully(input, this.data)) {
            throw new EOFException("EOF encountered in middle of X448 private key");
        }
    }

    public void encode(byte[] buf, int off) {
        System.arraycopy(this.data, 0, buf, off, 56);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    public X448PublicKeyParameters generatePublicKey() {
        byte[] publicKey = new byte[56];
        X448.generatePublicKey(this.data, 0, publicKey, 0);
        return new X448PublicKeyParameters(publicKey, 0);
    }

    public void generateSecret(X448PublicKeyParameters publicKey, byte[] buf, int off) {
        byte[] encoded = new byte[56];
        publicKey.encode(encoded, 0);
        if (!X448.calculateAgreement(this.data, 0, encoded, 0, buf, off)) {
            throw new IllegalStateException("X448 agreement failed");
        }
    }

    private static byte[] validate(byte[] buf) {
        if (buf.length != 56) {
            throw new IllegalArgumentException("'buf' must have length 56");
        }
        return buf;
    }
}

