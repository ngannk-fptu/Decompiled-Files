/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.jose;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.oauth2.sdk.auth.Secret;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecretKeyDerivation {
    public static SecretKey deriveSecretKey(Secret clientSecret, JWEAlgorithm alg, EncryptionMethod enc) throws JOSEException {
        if (JWEAlgorithm.DIR.equals(alg)) {
            int cekBitLength = enc.cekBitLength();
            if (cekBitLength == 0) {
                throw new JOSEException("Unsupported JWE method: enc=" + enc);
            }
            return SecretKeyDerivation.deriveSecretKey(clientSecret, enc.cekBitLength());
        }
        if (JWEAlgorithm.Family.AES_KW.contains(alg)) {
            if (JWEAlgorithm.A128KW.equals(alg)) {
                return SecretKeyDerivation.deriveSecretKey(clientSecret, 128);
            }
            if (JWEAlgorithm.A192KW.equals(alg)) {
                return SecretKeyDerivation.deriveSecretKey(clientSecret, 192);
            }
            if (JWEAlgorithm.A256KW.equals(alg)) {
                return SecretKeyDerivation.deriveSecretKey(clientSecret, 256);
            }
        } else if (JWEAlgorithm.Family.AES_GCM_KW.contains(alg)) {
            if (JWEAlgorithm.A128GCMKW.equals(alg)) {
                return SecretKeyDerivation.deriveSecretKey(clientSecret, 128);
            }
            if (JWEAlgorithm.A192GCMKW.equals(alg)) {
                return SecretKeyDerivation.deriveSecretKey(clientSecret, 192);
            }
            if (JWEAlgorithm.A256GCMKW.equals(alg)) {
                return SecretKeyDerivation.deriveSecretKey(clientSecret, 256);
            }
        }
        throw new JOSEException("Unsupported JWE algorithm / method: alg=" + alg + " enc=" + enc);
    }

    public static SecretKey deriveSecretKey(Secret clientSecret, int bits) throws JOSEException {
        byte[] keyBytes;
        byte[] hash;
        int hashBitLength;
        switch (bits) {
            case 128: 
            case 192: 
            case 256: {
                hashBitLength = 256;
                break;
            }
            case 384: {
                hashBitLength = 384;
                break;
            }
            case 512: {
                hashBitLength = 512;
                break;
            }
            default: {
                throw new JOSEException("Unsupported secret key length: " + bits + " bits");
            }
        }
        try {
            hash = MessageDigest.getInstance("SHA-" + hashBitLength).digest(clientSecret.getValueBytes());
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        switch (bits) {
            case 128: {
                keyBytes = ByteUtils.subArray(hash, ByteUtils.byteLength(128), ByteUtils.byteLength(128));
                break;
            }
            case 192: {
                keyBytes = ByteUtils.subArray(hash, ByteUtils.byteLength(64), ByteUtils.byteLength(192));
                break;
            }
            case 256: 
            case 384: 
            case 512: {
                keyBytes = hash;
                break;
            }
            default: {
                throw new JOSEException("Unsupported secret key length: " + bits + " bits");
            }
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    private SecretKeyDerivation() {
    }
}

