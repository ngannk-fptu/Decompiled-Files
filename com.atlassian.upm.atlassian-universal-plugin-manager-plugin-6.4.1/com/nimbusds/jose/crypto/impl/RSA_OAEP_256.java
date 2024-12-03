/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.AlgorithmParametersHelper;
import com.nimbusds.jose.crypto.impl.CipherHelper;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSA_OAEP_256 {
    private static final String RSA_OEAP_256_JCA_ALG = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    public static byte[] encryptCEK(RSAPublicKey pub, SecretKey cek, Provider provider) throws JOSEException {
        try {
            AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
            OAEPParameterSpec paramSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            algp.init(paramSpec);
            Cipher cipher = CipherHelper.getInstance(RSA_OEAP_256_JCA_ALG, provider);
            cipher.init(1, (Key)pub, algp);
            return cipher.doFinal(cek.getEncoded());
        }
        catch (IllegalBlockSizeException e) {
            throw new JOSEException("RSA block size exception: The RSA key is too short, try a longer one", e);
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public static SecretKey decryptCEK(PrivateKey priv, byte[] encryptedCEK, Provider provider) throws JOSEException {
        try {
            AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
            OAEPParameterSpec paramSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            algp.init(paramSpec);
            Cipher cipher = CipherHelper.getInstance(RSA_OEAP_256_JCA_ALG, provider);
            cipher.init(2, (Key)priv, algp);
            return new SecretKeySpec(cipher.doFinal(encryptedCEK), "AES");
        }
        catch (Exception e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    private RSA_OAEP_256() {
    }
}

