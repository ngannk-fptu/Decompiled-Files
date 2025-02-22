/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.crypto.tink.subtle.X25519
 */
package com.nimbusds.jose.crypto.impl;

import com.google.crypto.tink.subtle.X25519;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ConcatKDF;
import com.nimbusds.jose.crypto.impl.ECDHCryptoProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ECDH {
    public static AlgorithmMode resolveAlgorithmMode(JWEAlgorithm alg) throws JOSEException {
        if (alg.equals(JWEAlgorithm.ECDH_ES)) {
            return AlgorithmMode.DIRECT;
        }
        if (alg.equals(JWEAlgorithm.ECDH_ES_A128KW) || alg.equals(JWEAlgorithm.ECDH_ES_A192KW) || alg.equals(JWEAlgorithm.ECDH_ES_A256KW)) {
            return AlgorithmMode.KW;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
    }

    public static int sharedKeyLength(JWEAlgorithm alg, EncryptionMethod enc) throws JOSEException {
        if (alg.equals(JWEAlgorithm.ECDH_ES)) {
            int length = enc.cekBitLength();
            if (length == 0) {
                throw new JOSEException("Unsupported JWE encryption method " + enc);
            }
            return length;
        }
        if (alg.equals(JWEAlgorithm.ECDH_ES_A128KW)) {
            return 128;
        }
        if (alg.equals(JWEAlgorithm.ECDH_ES_A192KW)) {
            return 192;
        }
        if (alg.equals(JWEAlgorithm.ECDH_ES_A256KW)) {
            return 256;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, ECDHCryptoProvider.SUPPORTED_ALGORITHMS));
    }

    public static SecretKey deriveSharedSecret(ECPublicKey publicKey, PrivateKey privateKey, Provider provider) throws JOSEException {
        KeyAgreement keyAgreement;
        try {
            keyAgreement = provider != null ? KeyAgreement.getInstance("ECDH", provider) : KeyAgreement.getInstance("ECDH");
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't get an ECDH key agreement instance: " + e.getMessage(), e);
        }
        try {
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Invalid key for ECDH key agreement: " + e.getMessage(), e);
        }
        return new SecretKeySpec(keyAgreement.generateSecret(), "AES");
    }

    public static SecretKey deriveSharedSecret(OctetKeyPair publicKey, OctetKeyPair privateKey) throws JOSEException {
        byte[] sharedSecretBytes;
        if (publicKey.isPrivate()) {
            throw new JOSEException("Expected public key but received OKP with 'd' value");
        }
        if (!Curve.X25519.equals(publicKey.getCurve())) {
            throw new JOSEException("Expected public key OKP with crv=X25519");
        }
        if (!privateKey.isPrivate()) {
            throw new JOSEException("Expected private key but received OKP without 'd' value");
        }
        if (!Curve.X25519.equals(privateKey.getCurve())) {
            throw new JOSEException("Expected private key OKP with crv=X25519");
        }
        byte[] privateKeyBytes = privateKey.getDecodedD();
        byte[] publicKeyBytes = publicKey.getDecodedX();
        try {
            sharedSecretBytes = X25519.computeSharedSecret((byte[])privateKeyBytes, (byte[])publicKeyBytes);
        }
        catch (InvalidKeyException e) {
            throw new JOSEException(e.getMessage(), e);
        }
        return new SecretKeySpec(sharedSecretBytes, "AES");
    }

    public static SecretKey deriveSharedKey(JWEHeader header, SecretKey Z, ConcatKDF concatKDF) throws JOSEException {
        String algID;
        int sharedKeyLength = ECDH.sharedKeyLength(header.getAlgorithm(), header.getEncryptionMethod());
        AlgorithmMode algMode = ECDH.resolveAlgorithmMode(header.getAlgorithm());
        if (algMode == AlgorithmMode.DIRECT) {
            algID = header.getEncryptionMethod().getName();
        } else if (algMode == AlgorithmMode.KW) {
            algID = header.getAlgorithm().getName();
        } else {
            throw new JOSEException("Unsupported JWE ECDH algorithm mode: " + (Object)((Object)algMode));
        }
        return concatKDF.deriveKey(Z, sharedKeyLength, ConcatKDF.encodeDataWithLength(algID.getBytes(StandardCharsets.US_ASCII)), ConcatKDF.encodeDataWithLength(header.getAgreementPartyUInfo()), ConcatKDF.encodeDataWithLength(header.getAgreementPartyVInfo()), ConcatKDF.encodeIntData(sharedKeyLength), ConcatKDF.encodeNoData());
    }

    private ECDH() {
    }

    public static enum AlgorithmMode {
        DIRECT,
        KW;

    }
}

