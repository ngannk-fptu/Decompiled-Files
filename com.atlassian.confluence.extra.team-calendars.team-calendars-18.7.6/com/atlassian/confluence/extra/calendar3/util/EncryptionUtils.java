/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.EncryptionException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionUtils {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);
    private static final String ALGORITHM = "AES";
    private static final String ENCRYPTION_PREFIX = "!!!!";

    public static boolean isEncrypted(String value) {
        return StringUtils.isNotBlank(value) && value.startsWith(ENCRYPTION_PREFIX);
    }

    public static String encrypt(String key, String value) throws EncryptionException {
        if (StringUtils.isBlank(key)) {
            throw new EncryptionException("Cannot encrypt with an empty key");
        }
        if (StringUtils.isBlank(value)) {
            throw new EncryptionException("Cannot encrypt an empty value");
        }
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        }
        catch (NoSuchAlgorithmException e) {
            String msg = String.format("Unable to obtain cipher for %s algorithm: %s", ALGORITHM, e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        catch (NoSuchPaddingException e) {
            String msg = String.format("Unable to obtain cipher for %s algorithm: %s", ALGORITHM, e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        try {
            cipher.init(1, keySpec);
        }
        catch (InvalidKeyException e) {
            String msg = String.format("Invalid Encryption Key: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        try {
            return ENCRYPTION_PREFIX + Base64.encodeBase64String(cipher.doFinal(value.getBytes()));
        }
        catch (IllegalBlockSizeException e) {
            String msg = String.format("Unable to encrypt: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        catch (BadPaddingException e) {
            String msg = String.format("Unable to encrypt: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
    }

    public static String decrypt(String key, String value) throws EncryptionException {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64(key), ALGORITHM);
        try {
            Cipher cipher = null;
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, keySpec);
            return new String(cipher.doFinal(Base64.decodeBase64(value.substring(2))));
        }
        catch (InvalidKeyException e) {
            String msg = String.format("Unable to decrypt value: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        catch (NoSuchAlgorithmException e) {
            String msg = String.format("Unable to decrypt value: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        catch (NoSuchPaddingException e) {
            String msg = String.format("Unable to decrypt value: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        catch (IllegalBlockSizeException e) {
            String msg = String.format("Unable to decrypt value: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        catch (BadPaddingException e) {
            String msg = String.format("Unable to decrypt value: %s", e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
    }

    public static String generateKey() throws EncryptionException {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        }
        catch (NoSuchAlgorithmException e) {
            String msg = String.format("Unable to create KeyGenerator for %s algorithm: %s", ALGORITHM, e.getMessage());
            logger.error(msg, (Throwable)e);
            throw new EncryptionException(msg, e);
        }
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        return Base64.encodeBase64String(key.getEncoded());
    }
}

