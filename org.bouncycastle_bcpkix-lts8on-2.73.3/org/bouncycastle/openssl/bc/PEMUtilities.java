/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.crypto.BlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.DefaultBufferedBlockCipher
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.PBEParametersGenerator
 *  org.bouncycastle.crypto.digests.SHA1Digest
 *  org.bouncycastle.crypto.engines.AESEngine
 *  org.bouncycastle.crypto.engines.BlowfishEngine
 *  org.bouncycastle.crypto.engines.DESEngine
 *  org.bouncycastle.crypto.engines.DESedeEngine
 *  org.bouncycastle.crypto.engines.RC2Engine
 *  org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator
 *  org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
 *  org.bouncycastle.crypto.modes.CBCBlockCipher
 *  org.bouncycastle.crypto.modes.CFBBlockCipher
 *  org.bouncycastle.crypto.modes.OFBBlockCipher
 *  org.bouncycastle.crypto.paddings.BlockCipherPadding
 *  org.bouncycastle.crypto.paddings.PKCS7Padding
 *  org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithIV
 *  org.bouncycastle.crypto.params.RC2Parameters
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.openssl.bc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

class PEMUtilities {
    private static final Map KEYSIZES = new HashMap();
    private static final Set PKCS5_SCHEME_1 = new HashSet();
    private static final Set PKCS5_SCHEME_2 = new HashSet();

    PEMUtilities() {
    }

    static int getKeySize(String algorithm) {
        if (!KEYSIZES.containsKey(algorithm)) {
            throw new IllegalStateException("no key size for algorithm: " + algorithm);
        }
        return (Integer)KEYSIZES.get(algorithm);
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

    public static KeyParameter generateSecretKeyForPKCS5Scheme2(String algorithm, char[] password, byte[] salt, int iterationCount) {
        PKCS5S2ParametersGenerator paramsGen = new PKCS5S2ParametersGenerator((Digest)new SHA1Digest());
        paramsGen.init(PBEParametersGenerator.PKCS5PasswordToBytes((char[])password), salt, iterationCount);
        return (KeyParameter)paramsGen.generateDerivedParameters(PEMUtilities.getKeySize(algorithm));
    }

    static byte[] crypt(boolean encrypt, byte[] bytes, char[] password, String dekAlgName, byte[] iv) throws PEMException {
        DESEngine engine;
        KeyParameter sKey;
        byte[] ivValue = iv;
        String blockMode = "CBC";
        PKCS7Padding padding = new PKCS7Padding();
        if (dekAlgName.endsWith("-CFB")) {
            blockMode = "CFB";
            padding = null;
        }
        if (dekAlgName.endsWith("-ECB") || "DES-EDE".equals(dekAlgName) || "DES-EDE3".equals(dekAlgName)) {
            blockMode = "ECB";
            ivValue = null;
        }
        if (dekAlgName.endsWith("-OFB")) {
            blockMode = "OFB";
            padding = null;
        }
        if (dekAlgName.startsWith("DES-EDE")) {
            boolean des2 = !dekAlgName.startsWith("DES-EDE3");
            sKey = PEMUtilities.getKey(password, 24, iv, des2);
            engine = new DESedeEngine();
        } else if (dekAlgName.startsWith("DES-")) {
            sKey = PEMUtilities.getKey(password, 8, iv);
            engine = new DESEngine();
        } else if (dekAlgName.startsWith("BF-")) {
            sKey = PEMUtilities.getKey(password, 16, iv);
            engine = new BlowfishEngine();
        } else if (dekAlgName.startsWith("RC2-")) {
            int keyBits = 128;
            if (dekAlgName.startsWith("RC2-40-")) {
                keyBits = 40;
            } else if (dekAlgName.startsWith("RC2-64-")) {
                keyBits = 64;
            }
            sKey = new RC2Parameters(PEMUtilities.getKey(password, keyBits / 8, iv).getKey(), keyBits);
            engine = new RC2Engine();
        } else if (dekAlgName.startsWith("AES-")) {
            int keyBits;
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
                throw new EncryptionException("unknown AES encryption with private key: " + dekAlgName);
            }
            sKey = PEMUtilities.getKey(password, keyBits / 8, salt);
            engine = AESEngine.newInstance();
        } else {
            throw new EncryptionException("unknown encryption with private key: " + dekAlgName);
        }
        if (blockMode.equals("CBC")) {
            engine = CBCBlockCipher.newInstance((BlockCipher)engine);
        } else if (blockMode.equals("CFB")) {
            engine = CFBBlockCipher.newInstance((BlockCipher)engine, (int)(engine.getBlockSize() * 8));
        } else if (blockMode.equals("OFB")) {
            engine = new OFBBlockCipher((BlockCipher)engine, engine.getBlockSize() * 8);
        }
        try {
            Object c = padding == null ? new DefaultBufferedBlockCipher((BlockCipher)engine) : new PaddedBufferedBlockCipher((BlockCipher)engine, (BlockCipherPadding)padding);
            if (ivValue == null) {
                c.init(encrypt, (CipherParameters)sKey);
            } else {
                c.init(encrypt, (CipherParameters)new ParametersWithIV((CipherParameters)sKey, ivValue));
            }
            byte[] out = new byte[c.getOutputSize(bytes.length)];
            int procLen = c.processBytes(bytes, 0, bytes.length, out, 0);
            procLen += c.doFinal(out, procLen);
            if (procLen == out.length) {
                return out;
            }
            byte[] rv = new byte[procLen];
            System.arraycopy(out, 0, rv, 0, procLen);
            return rv;
        }
        catch (Exception e) {
            throw new EncryptionException("exception using cipher - please check password and data.", (Throwable)e);
        }
    }

    private static KeyParameter getKey(char[] password, int keyLength, byte[] salt) throws PEMException {
        return PEMUtilities.getKey(password, keyLength, salt, false);
    }

    private static KeyParameter getKey(char[] password, int keyLength, byte[] salt, boolean des2) throws PEMException {
        OpenSSLPBEParametersGenerator paramsGen = new OpenSSLPBEParametersGenerator();
        paramsGen.init(PBEParametersGenerator.PKCS5PasswordToBytes((char[])password), salt, 1);
        KeyParameter kp = (KeyParameter)paramsGen.generateDerivedParameters(keyLength * 8);
        if (des2 && kp.getKey().length == 24) {
            byte[] key = kp.getKey();
            System.arraycopy(key, 0, key, 16, 8);
            return new KeyParameter(key);
        }
        return kp;
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
    }
}

