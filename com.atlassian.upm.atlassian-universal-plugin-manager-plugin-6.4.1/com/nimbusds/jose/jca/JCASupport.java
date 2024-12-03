/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jca;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public final class JCASupport {
    public static boolean isUnlimitedStrength() {
        try {
            return Cipher.getMaxAllowedKeyLength("AES") >= 256;
        }
        catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static boolean isSupported(Algorithm alg) {
        if (alg instanceof JWSAlgorithm) {
            return JCASupport.isSupported((JWSAlgorithm)alg);
        }
        if (alg instanceof JWEAlgorithm) {
            return JCASupport.isSupported((JWEAlgorithm)alg);
        }
        if (alg instanceof EncryptionMethod) {
            return JCASupport.isSupported((EncryptionMethod)alg);
        }
        throw new IllegalArgumentException("Unexpected algorithm class: " + alg.getClass().getCanonicalName());
    }

    public static boolean isSupported(Algorithm alg, Provider provider) {
        if (alg instanceof JWSAlgorithm) {
            return JCASupport.isSupported((JWSAlgorithm)alg, provider);
        }
        if (alg instanceof JWEAlgorithm) {
            return JCASupport.isSupported((JWEAlgorithm)alg, provider);
        }
        if (alg instanceof EncryptionMethod) {
            return JCASupport.isSupported((EncryptionMethod)alg, provider);
        }
        throw new IllegalArgumentException("Unexpected algorithm class: " + alg.getClass().getCanonicalName());
    }

    public static boolean isSupported(JWSAlgorithm alg) {
        if (alg.getName().equals(Algorithm.NONE.getName())) {
            return true;
        }
        for (Provider p : Security.getProviders()) {
            if (!JCASupport.isSupported(alg, p)) continue;
            return true;
        }
        return false;
    }

    public static boolean isSupported(JWSAlgorithm alg, Provider provider) {
        if (JWSAlgorithm.Family.HMAC_SHA.contains(alg)) {
            String jcaName;
            if (alg.equals(JWSAlgorithm.HS256)) {
                jcaName = "HMACSHA256";
            } else if (alg.equals(JWSAlgorithm.HS384)) {
                jcaName = "HMACSHA384";
            } else if (alg.equals(JWSAlgorithm.HS512)) {
                jcaName = "HMACSHA512";
            } else {
                return false;
            }
            return provider.getService("KeyGenerator", jcaName) != null;
        }
        if (JWSAlgorithm.Family.RSA.contains(alg)) {
            String jcaName;
            String jcaNameAlt = null;
            if (alg.equals(JWSAlgorithm.RS256)) {
                jcaName = "SHA256withRSA";
            } else if (alg.equals(JWSAlgorithm.RS384)) {
                jcaName = "SHA384withRSA";
            } else if (alg.equals(JWSAlgorithm.RS512)) {
                jcaName = "SHA512withRSA";
            } else if (alg.equals(JWSAlgorithm.PS256)) {
                jcaName = "RSASSA-PSS";
                jcaNameAlt = "SHA256withRSAandMGF1";
            } else if (alg.equals(JWSAlgorithm.PS384)) {
                jcaName = "RSASSA-PSS";
                jcaNameAlt = "SHA384withRSAandMGF1";
            } else if (alg.equals(JWSAlgorithm.PS512)) {
                jcaName = "RSASSA-PSS";
                jcaNameAlt = "SHA512withRSAandMGF1";
            } else {
                return false;
            }
            return provider.getService("Signature", jcaName) != null || jcaNameAlt != null && provider.getService("Signature", jcaNameAlt) != null;
        }
        if (JWSAlgorithm.Family.EC.contains(alg)) {
            String jcaName;
            if (alg.equals(JWSAlgorithm.ES256)) {
                jcaName = "SHA256withECDSA";
            } else if (alg.equals(JWSAlgorithm.ES384)) {
                jcaName = "SHA384withECDSA";
            } else if (alg.equals(JWSAlgorithm.ES512)) {
                jcaName = "SHA512withECDSA";
            } else {
                return false;
            }
            return provider.getService("Signature", jcaName) != null;
        }
        return false;
    }

    public static boolean isSupported(JWEAlgorithm alg) {
        for (Provider p : Security.getProviders()) {
            if (!JCASupport.isSupported(alg, p)) continue;
            return true;
        }
        return false;
    }

    public static boolean isSupported(JWEAlgorithm alg, Provider provider) {
        if (JWEAlgorithm.Family.RSA.contains(alg)) {
            String jcaName;
            if (alg.equals(JWEAlgorithm.RSA1_5)) {
                jcaName = "RSA/ECB/PKCS1Padding";
            } else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
                jcaName = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
            } else if (alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
                jcaName = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
            } else {
                return false;
            }
            try {
                Cipher.getInstance(jcaName, provider);
            }
            catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                return false;
            }
            return true;
        }
        if (JWEAlgorithm.Family.AES_KW.contains(alg)) {
            return provider.getService("Cipher", "AESWrap") != null;
        }
        if (JWEAlgorithm.Family.ECDH_ES.contains(alg)) {
            return provider.getService("KeyAgreement", "ECDH") != null;
        }
        if (JWEAlgorithm.Family.AES_GCM_KW.contains(alg)) {
            try {
                Cipher.getInstance("AES/GCM/NoPadding", provider);
            }
            catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                return false;
            }
            return true;
        }
        if (JWEAlgorithm.Family.PBES2.contains(alg)) {
            String hmac = alg.equals(JWEAlgorithm.PBES2_HS256_A128KW) ? "HmacSHA256" : (alg.equals(JWEAlgorithm.PBES2_HS384_A192KW) ? "HmacSHA384" : "HmacSHA512");
            return provider.getService("KeyGenerator", hmac) != null;
        }
        return JWEAlgorithm.DIR.equals(alg);
    }

    public static boolean isSupported(EncryptionMethod enc) {
        for (Provider p : Security.getProviders()) {
            if (!JCASupport.isSupported(enc, p)) continue;
            return true;
        }
        return false;
    }

    public static boolean isSupported(EncryptionMethod enc, Provider provider) {
        if (EncryptionMethod.Family.AES_CBC_HMAC_SHA.contains(enc)) {
            try {
                Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
            }
            catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                return false;
            }
            String hmac = enc.equals(EncryptionMethod.A128CBC_HS256) ? "HmacSHA256" : (enc.equals(EncryptionMethod.A192CBC_HS384) ? "HmacSHA384" : "HmacSHA512");
            return provider.getService("KeyGenerator", hmac) != null;
        }
        if (EncryptionMethod.Family.AES_GCM.contains(enc)) {
            try {
                Cipher.getInstance("AES/GCM/NoPadding", provider);
            }
            catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private JCASupport() {
    }
}

