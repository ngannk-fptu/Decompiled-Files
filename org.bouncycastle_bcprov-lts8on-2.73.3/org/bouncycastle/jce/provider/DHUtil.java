/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class DHUtil {
    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof DHPublicKey) {
            DHPublicKey k = (DHPublicKey)key;
            return new DHPublicKeyParameters(k.getY(), new DHParameters(k.getParams().getP(), k.getParams().getG(), null, k.getParams().getL()));
        }
        throw new InvalidKeyException("can't identify DH public key.");
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof DHPrivateKey) {
            DHPrivateKey k = (DHPrivateKey)key;
            return new DHPrivateKeyParameters(k.getX(), new DHParameters(k.getParams().getP(), k.getParams().getG(), null, k.getParams().getL()));
        }
        throw new InvalidKeyException("can't identify DH private key.");
    }
}

