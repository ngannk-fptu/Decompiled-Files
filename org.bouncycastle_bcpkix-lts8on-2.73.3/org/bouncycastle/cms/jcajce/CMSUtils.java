/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.jcajce.util.AlgorithmParametersUtils
 *  org.bouncycastle.jcajce.util.AnnotatedPrivateKey
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;

class CMSUtils {
    private static final Set mqvAlgs = new HashSet();
    private static final Set ecAlgs = new HashSet();
    private static final Set gostAlgs = new HashSet();
    private static final Map asymmetricWrapperAlgNames = new HashMap();
    private static Map<ASN1ObjectIdentifier, String> wrapAlgNames = new HashMap<ASN1ObjectIdentifier, String>();

    CMSUtils() {
    }

    static boolean isMQV(ASN1ObjectIdentifier algorithm) {
        return mqvAlgs.contains(algorithm);
    }

    static boolean isEC(ASN1ObjectIdentifier algorithm) {
        return ecAlgs.contains(algorithm);
    }

    static boolean isGOST(ASN1ObjectIdentifier algorithm) {
        return gostAlgs.contains(algorithm);
    }

    static boolean isRFC2631(ASN1ObjectIdentifier algorithm) {
        return algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.id_alg_ESDH) || algorithm.equals((ASN1Primitive)PKCSObjectIdentifiers.id_alg_SSDH);
    }

    static String getWrapAlgorithmName(ASN1ObjectIdentifier oid) {
        return wrapAlgNames.get(oid);
    }

    static PrivateKey cleanPrivateKey(PrivateKey key) {
        if (key instanceof AnnotatedPrivateKey) {
            return CMSUtils.cleanPrivateKey(((AnnotatedPrivateKey)key).getKey());
        }
        return key;
    }

    static IssuerAndSerialNumber getIssuerAndSerialNumber(X509Certificate cert) throws CertificateEncodingException {
        Certificate certStruct = Certificate.getInstance((Object)cert.getEncoded());
        return new IssuerAndSerialNumber(certStruct.getIssuer(), cert.getSerialNumber());
    }

    static byte[] getSubjectKeyId(X509Certificate cert) {
        byte[] ext = cert.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (ext != null) {
            return ASN1OctetString.getInstance((Object)ASN1OctetString.getInstance((Object)ext).getOctets()).getOctets();
        }
        return null;
    }

    static EnvelopedDataHelper createContentHelper(Provider provider) {
        if (provider != null) {
            return new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        }
        return new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }

    static EnvelopedDataHelper createContentHelper(String providerName) {
        if (providerName != null) {
            return new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        }
        return new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }

    static ASN1Encodable extractParameters(AlgorithmParameters params) throws CMSException {
        try {
            return AlgorithmParametersUtils.extractParameters((AlgorithmParameters)params);
        }
        catch (IOException e) {
            throw new CMSException("cannot extract parameters: " + e.getMessage(), e);
        }
    }

    static void loadParameters(AlgorithmParameters params, ASN1Encodable sParams) throws CMSException {
        try {
            AlgorithmParametersUtils.loadParameters((AlgorithmParameters)params, (ASN1Encodable)sParams);
        }
        catch (IOException e) {
            throw new CMSException("error encoding algorithm parameters.", e);
        }
    }

    static Key getJceKey(GenericKey key) {
        if (key.getRepresentation() instanceof Key) {
            return (Key)key.getRepresentation();
        }
        if (key.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[])key.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }

    static Cipher createAsymmetricWrapper(JcaJceHelper helper, ASN1ObjectIdentifier algorithm, Map extraAlgNames) throws OperatorCreationException {
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
                        return helper.createCipher(cipherName);
                    }
                    catch (NoSuchAlgorithmException e) {
                        if (!cipherName.equals("RSA/ECB/PKCS1Padding")) break block9;
                        try {
                            return helper.createCipher("RSA/NONE/PKCS1Padding");
                        }
                        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                            // empty catch block
                        }
                    }
                }
            }
            return helper.createCipher(algorithm.getId());
        }
        catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    public static int getKekSize(ASN1ObjectIdentifier symWrapAlg) {
        if (symWrapAlg.equals((ASN1Primitive)CMSAlgorithm.AES256_WRAP)) {
            return 32;
        }
        if (symWrapAlg.equals((ASN1Primitive)CMSAlgorithm.AES128_WRAP)) {
            return 16;
        }
        if (symWrapAlg.equals((ASN1Primitive)CMSAlgorithm.AES192_WRAP)) {
            return 24;
        }
        throw new IllegalArgumentException("unknown wrap algorithm");
    }

    static {
        wrapAlgNames.put(CMSAlgorithm.AES128_WRAP, "AESWRAP");
        wrapAlgNames.put(CMSAlgorithm.AES192_WRAP, "AESWRAP");
        wrapAlgNames.put(CMSAlgorithm.AES256_WRAP, "AESWRAP");
        mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
        asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        asymmetricWrapperAlgNames.put(ISOIECObjectIdentifiers.id_kem_rsa, "RSA-KTS-KEM-KWS");
    }
}

