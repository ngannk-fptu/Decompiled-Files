/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.kisa.KISAObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.ntt.NTTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.RSASSAPSSparams
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.jcajce.util.AlgorithmParametersUtils
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.MessageDigestUtils
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.operator.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.operator.DefaultSignatureNameFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Integers;

class OperatorHelper {
    private static final Map oids = new HashMap();
    private static final Map asymmetricWrapperAlgNames = new HashMap();
    private static final Map symmetricWrapperAlgNames = new HashMap();
    private static final Map symmetricKeyAlgNames = new HashMap();
    private static final Map symmetricWrapperKeySizes = new HashMap();
    private static DefaultSignatureNameFinder sigFinder = new DefaultSignatureNameFinder();
    private JcaJceHelper helper;

    OperatorHelper(JcaJceHelper helper) {
        this.helper = helper;
    }

    String getWrappingAlgorithmName(ASN1ObjectIdentifier algOid) {
        return (String)symmetricWrapperAlgNames.get(algOid);
    }

    int getKeySizeInBits(ASN1ObjectIdentifier algOid) {
        return (Integer)symmetricWrapperKeySizes.get(algOid);
    }

    KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier algorithm) throws CMSException {
        try {
            String agreementName = null;
            if (agreementName != null) {
                try {
                    return this.helper.createKeyPairGenerator(agreementName);
                }
                catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    // empty catch block
                }
            }
            return this.helper.createKeyPairGenerator(algorithm.getId());
        }
        catch (GeneralSecurityException e) {
            throw new CMSException("cannot create key agreement: " + e.getMessage(), e);
        }
    }

    Cipher createCipher(ASN1ObjectIdentifier algorithm) throws OperatorCreationException {
        try {
            return this.helper.createCipher(algorithm.getId());
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    KeyAgreement createKeyAgreement(ASN1ObjectIdentifier algorithm) throws OperatorCreationException {
        try {
            String agreementName = null;
            if (agreementName != null) {
                try {
                    return this.helper.createKeyAgreement(agreementName);
                }
                catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    // empty catch block
                }
            }
            return this.helper.createKeyAgreement(algorithm.getId());
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create key agreement: " + e.getMessage(), e);
        }
    }

    Cipher createAsymmetricWrapper(ASN1ObjectIdentifier algorithm, Map extraAlgNames) throws OperatorCreationException {
        try {
            block9: {
                String cipherName = null;
                if (!extraAlgNames.isEmpty()) {
                    cipherName = (String)extraAlgNames.get(algorithm);
                }
                if (cipherName == null) {
                    cipherName = (String)asymmetricWrapperAlgNames.get(algorithm);
                }
                if (cipherName != null) {
                    try {
                        return this.helper.createCipher(cipherName);
                    }
                    catch (NoSuchAlgorithmException e) {
                        if (!cipherName.equals("RSA/ECB/PKCS1Padding")) break block9;
                        try {
                            return this.helper.createCipher("RSA/NONE/PKCS1Padding");
                        }
                        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                            // empty catch block
                        }
                    }
                }
            }
            return this.helper.createCipher(algorithm.getId());
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    Cipher createSymmetricWrapper(ASN1ObjectIdentifier algorithm) throws OperatorCreationException {
        try {
            String cipherName = (String)symmetricWrapperAlgNames.get(algorithm);
            if (cipherName != null) {
                try {
                    return this.helper.createCipher(cipherName);
                }
                catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    // empty catch block
                }
            }
            return this.helper.createCipher(algorithm.getId());
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    AlgorithmParameters createAlgorithmParameters(AlgorithmIdentifier cipherAlgId) throws OperatorCreationException {
        AlgorithmParameters parameters = null;
        if (cipherAlgId.getAlgorithm().equals((ASN1Primitive)PKCSObjectIdentifiers.rsaEncryption)) {
            return null;
        }
        if (cipherAlgId.getAlgorithm().equals((ASN1Primitive)PKCSObjectIdentifiers.id_RSAES_OAEP)) {
            try {
                parameters = this.helper.createAlgorithmParameters("OAEP");
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            }
            catch (NoSuchProviderException e) {
                throw new OperatorCreationException("cannot create algorithm parameters: " + e.getMessage(), e);
            }
        }
        if (parameters == null) {
            try {
                parameters = this.helper.createAlgorithmParameters(cipherAlgId.getAlgorithm().getId());
            }
            catch (NoSuchAlgorithmException e) {
                return null;
            }
            catch (NoSuchProviderException e) {
                throw new OperatorCreationException("cannot create algorithm parameters: " + e.getMessage(), e);
            }
        }
        try {
            parameters.init(cipherAlgId.getParameters().toASN1Primitive().getEncoded());
        }
        catch (IOException e) {
            throw new OperatorCreationException("cannot initialise algorithm parameters: " + e.getMessage(), e);
        }
        return parameters;
    }

    MessageDigest createDigest(AlgorithmIdentifier digAlgId) throws GeneralSecurityException {
        MessageDigest dig;
        try {
            dig = digAlgId.getAlgorithm().equals((ASN1Primitive)NISTObjectIdentifiers.id_shake256_len) ? this.helper.createMessageDigest("SHAKE256-" + ASN1Integer.getInstance((Object)digAlgId.getParameters()).getValue()) : (digAlgId.getAlgorithm().equals((ASN1Primitive)NISTObjectIdentifiers.id_shake128_len) ? this.helper.createMessageDigest("SHAKE128-" + ASN1Integer.getInstance((Object)digAlgId.getParameters()).getValue()) : this.helper.createMessageDigest(MessageDigestUtils.getDigestName((ASN1ObjectIdentifier)digAlgId.getAlgorithm())));
        }
        catch (NoSuchAlgorithmException e) {
            if (oids.get(digAlgId.getAlgorithm()) != null) {
                String digestAlgorithm = (String)oids.get(digAlgId.getAlgorithm());
                dig = this.helper.createMessageDigest(digestAlgorithm);
            }
            throw e;
        }
        return dig;
    }

    Signature createSignature(AlgorithmIdentifier sigAlgId) throws GeneralSecurityException {
        ASN1Sequence seq;
        Signature sig;
        String sigName = OperatorHelper.getSignatureName(sigAlgId);
        try {
            sig = this.helper.createSignature(sigName);
        }
        catch (NoSuchAlgorithmException e) {
            if (sigName.endsWith("WITHRSAANDMGF1")) {
                String signatureAlgorithm = sigName.substring(0, sigName.indexOf(87)) + "WITHRSASSA-PSS";
                sig = this.helper.createSignature(signatureAlgorithm);
            }
            throw e;
        }
        if (sigAlgId.getAlgorithm().equals((ASN1Primitive)PKCSObjectIdentifiers.id_RSASSA_PSS) && this.notDefaultPSSParams(seq = ASN1Sequence.getInstance((Object)sigAlgId.getParameters()))) {
            try {
                AlgorithmParameters algParams = this.helper.createAlgorithmParameters("PSS");
                algParams.init(seq.getEncoded());
                sig.setParameter(algParams.getParameterSpec(PSSParameterSpec.class));
            }
            catch (IOException e) {
                throw new GeneralSecurityException("unable to process PSS parameters: " + e.getMessage());
            }
        }
        return sig;
    }

    Signature createRawSignature(AlgorithmIdentifier algorithm) {
        Signature sig;
        try {
            String algName = OperatorHelper.getSignatureName(algorithm);
            algName = "NONE" + algName.substring(algName.indexOf("WITH"));
            sig = this.helper.createSignature(algName);
            if (algorithm.getAlgorithm().equals((ASN1Primitive)PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                AlgorithmParameters params = this.helper.createAlgorithmParameters(algName);
                AlgorithmParametersUtils.loadParameters((AlgorithmParameters)params, (ASN1Encodable)algorithm.getParameters());
                PSSParameterSpec spec = params.getParameterSpec(PSSParameterSpec.class);
                sig.setParameter(spec);
            }
        }
        catch (Exception e) {
            return null;
        }
        return sig;
    }

    private static String getSignatureName(AlgorithmIdentifier sigAlgId) {
        return sigFinder.getAlgorithmName(sigAlgId);
    }

    static String getDigestName(ASN1ObjectIdentifier oid) {
        String name = MessageDigestUtils.getDigestName((ASN1ObjectIdentifier)oid);
        int dIndex = name.indexOf(45);
        if (dIndex > 0 && !name.startsWith("SHA3")) {
            return name.substring(0, dIndex) + name.substring(dIndex + 1);
        }
        return name;
    }

    public X509Certificate convertCertificate(X509CertificateHolder certHolder) throws CertificateException {
        try {
            CertificateFactory certFact = this.helper.createCertificateFactory("X.509");
            return (X509Certificate)certFact.generateCertificate(new ByteArrayInputStream(certHolder.getEncoded()));
        }
        catch (IOException e) {
            throw new OpCertificateException("cannot get encoded form of certificate: " + e.getMessage(), e);
        }
        catch (NoSuchProviderException e) {
            throw new OpCertificateException("cannot find factory provider: " + e.getMessage(), e);
        }
    }

    public PublicKey convertPublicKey(SubjectPublicKeyInfo publicKeyInfo) throws OperatorCreationException {
        try {
            KeyFactory keyFact = this.helper.createKeyFactory(publicKeyInfo.getAlgorithm().getAlgorithm().getId());
            return keyFact.generatePublic(new X509EncodedKeySpec(publicKeyInfo.getEncoded()));
        }
        catch (IOException e) {
            throw new OperatorCreationException("cannot get encoded form of key: " + e.getMessage(), e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new OperatorCreationException("cannot create key factory: " + e.getMessage(), e);
        }
        catch (NoSuchProviderException e) {
            throw new OperatorCreationException("cannot find factory provider: " + e.getMessage(), e);
        }
        catch (InvalidKeySpecException e) {
            throw new OperatorCreationException("cannot create key factory: " + e.getMessage(), e);
        }
    }

    String getKeyAlgorithmName(ASN1ObjectIdentifier oid) {
        String name = (String)symmetricKeyAlgNames.get(oid);
        if (name != null) {
            return name;
        }
        return oid.getId();
    }

    private boolean notDefaultPSSParams(ASN1Sequence seq) throws GeneralSecurityException {
        if (seq == null || seq.size() == 0) {
            return false;
        }
        RSASSAPSSparams pssParams = RSASSAPSSparams.getInstance((Object)seq);
        if (!pssParams.getMaskGenAlgorithm().getAlgorithm().equals((ASN1Primitive)PKCSObjectIdentifiers.id_mgf1)) {
            return true;
        }
        if (!pssParams.getHashAlgorithm().equals((Object)AlgorithmIdentifier.getInstance((Object)pssParams.getMaskGenAlgorithm().getParameters()))) {
            return true;
        }
        MessageDigest digest = this.createDigest(pssParams.getHashAlgorithm());
        return pssParams.getSaltLength().intValue() != digest.getDigestLength();
    }

    static {
        oids.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        oids.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        oids.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        oids.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        oids.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        oids.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        oids.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        oids.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
        asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes128_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes192_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes256_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia128_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia192_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia256_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf((int)192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf((int)128));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf((int)192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf((int)256));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf((int)128));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf((int)192));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf((int)256));
        symmetricWrapperKeySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf((int)128));
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf((int)192));
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.aes, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
    }

    private static class OpCertificateException
    extends CertificateException {
        private Throwable cause;

        public OpCertificateException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

