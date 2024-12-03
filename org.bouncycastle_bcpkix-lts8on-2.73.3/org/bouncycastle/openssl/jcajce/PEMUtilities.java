/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.openssl.jcajce;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

class PEMUtilities {
    private static final Map KEYSIZES = new HashMap();
    private static final Set PKCS5_SCHEME_1 = new HashSet();
    private static final Set PKCS5_SCHEME_2 = new HashSet();
    private static final Map PRFS = new HashMap();
    private static final Map PRFS_SALT = new HashMap();
    private static final Map CIPHER_NAMES = new HashMap();
    private static final Map KEY_NAMES = new HashMap();

    PEMUtilities() {
    }

    static int getKeySize(String algorithm) {
        if (!KEYSIZES.containsKey(algorithm)) {
            throw new IllegalStateException("no key size for algorithm: " + algorithm);
        }
        return (Integer)KEYSIZES.get(algorithm);
    }

    static int getSaltSize(ASN1ObjectIdentifier algorithm) {
        if (!PRFS_SALT.containsKey(algorithm)) {
            throw new IllegalStateException("no salt size for algorithm: " + algorithm);
        }
        return (Integer)PRFS_SALT.get(algorithm);
    }

    static boolean isHmacSHA1(AlgorithmIdentifier prf) {
        return prf == null || prf.getAlgorithm().equals((ASN1Primitive)PKCSObjectIdentifiers.id_hmacWithSHA1);
    }

    static boolean isPKCS5Scheme1(ASN1ObjectIdentifier algOid) {
        return PKCS5_SCHEME_1.contains(algOid);
    }

    static boolean isPKCS5Scheme2(ASN1ObjectIdentifier algOid) {
        return PKCS5_SCHEME_2.contains(algOid);
    }

    public static boolean isPKCS12(ASN1ObjectIdentifier algOid) {
        return algOid.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
    }

