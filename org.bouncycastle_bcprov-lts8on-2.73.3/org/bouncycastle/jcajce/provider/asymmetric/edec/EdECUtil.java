/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;

class EdECUtil {
    EdECUtil() {
    }

    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof BCXDHPublicKey) {
            return ((BCXDHPublicKey)key).engineGetKeyParameters();
        }
        if (key instanceof BCEdDSAPublicKey) {
            return ((BCEdDSAPublicKey)key).engineGetKeyParameters();
        }
        try {
            byte[] bytes = key.getEncoded();
            if (bytes == null) {
                throw new InvalidKeyException("no encoding for EdEC/XDH public key");
            }
            return PublicKeyFactory.createKey(bytes);
        }
        catch (Exception e) {
            throw new InvalidKeyException("cannot identify EdEC/XDH public key: " + e.getMessage());
        }
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof BCXDHPrivateKey) {
            return ((BCXDHPrivateKey)key).engineGetKeyParameters();
        }
        if (key instanceof BCEdDSAPrivateKey) {
            return ((BCEdDSAPrivateKey)key).engineGetKeyParameters();
        }
        try {
            byte[] bytes = key.getEncoded();
            if (bytes == null) {
                throw new InvalidKeyException("no encoding for EdEC/XDH private key");
            }
            return PrivateKeyFactory.createKey(bytes);
        }
        catch (Exception e) {
            throw new InvalidKeyException("cannot identify EdEC/XDH private key: " + e.getMessage());
        }
    }
}

