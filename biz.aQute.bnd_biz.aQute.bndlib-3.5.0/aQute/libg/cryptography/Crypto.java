/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.cryptography;

import aQute.libg.cryptography.Digest;
import aQute.libg.cryptography.RSA;
import aQute.libg.cryptography.Verifier;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crypto {
    static final Pattern RSA_PRIVATE = Pattern.compile("\\s*RSA\\.Private\\((\\p{XDigit})+:(\\p{XDigit})+\\)\\s*");
    static final Pattern RSA_PUBLIC = Pattern.compile("\\s*RSA\\.Public\\((\\p{XDigit})+:(\\p{XDigit})+\\)\\s*");

    public static <T> T fromString(String spec, Class<T> c) throws Exception {
        if (PrivateKey.class.isAssignableFrom(c)) {
            Matcher m = RSA_PRIVATE.matcher(spec);
            if (m.matches()) {
                return (T)RSA.createPrivate(new BigInteger(m.group(1)), new BigInteger(m.group(2)));
            }
            throw new IllegalArgumentException("No such private key " + spec);
        }
        if (PublicKey.class.isAssignableFrom(c)) {
            Matcher m = RSA_PUBLIC.matcher(spec);
            if (m.matches()) {
                return (T)RSA.create(new RSAPublicKeySpec(new BigInteger(m.group(1)), new BigInteger(m.group(2))));
            }
            throw new IllegalArgumentException("No such public key " + spec);
        }
        return null;
    }

    public static String toString(Object key) {
        if (key instanceof RSAPrivateKey) {
            RSAPrivateKey pk = (RSAPrivateKey)key;
            return "RSA.Private(" + pk.getModulus() + ":" + pk.getPrivateExponent() + ")";
        }
        if (key instanceof RSAPublicKey) {
            RSAPublicKey pk = (RSAPublicKey)key;
            return "RSA.Private(" + pk.getModulus() + ":" + pk.getPublicExponent() + ")";
        }
        return null;
    }

    public static Verifier verifier(PublicKey key, Digest digest) throws NoSuchAlgorithmException {
        Signature s = Signature.getInstance(key.getAlgorithm() + "with" + digest.getAlgorithm());
        return new Verifier(s, digest);
    }
}

