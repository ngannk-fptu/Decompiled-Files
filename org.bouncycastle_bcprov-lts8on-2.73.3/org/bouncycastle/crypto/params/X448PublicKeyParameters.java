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

public final class X448PublicKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 56;
    private final byte[] data = new byte[56];

    public X448PublicKeyParameters(byte[] buf) {
        this(X448PublicKeyParameters.validate(buf), 0);
    }

    public X448PublicKeyParameters(byte[] buf, int off) {
        super(false);
        System.arraycopy(buf, off, this.data, 0, 56);
    }

    public X448PublicKeyParameters(InputStream input) throws IOException {
        super(false);
        if (56 != Streams.readFully(input, this.data)) {
            throw new EOFException("EOF encountered in middle of X448 public key");
        }
    }

    public void encode(byte[] buf, int off) {
        System.arraycopy(this.data, 0, buf, off, 56);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.data);
    }

    private static byte[] validate(byte[] buf) {
        if (buf.length != 56) {
            throw new IllegalArgumentException("'buf' must have length 56");
        }
        return buf;
    }
}

