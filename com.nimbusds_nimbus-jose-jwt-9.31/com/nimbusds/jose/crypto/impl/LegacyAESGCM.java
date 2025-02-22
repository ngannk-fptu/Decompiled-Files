/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 *  org.bouncycastle.crypto.BlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.InvalidCipherTextException
 *  org.bouncycastle.crypto.engines.AESEngine
 *  org.bouncycastle.crypto.modes.GCMBlockCipher
 *  org.bouncycastle.crypto.params.AEADParameters
 *  org.bouncycastle.crypto.params.KeyParameter
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

@ThreadSafe
public class LegacyAESGCM {
    public static final int AUTH_TAG_BIT_LENGTH = 128;

    public static AESEngine createAESCipher(SecretKey secretKey, boolean forEncryption) {
        AESEngine cipher = new AESEngine();
        KeyParameter cipherParams = new KeyParameter(secretKey.getEncoded());
        cipher.init(forEncryption, (CipherParameters)cipherParams);
        return cipher;
    }

    private static GCMBlockCipher createAESGCMCipher(SecretKey secretKey, boolean forEncryption, byte[] iv, byte[] authData) {
        AESEngine cipher = LegacyAESGCM.createAESCipher(secretKey, forEncryption);
        GCMBlockCipher gcm = new GCMBlockCipher((BlockCipher)cipher);
        AEADParameters aeadParams = new AEADParameters(new KeyParameter(secretKey.getEncoded()), 128, iv, authData);
        gcm.init(forEncryption, (CipherParameters)aeadParams);
        return gcm;
    }

    public static AuthenticatedCipherText encrypt(SecretKey secretKey, byte[] iv, byte[] plainText, byte[] authData) throws JOSEException {
        GCMBlockCipher cipher = LegacyAESGCM.createAESGCMCipher(secretKey, true, iv, authData);
        int outputLength = cipher.getOutputSize(plainText.length);
        byte[] output = new byte[outputLength];
        int outputOffset = cipher.processBytes(plainText, 0, plainText.length, output, 0);
        try {
            outputOffset += cipher.doFinal(output, outputOffset);
        }
        catch (InvalidCipherTextException e) {
            throw new JOSEException("Couldn't generate GCM authentication tag: " + e.getMessage(), e);
        }
        int authTagLength = 16;
        byte[] cipherText = new byte[outputOffset - authTagLength];
        byte[] authTag = new byte[authTagLength];
        System.arraycopy(output, 0, cipherText, 0, cipherText.length);
        System.arraycopy(output, outputOffset - authTagLength, authTag, 0, authTag.length);
        return new AuthenticatedCipherText(cipherText, authTag);
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] iv, byte[] cipherText, byte[] authData, byte[] authTag) throws JOSEException {
        GCMBlockCipher cipher = LegacyAESGCM.createAESGCMCipher(secretKey, false, iv, authData);
        byte[] input = new byte[cipherText.length + authTag.length];
        System.arraycopy(cipherText, 0, input, 0, cipherText.length);
        System.arraycopy(authTag, 0, input, cipherText.length, authTag.length);
        int outputLength = cipher.getOutputSize(input.length);
        byte[] output = new byte[outputLength];
        int outputOffset = cipher.processBytes(input, 0, input.length, output, 0);
        try {
            outputOffset += cipher.doFinal(output, outputOffset);
        }
        catch (InvalidCipherTextException e) {
            throw new JOSEException("Couldn't validate GCM authentication tag: " + e.getMessage(), e);
        }
        return output;
    }

    private LegacyAESGCM() {
    }
}

