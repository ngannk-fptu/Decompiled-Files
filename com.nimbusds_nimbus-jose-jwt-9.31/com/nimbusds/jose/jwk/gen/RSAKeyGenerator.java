/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.gen;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

public class RSAKeyGenerator
extends JWKGenerator<RSAKey> {
    public static final int MIN_KEY_SIZE_BITS = 2048;
    private final int size;

    public RSAKeyGenerator(int size) {
        this(size, false);
    }

    public RSAKeyGenerator(int size, boolean allowWeakKeys) {
        if (!allowWeakKeys && size < 2048) {
            throw new IllegalArgumentException("The key size must be at least 2048 bits");
        }
        this.size = size;
    }

    @Override
    public RSAKey generate() throws JOSEException {
        KeyPairGenerator generator;
        try {
            generator = this.keyStore != null ? KeyPairGenerator.getInstance("RSA", this.keyStore.getProvider()) : (this.provider != null ? KeyPairGenerator.getInstance("RSA", this.provider) : KeyPairGenerator.getInstance("RSA"));
            if (this.secureRandom != null) {
                generator.initialize(this.size, this.secureRandom);
            } else {
                generator.initialize(this.size);
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        KeyPair kp = generator.generateKeyPair();
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey)kp.getPublic()).privateKey(kp.getPrivate()).keyUse(this.use).keyOperations(this.ops).algorithm(this.alg).expirationTime(this.exp).notBeforeTime(this.nbf).issueTime(this.iat).keyStore(this.keyStore);
        if (this.x5tKid) {
            builder.keyIDFromThumbprint();
        } else {
            builder.keyID(this.kid);
        }
        return builder.build();
    }
}

