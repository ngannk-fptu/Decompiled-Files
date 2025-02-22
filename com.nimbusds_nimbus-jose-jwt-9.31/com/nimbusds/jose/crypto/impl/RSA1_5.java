/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.CipherHelper;
import com.nimbusds.jose.util.ByteUtils;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSA1_5 {
    public static byte[] encryptCEK(RSAPublicKey pub, SecretKey cek, Provider provider) throws JOSEException {
        try {
            Cipher cipher = CipherHelper.getInstance("RSA/ECB/PKCS1Padding", provider);
            cipher.init(1, pub);
            return cipher.doFinal(cek.getEncoded());
        }
        catch (IllegalBlockSizeException e) {
            throw new JOSEException("RSA block size exception: The RSA key is too short, use a longer one", e);
        }
        catch (Exception e) {
            throw new JOSEException("Couldn't encrypt Content Encryption Key (CEK): " + e.getMessage(), e);
        }
    }

    public static SecretKey decryptCEK(PrivateKey priv, byte[] encryptedCEK, int keyLength, Provider provider) throws JOSEException {
        try {
            Cipher cipher = CipherHelper.getInstance("RSA/ECB/PKCS1Padding", provider);
            cipher.init(2, priv);
            byte[] secretKeyBytes = cipher.doFinal(encryptedCEK);
            if (ByteUtils.safeBitLength(secretKeyBytes) != keyLength) {
                return null;
            }
            return new SecretKeySpec(secretKeyBytes, "AES");
        }
        catch (Exception e) {
            throw new JOSEException("Couldn't decrypt Content Encryption Key (CEK): " + e.getMessage(), e);
        }
    }

    private RSA1_5() {
    }
}

