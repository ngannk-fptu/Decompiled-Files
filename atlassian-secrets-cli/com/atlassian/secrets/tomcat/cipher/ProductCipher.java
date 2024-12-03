/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.tomcat.cipher;

import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.tomcat.cipher.EncryptionResult;
import com.atlassian.secrets.tomcat.cipher.SerializationService;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

public class ProductCipher {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM_KEY = "AES";
    private static final int ALGORITHM_KEY_SIZE = 128;
    private final SerializationService serializationService = new SerializationService();

    public EncryptionResult encrypt(String plainTextPassword, Optional<String> existingEncryptionKeyFile) {
        try {
            long timeStamp = System.currentTimeMillis();
            String encryptionKeyFile = existingEncryptionKeyFile.orElse("encryptionKey_" + timeStamp);
            String encryptedPasswordFile = "encryptedPassword_" + timeStamp;
            SecretKeySpec secretKey = existingEncryptionKeyFile.map(keyFile -> this.serializationService.load((String)keyFile, SecretKeySpec.class)).orElseGet(() -> this.createSecretKey(encryptionKeyFile));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, secretKey);
            SealedObject sealedObject = new SealedObject((Serializable)((Object)plainTextPassword), cipher);
            this.serializationService.save(encryptedPasswordFile, sealedObject);
            return new EncryptionResult(encryptionKeyFile, encryptedPasswordFile);
        }
        catch (IOException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new SecretStoreException(e);
        }
    }

    public String decrypt(String passwordFile, String keyFile) {
        SealedObject sealedObject = this.serializationService.load(passwordFile, SealedObject.class);
        SecretKeySpec secretKey = this.serializationService.load(keyFile, SecretKeySpec.class);
        try {
            return (String)sealedObject.getObject(secretKey);
        }
        catch (IOException | ClassNotFoundException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new SecretStoreException(e);
        }
    }

    private SecretKeySpec createSecretKey(String encryptionKeyFile) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM_KEY);
            keyGen.init(128);
            SecretKeySpec secretKey = (SecretKeySpec)keyGen.generateKey();
            this.serializationService.save(encryptionKeyFile, secretKey);
            return secretKey;
        }
        catch (NoSuchAlgorithmException e) {
            throw new SecretStoreException(e);
        }
    }
}

