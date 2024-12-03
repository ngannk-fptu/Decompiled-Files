/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.cryptography;

import aQute.libg.tuple.Pair;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSA {
    static final String ALGORITHM = "RSA";
    static final KeyFactory factory = RSA.getKeyFactory();

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance(ALGORITHM);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static RSAPrivateKey create(RSAPrivateKeySpec keyspec) throws InvalidKeySpecException {
        return (RSAPrivateKey)factory.generatePrivate(keyspec);
    }

    public static RSAPublicKey create(RSAPublicKeySpec keyspec) throws InvalidKeySpecException {
        return (RSAPublicKey)((Object)factory.generatePrivate(keyspec));
    }

    public static RSAPublicKey createPublic(BigInteger m, BigInteger e) throws InvalidKeySpecException {
        return RSA.create(new RSAPublicKeySpec(m, e));
    }

    public static RSAPrivateKey createPrivate(BigInteger m, BigInteger e) throws InvalidKeySpecException {
        return RSA.create(new RSAPrivateKeySpec(m, e));
    }

    public static Pair<RSAPrivateKey, RSAPublicKey> generate() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
        KeyPair keypair = kpg.generateKeyPair();
        return new Pair<RSAPrivateKey, RSAPublicKey>((RSAPrivateKey)keypair.getPrivate(), (RSAPublicKey)keypair.getPublic());
    }
}

