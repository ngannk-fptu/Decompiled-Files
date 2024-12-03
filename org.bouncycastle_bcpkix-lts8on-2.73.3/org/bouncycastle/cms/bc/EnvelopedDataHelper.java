/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.BlockCipher
 *  org.bouncycastle.crypto.CipherKeyGenerator
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.Wrapper
 *  org.bouncycastle.crypto.digests.SHA1Digest
 *  org.bouncycastle.crypto.digests.SHA224Digest
 *  org.bouncycastle.crypto.digests.SHA256Digest
 *  org.bouncycastle.crypto.digests.SHA384Digest
 *  org.bouncycastle.crypto.digests.SHA512Digest
 *  org.bouncycastle.crypto.engines.AESEngine
 *  org.bouncycastle.crypto.engines.DESEngine
 *  org.bouncycastle.crypto.engines.DESedeEngine
 *  org.bouncycastle.crypto.engines.RC2Engine
 *  org.bouncycastle.crypto.engines.RFC3211WrapEngine
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.util.AlgorithmIdentifierFactory
 *  org.bouncycastle.crypto.util.CipherFactory
 *  org.bouncycastle.crypto.util.CipherKeyGeneratorFactory
 */
package org.bouncycastle.cms.bc;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.AlgorithmIdentifierFactory;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.crypto.util.CipherKeyGeneratorFactory;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;

class EnvelopedDataHelper {
    protected static final Map BASE_CIPHER_NAMES = new HashMap();
    protected static final Map MAC_ALG_NAMES = new HashMap();
    private static final Set authEnvelopedAlgorithms = new HashSet();
    private static final Map prfs = EnvelopedDataHelper.createTable();

    private static Map createTable() {
        HashMap<ASN1ObjectIdentifier, BcDigestProvider> table = new HashMap<ASN1ObjectIdentifier, BcDigestProvider>();
        table.put(PKCSObjectIdentifiers.id_hmacWithSHA1, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA1Digest();
            }
        });
        table.put(PKCSObjectIdentifiers.id_hmacWithSHA224, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA224Digest();
            }
        });
        table.put(PKCSObjectIdentifiers.id_hmacWithSHA256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return SHA256Digest.newInstance();
            }
        });
        table.put(PKCSObjectIdentifiers.id_hmacWithSHA384, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA384Digest();
            }
        });
        table.put(PKCSObjectIdentifiers.id_hmacWithSHA512, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA512Digest();
            }
        });
        return Collections.unmodifiableMap(table);
    }

    EnvelopedDataHelper() {
    }

    static ExtendedDigest getPRF(AlgorithmIdentifier algID) throws OperatorCreationException {
        return ((BcDigestProvider)prfs.get(algID.getAlgorithm())).get(null);
    }

    static Wrapper createRFC3211Wrapper(ASN1ObjectIdentifier algorithm) throws CMSException {
        if (NISTObjectIdentifiers.id_aes128_CBC.equals((ASN1Primitive)algorithm) || NISTObjectIdentifiers.id_aes192_CBC.equals((ASN1Primitive)algorithm) || NISTObjectIdentifiers.id_aes256_CBC.equals((ASN1Primitive)algorithm)) {
            return new RFC3211WrapEngine((BlockCipher)AESEngine.newInstance());
        }
        if (PKCSObjectIdentifiers.des_EDE3_CBC.equals((ASN1Primitive)algorithm)) {
            return new RFC3211WrapEngine((BlockCipher)new DESedeEngine());
        }
        if (OIWObjectIdentifiers.desCBC.equals((ASN1Primitive)algorithm)) {
            return new RFC3211WrapEngine((BlockCipher)new DESEngine());
        }
        if (PKCSObjectIdentifiers.RC2_CBC.equals((ASN1Primitive)algorithm)) {
            return new RFC3211WrapEngine((BlockCipher)new RC2Engine());
        }
        throw new CMSException("cannot recognise wrapper: " + algorithm);
    }

    static Object createContentCipher(boolean forEncryption, CipherParameters encKey, AlgorithmIdentifier encryptionAlgID) throws CMSException {
        try {
            return CipherFactory.createContentCipher((boolean)forEncryption, (CipherParameters)encKey, (AlgorithmIdentifier)encryptionAlgID);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException(e.getMessage(), e);
        }
    }

    AlgorithmIdentifier generateEncryptionAlgID(ASN1ObjectIdentifier encryptionOID, KeyParameter encKey, SecureRandom random) throws CMSException {
        try {
            return AlgorithmIdentifierFactory.generateEncryptionAlgID((ASN1ObjectIdentifier)encryptionOID, (int)(encKey.getKey().length * 8), (SecureRandom)random);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException(e.getMessage(), e);
        }
    }

    CipherKeyGenerator createKeyGenerator(ASN1ObjectIdentifier algorithm, int keySize, SecureRandom random) throws CMSException {
        try {
            return CipherKeyGeneratorFactory.createKeyGenerator((ASN1ObjectIdentifier)algorithm, (SecureRandom)random);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException(e.getMessage(), e);
        }
    }

    boolean isAuthEnveloped(ASN1ObjectIdentifier algorithm) {
        return authEnvelopedAlgorithms.contains(algorithm);
    }

    static {
        BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
        BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
        MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
        MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes128_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes192_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes256_GCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes128_CCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes192_CCM);
        authEnvelopedAlgorithms.add(NISTObjectIdentifiers.id_aes256_CCM);
    }
}

