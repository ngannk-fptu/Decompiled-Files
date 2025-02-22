/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;

public class ElGamalUtil {
    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof ElGamalPublicKey) {
            ElGamalPublicKey k = (ElGamalPublicKey)key;
            return new ElGamalPublicKeyParameters(k.getY(), new ElGamalParameters(k.getParameters().getP(), k.getParameters().getG()));
        }
        if (key instanceof DHPublicKey) {
            DHPublicKey k = (DHPublicKey)key;
            return new ElGamalPublicKeyParameters(k.getY(), new ElGamalParameters(k.getParams().getP(), k.getParams().getG()));
        }
        throw new InvalidKeyException("can't identify public key for El Gamal.");
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof ElGamalPrivateKey) {
            ElGamalPrivateKey k = (ElGamalPrivateKey)key;
            return new ElGamalPrivateKeyParameters(k.getX(), new ElGamalParameters(k.getParameters().getP(), k.getParameters().getG()));
        }
        if (key instanceof DHPrivateKey) {
            DHPrivateKey k = (DHPrivateKey)key;
            return new ElGamalPrivateKeyParameters(k.getX(), new ElGamalParameters(k.getParams().getP(), k.getParams().getG()));
        }
        throw new InvalidKeyException("can't identify private key for El Gamal.");
    }
}

