/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.security.interfaces.EdECPrivateKey;
import java.security.spec.NamedParameterSpec;
import java.util.Optional;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BC15EdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;

class BC15EdDSAPrivateKey
extends BCEdDSAPrivateKey
implements EdECPrivateKey {
    BC15EdDSAPrivateKey(AsymmetricKeyParameter privKey) {
        super(privKey);
    }

    BC15EdDSAPrivateKey(PrivateKeyInfo keyInfo) throws IOException {
        super(keyInfo);
    }

    @Override
    public Optional<byte[]> getBytes() {
        if (this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters) {
            return Optional.of(((Ed448PrivateKeyParameters)this.eddsaPrivateKey).getEncoded());
        }
        return Optional.of(((Ed25519PrivateKeyParameters)this.eddsaPrivateKey).getEncoded());
    }

    @Override
    public NamedParameterSpec getParams() {
        if (this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters) {
            return NamedParameterSpec.ED448;
        }
        return NamedParameterSpec.ED25519;
    }

    @Override
    public EdDSAPublicKey getPublicKey() {
        if (this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters) {
            return new BC15EdDSAPublicKey(((Ed448PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey());
        }
        return new BC15EdDSAPublicKey(((Ed25519PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey());
    }
}

