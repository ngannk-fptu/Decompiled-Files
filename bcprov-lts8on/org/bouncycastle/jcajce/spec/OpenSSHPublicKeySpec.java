/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class OpenSSHPublicKeySpec
extends EncodedKeySpec {
    private static final String[] allowedTypes = new String[]{"ssh-rsa", "ssh-ed25519", "ssh-dss"};
    private final String type;

    public OpenSSHPublicKeySpec(byte[] encodedKey) {
        super(encodedKey);
        int pos = 0;
        int i = (encodedKey[pos++] & 0xFF) << 24;
        i |= (encodedKey[pos++] & 0xFF) << 16;
        i |= (encodedKey[pos++] & 0xFF) << 8;
        if (pos + (i |= encodedKey[pos++] & 0xFF) >= encodedKey.length) {
            throw new IllegalArgumentException("invalid public key blob: type field longer than blob");
        }
        this.type = Strings.fromByteArray(Arrays.copyOfRange(encodedKey, pos, pos + i));
        if (this.type.startsWith("ecdsa")) {
            return;
        }
        for (int t = 0; t < allowedTypes.length; ++t) {
            if (!allowedTypes[t].equals(this.type)) continue;
            return;
        }
        throw new IllegalArgumentException("unrecognised public key type " + this.type);
    }

    @Override
    public String getFormat() {
        return "OpenSSH";
    }

    public String getType() {
        return this.type;
    }
}

