/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class DHUParameterSpec
implements AlgorithmParameterSpec {
    private final PublicKey ephemeralPublicKey;
    private final PrivateKey ephemeralPrivateKey;
    private final PublicKey otherPartyEphemeralKey;
    private final byte[] userKeyingMaterial;

    public DHUParameterSpec(PublicKey publicKey, PrivateKey privateKey, PublicKey publicKey2, byte[] byArray) {
        if (privateKey == null) {
            throw new IllegalArgumentException("ephemeral private key cannot be null");
        }
        if (publicKey2 == null) {
            throw new IllegalArgumentException("other party ephemeral key cannot be null");
        }
        this.ephemeralPublicKey = publicKey;
        this.ephemeralPrivateKey = privateKey;
        this.otherPartyEphemeralKey = publicKey2;
        this.userKeyingMaterial = Arrays.clone(byArray);
    }

    public DHUParameterSpec(PublicKey publicKey, PrivateKey privateKey, PublicKey publicKey2) {
        this(publicKey, privateKey, publicKey2, null);
    }

    public DHUParameterSpec(KeyPair keyPair, PublicKey publicKey, byte[] byArray) {
        this(keyPair.getPublic(), keyPair.getPrivate(), publicKey, byArray);
    }

    public DHUParameterSpec(PrivateKey privateKey, PublicKey publicKey, byte[] byArray) {
        this(null, privateKey, publicKey, byArray);
    }

    public DHUParameterSpec(KeyPair keyPair, PublicKey publicKey) {
        this(keyPair.getPublic(), keyPair.getPrivate(), publicKey, null);
    }

    public DHUParameterSpec(PrivateKey privateKey, PublicKey publicKey) {
        this(null, privateKey, publicKey, null);
    }

    public PrivateKey getEphemeralPrivateKey() {
        return this.ephemeralPrivateKey;
    }

    public PublicKey getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
    }

    public PublicKey getOtherPartyEphemeralKey() {
        return this.otherPartyEphemeralKey;
    }

    public byte[] getUserKeyingMaterial() {
        return Arrays.clone(this.userKeyingMaterial);
    }
}

