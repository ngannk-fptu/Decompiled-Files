/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class Ed25519PublicKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 32;
    private final byte[] data = new byte[32];

    public Ed25519PublicKeyParameters(byte[] byArray) {
        this(Ed25519PublicKeyParameters.validate(byArray), 0);
    }

    public Ed25519PublicKeyParameters(byte[] byArray, int n) {
        super(false);
        System.arraycopy(byArray, n, this.data, 0, 32);
    }

    public Ed25519PublicKeyParameters(InputStream inputStream) throws IOException {
        super(false);
        if (32 != Streams.readFully(inputStream, this.data)) {
            throw new EOFException("EOF encountered in middle of Ed25519 public key");
        }
    }

    public void encode(byte[] byArray, int n) {
        System.arraycopy(this.data, 0, byArray, n, 32);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    private static byte[] validate(byte[] byArray) {
        if (byArray.length != 32) {
            throw new IllegalArgumentException("'buf' must have length 32");
        }
        return byArray;
    }
}