    public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper helper, String algorithm, char[] password, byte[] salt, int iterationCount) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyGen = helper.createSecretKeyFactory("PBKDF2with8BIT");
        SecretKey sKey = keyGen.generateSecret(new PBEKeySpec(password, salt, iterationCount, PEMUtilities.getKeySize(algorithm)));
        return new SecretKeySpec(sKey.getEncoded(), PEMUtilities.getAlgorithmName(algorithm));
    }

    public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper helper, String algorithm, char[] password, byte[] salt, int iterationCount, AlgorithmIdentifier prf) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        String prfName = (String)PRFS.get(prf.getAlgorithm());
        if (prfName == null) {
            throw new NoSuchAlgorithmException("unknown PRF in PKCS#2: " + prf.getAlgorithm());
        }
        SecretKeyFactory keyGen = helper.createSecretKeyFactory(prfName);
        SecretKey sKey = keyGen.generateSecret(new PBEKeySpec(password, salt, iterationCount, PEMUtilities.getKeySize(algorithm)));
        return new SecretKeySpec(sKey.getEncoded(), algorithm);
    }

    static byte[] crypt(boolean encrypt, JcaJceHelper helper, byte[] bytes, char[] password, String dekAlgName, byte[] iv) throws PEMException {
        SecretKey sKey;
        String alg;
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        String blockMode = "CBC";
        String padding = "PKCS5Padding";
        if (dekAlgName.endsWith("-CFB")) {
            blockMode = "CFB";
            padding = "NoPadding";
        }
        if (dekAlgName.endsWith("-ECB") || "DES-EDE".equals(dekAlgName) || "DES-EDE3".equals(dekAlgName)) {
            blockMode = "ECB";
            paramSpec = null;
        }
        if (dekAlgName.endsWith("-OFB")) {
            blockMode = "OFB";
            padding = "NoPadding";
        }
        if (dekAlgName.startsWith("DES-EDE")) {
            alg = "DESede";
            boolean des2 = !dekAlgName.startsWith("DES-EDE3");
            sKey = PEMUtilities.getKey(helper, password, alg, 24, iv, des2);
        } else if (dekAlgName.startsWith("DES-")) {
            alg = "DES";
            sKey = PEMUtilities.getKey(helper, password, alg, 8, iv);
        } else if (dekAlgName.startsWith("BF-")) {
            alg = "Blowfish";
            sKey = PEMUtilities.getKey(helper, password, alg, 16, iv);
        } else if (dekAlgName.startsWith("RC2-")) {
            alg = "RC2";
            int keyBits = 128;
            if (dekAlgName.startsWith("RC2-40-")) {
                keyBits = 40;
            } else if (dekAlgName.startsWith("RC2-64-")) {
                keyBits = 64;
            }
            sKey = PEMUtilities.getKey(helper, password, alg, keyBits / 8, iv);
            paramSpec = paramSpec == null ? new RC2ParameterSpec(keyBits) : new RC2ParameterSpec(keyBits, iv);
        } else if (dekAlgName.startsWith("AES-")) {
            int keyBits;
            alg = "AES";
            byte[] salt = iv;
            if (salt.length > 8) {
                salt = new byte[8];
                System.arraycopy(iv, 0, salt, 0, 8);
            }
            if (dekAlgName.startsWith("AES-128-")) {
                keyBits = 128;
            } else if (dekAlgName.startsWith("AES-192-")) {
                keyBits = 192;
            } else if (dekAlgName.startsWith("AES-256-")) {
                keyBits = 256;
            } else {
                throw new EncryptionException("unknown AES encryption with private key");
            }
            sKey = PEMUtilities.getKey(helper, password, "AES", keyBits / 8, salt);
        } else {
            throw new EncryptionException("unknown encryption with private key");
        }
        String transformation = alg + "/" + blockMode + "/" + padding;
        try {
            int mode;
            Cipher c = helper.createCipher(transformation);
            int n = mode = encrypt ? 1 : 2;
            if (paramSpec == null) {
                c.init(mode, sKey);
            } else {
                c.init(mode, (Key)sKey, paramSpec);
            }
            return c.doFinal(bytes);
        }
        catch (Exception e) {
            throw new EncryptionException("exception using cipher - please check password and data.", (Throwable)e);
        }
    }

    private static SecretKey getKey(JcaJceHelper helper, char[] password, String algorithm, int keyLength, byte[] salt) throws PEMException {
        return PEMUtilities.getKey(helper, password, algorithm, keyLength, salt, false);
    }

    private static SecretKey getKey(JcaJceHelper helper, char[] password, String algorithm, int keyLength, byte[] salt, boolean des2) throws PEMException {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, 1, keyLength * 8);
            SecretKeyFactory keyFactory = helper.createSecretKeyFactory("PBKDF-OpenSSL");
            byte[] key = keyFactory.generateSecret(spec).getEncoded();
            if (des2 && key.length >= 24) {
                System.arraycopy(key, 0, key, 16, 8);
            }
            return new SecretKeySpec(key, algorithm);
        }
        catch (GeneralSecurityException e) {
            throw new PEMException("Unable to create OpenSSL PBDKF: " + e.getMessage(), e);
        }
    }

    static String getCipherName(ASN1ObjectIdentifier oid) {
        String name = (String)CIPHER_NAMES.get(oid);
        if (name != null) {
            return name;
        }
        return oid.getId();
    }

    static String getAlgorithmName(String oid) {
        String name = (String)KEY_NAMES.get(oid);
        if (name != null) {
            return name;
        }
        return oid;
    }

    static {
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
        PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
        PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
        PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
        PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
        KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf((int)192));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf((int)128));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf((int)192));
        KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf((int)256));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId(), Integers.valueOf((int)128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf((int)40));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf((int)128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf((int)192));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf((int)128));
        KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf((int)40));
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "PBKDF2withHMACSHA1");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "PBKDF2withHMACSHA256");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "PBKDF2withHMACSHA512");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "PBKDF2withHMACSHA224");
        PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "PBKDF2withHMACSHA384");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, "PBKDF2withHMACSHA3-224");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, "PBKDF2withHMACSHA3-256");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, "PBKDF2withHMACSHA3-384");
        PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, "PBKDF2withHMACSHA3-512");
        PRFS.put(CryptoProObjectIdentifiers.gostR3411Hmac, "PBKDF2withHMACGOST3411");
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf((int)20));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf((int)32));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf((int)64));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf((int)28));
        PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf((int)48));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf((int)28));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf((int)32));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf((int)48));
        PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf((int)64));
        PRFS_SALT.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf((int)32));
        CIPHER_NAMES.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
        CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes128_CBC, "AES/CBC/PKCS7Padding");
        CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes192_CBC, "AES/CBC/PKCS7Padding");
        CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes256_CBC, "AES/CBC/PKCS7Padding");
        KEY_NAMES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DESEDE");
        KEY_NAMES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), "AES");
        KEY_NAMES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), "AES");
        KEY_NAMES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), "AES");
    }
}

