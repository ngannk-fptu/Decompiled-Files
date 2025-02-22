/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.gen;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import com.nimbusds.jose.util.Base64URL;
import java.security.SecureRandom;

public class OctetSequenceKeyGenerator
extends JWKGenerator<OctetSequenceKey> {
    public static final int MIN_KEY_SIZE_BITS = 112;
    private final int size;

    public OctetSequenceKeyGenerator(int size) {
        if (size < 112) {
            throw new IllegalArgumentException("The key size must be at least 112 bits");
        }
        if (size % 8 != 0) {
            throw new IllegalArgumentException("The key size in bits must be divisible by 8");
        }
        this.size = size;
    }

    @Override
    public OctetSequenceKey generate() throws JOSEException {
        byte[] keyMaterial = new byte[this.size / 8];
        if (this.secureRandom != null) {
            this.secureRandom.nextBytes(keyMaterial);
        } else {
            new SecureRandom().nextBytes(keyMaterial);
        }
        OctetSequenceKey.Builder builder = new OctetSequenceKey.Builder(Base64URL.encode(keyMaterial)).keyUse(this.use).keyOperations(this.ops).algorithm(this.alg).keyStore(this.keyStore);
        if (this.x5tKid) {
            builder.keyIDFromThumbprint();
        } else {
            builder.keyID(this.kid);
        }
        return builder.build();
    }
}

