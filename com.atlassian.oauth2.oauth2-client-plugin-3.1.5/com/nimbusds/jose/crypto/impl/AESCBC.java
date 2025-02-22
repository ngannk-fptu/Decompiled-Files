/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.crypto.impl.CipherHelper;
import com.nimbusds.jose.crypto.impl.CompositeKey;
import com.nimbusds.jose.crypto.impl.HMAC;
import com.nimbusds.jose.crypto.impl.LegacyConcatKDF;
import com.nimbusds.jose.crypto.utils.ConstantTimeUtils;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class AESCBC {
    public static final int IV_BIT_LENGTH = 128;

    public static byte[] generateIV(SecureRandom randomGen) {
        byte[] bytes = new byte[ByteUtils.byteLength(128)];
        randomGen.nextBytes(bytes);
        return bytes;
    }

    private static Cipher createAESCBCCipher(SecretKey secretKey, boolean forEncryption, byte[] iv, Provider provider) throws JOSEException {
        Cipher cipher;
        try {
            cipher = CipherHelper.getInstance("AES/CBC/PKCS5Padding", provider);
            SecretKeySpec keyspec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            if (forEncryption) {
                cipher.init(1, (Key)keyspec, ivSpec);
            } else {
                cipher.init(2, (Key)keyspec, ivSpec);
            }
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
        return cipher;
    }

    public static byte[] encrypt(SecretKey secretKey, byte[] iv, byte[] plainText, Provider provider) throws JOSEException {
        Cipher cipher = AESCBC.createAESCBCCipher(secretKey, true, iv, provider);
        try {
            return cipher.doFinal(plainText);
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public static AuthenticatedCipherText encryptAuthenticated(SecretKey secretKey, byte[] iv, byte[] plainText, byte[] aad, Provider ceProvider, Provider macProvider) throws JOSEException {
        CompositeKey compositeKey = new CompositeKey(secretKey);
        byte[] cipherText = AESCBC.encrypt(compositeKey.getAESKey(), iv, plainText, ceProvider);
        byte[] al = AAD.computeLength(aad);
        int hmacInputLength = aad.length + iv.length + cipherText.length + al.length;
        byte[] hmacInput = ByteBuffer.allocate(hmacInputLength).put(aad).put(iv).put(cipherText).put(al).array();
        byte[] hmac = HMAC.compute(compositeKey.getMACKey(), hmacInput, macProvider);
        byte[] authTag = Arrays.copyOf(hmac, compositeKey.getTruncatedMACByteLength());
        return new AuthenticatedCipherText(cipherText, authTag);
    }

    public static AuthenticatedCipherText encryptWithConcatKDF(JWEHeader header, SecretKey secretKey, Base64URL encryptedKey, byte[] iv, byte[] plainText, Provider ceProvider, Provider macProvider) throws JOSEException {
        byte[] epu = null;
        if (header.getCustomParam("epu") instanceof String) {
            epu = new Base64URL((String)header.getCustomParam("epu")).decode();
        }
        byte[] epv = null;
        if (header.getCustomParam("epv") instanceof String) {
            epv = new Base64URL((String)header.getCustomParam("epv")).decode();
        }
        SecretKey altCEK = LegacyConcatKDF.generateCEK(secretKey, header.getEncryptionMethod(), epu, epv);
        byte[] cipherText = AESCBC.encrypt(altCEK, iv, plainText, ceProvider);
        SecretKey cik = LegacyConcatKDF.generateCIK(secretKey, header.getEncryptionMethod(), epu, epv);
        String macInput = header.toBase64URL() + "." + encryptedKey + "." + Base64URL.encode(iv) + "." + Base64URL.encode(cipherText);
        byte[] mac = HMAC.compute(cik, macInput.getBytes(StandardCharset.UTF_8), macProvider);
        return new AuthenticatedCipherText(cipherText, mac);
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] iv, byte[] cipherText, Provider provider) throws JOSEException {
        Cipher cipher = AESCBC.createAESCBCCipher(secretKey, false, iv, provider);
        try {
            return cipher.doFinal(cipherText);
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public static byte[] decryptAuthenticated(SecretKey secretKey, byte[] iv, byte[] cipherText, byte[] aad, byte[] authTag, Provider ceProvider, Provider macProvider) throws JOSEException {
        CompositeKey compositeKey = new CompositeKey(secretKey);
        byte[] al = AAD.computeLength(aad);
        int hmacInputLength = aad.length + iv.length + cipherText.length + al.length;
        byte[] hmacInput = ByteBuffer.allocate(hmacInputLength).put(aad).put(iv).put(cipherText).put(al).array();
        byte[] hmac = HMAC.compute(compositeKey.getMACKey(), hmacInput, macProvider);
        byte[] expectedAuthTag = Arrays.copyOf(hmac, compositeKey.getTruncatedMACByteLength());
        if (!ConstantTimeUtils.areEqual(expectedAuthTag, authTag)) {
            throw new JOSEException("MAC check failed");
        }
        return AESCBC.decrypt(compositeKey.getAESKey(), iv, cipherText, ceProvider);
    }

    public static byte[] decryptWithConcatKDF(JWEHeader header, SecretKey secretKey, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag, Provider ceProvider, Provider macProvider) throws JOSEException {
        byte[] epu = null;
        if (header.getCustomParam("epu") instanceof String) {
            epu = new Base64URL((String)header.getCustomParam("epu")).decode();
        }
        byte[] epv = null;
        if (header.getCustomParam("epv") instanceof String) {
            epv = new Base64URL((String)header.getCustomParam("epv")).decode();
        }
        SecretKey cik = LegacyConcatKDF.generateCIK(secretKey, header.getEncryptionMethod(), epu, epv);
        String macInput = header.toBase64URL().toString() + "." + encryptedKey.toString() + "." + iv.toString() + "." + cipherText.toString();
        byte[] mac = HMAC.compute(cik, macInput.getBytes(StandardCharset.UTF_8), macProvider);
        if (!ConstantTimeUtils.areEqual(authTag.decode(), mac)) {
            throw new JOSEException("MAC check failed");
        }
        SecretKey cekAlt = LegacyConcatKDF.generateCEK(secretKey, header.getEncryptionMethod(), epu, epv);
        return AESCBC.decrypt(cekAlt, iv.decode(), cipherText.decode(), ceProvider);
    }

    private AESCBC() {
    }
}

