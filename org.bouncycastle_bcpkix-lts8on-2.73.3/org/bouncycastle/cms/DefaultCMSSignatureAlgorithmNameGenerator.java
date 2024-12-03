/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.bc.BCObjectIdentifiers
 *  org.bouncycastle.asn1.bsi.BSIObjectIdentifiers
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.eac.EACObjectIdentifiers
 *  org.bouncycastle.asn1.edec.EdECObjectIdentifiers
 *  org.bouncycastle.asn1.gm.GMObjectIdentifiers
 *  org.bouncycastle.asn1.misc.MiscObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.X509ObjectIdentifiers
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 */
package org.bouncycastle.cms;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;

public class DefaultCMSSignatureAlgorithmNameGenerator
implements CMSSignatureAlgorithmNameGenerator {
    private final Map encryptionAlgs = new HashMap();
    private final Map digestAlgs = new HashMap();
    private final Map simpleAlgs = new HashMap();

    private void addEntries(ASN1ObjectIdentifier alias, String digest, String encryption) {
        this.digestAlgs.put(alias, digest);
        this.encryptionAlgs.put(alias, encryption);
    }

    public DefaultCMSSignatureAlgorithmNameGenerator() {
        this.addEntries(NISTObjectIdentifiers.dsa_with_sha224, "SHA224", "DSA");
        this.addEntries(NISTObjectIdentifiers.dsa_with_sha256, "SHA256", "DSA");
        this.addEntries(NISTObjectIdentifiers.dsa_with_sha384, "SHA384", "DSA");
        this.addEntries(NISTObjectIdentifiers.dsa_with_sha512, "SHA512", "DSA");
        this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_224, "SHA3-224", "DSA");
        this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_256, "SHA3-256", "DSA");
        this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_384, "SHA3-384", "DSA");
        this.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_512, "SHA3-512", "DSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, "SHA3-224", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, "SHA3-256", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, "SHA3-384", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, "SHA3-512", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, "SHA3-224", "ECDSA");
        this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, "SHA3-256", "ECDSA");
        this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, "SHA3-384", "ECDSA");
        this.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, "SHA3-512", "ECDSA");
        this.addEntries(OIWObjectIdentifiers.dsaWithSHA1, "SHA1", "DSA");
        this.addEntries(OIWObjectIdentifiers.md4WithRSA, "MD4", "RSA");
        this.addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        this.addEntries(OIWObjectIdentifiers.md5WithRSA, "MD5", "RSA");
        this.addEntries(OIWObjectIdentifiers.sha1WithRSA, "SHA1", "RSA");
        this.addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, "MD2", "RSA");
        this.addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        this.addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, "MD5", "RSA");
        this.addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1", "RSA");
        this.addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224", "RSA");
        this.addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256", "RSA");
        this.addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384", "RSA");
        this.addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512", "RSA");
        this.addEntries(PKCSObjectIdentifiers.sha512_224WithRSAEncryption, "SHA512(224)", "RSA");
        this.addEntries(PKCSObjectIdentifiers.sha512_256WithRSAEncryption, "SHA512(256)", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, "SHA3-224", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, "SHA3-256", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, "SHA3-384", "RSA");
        this.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, "SHA3-512", "RSA");
        this.addEntries(CMSObjectIdentifiers.id_RSASSA_PSS_SHAKE128, "SHAKE128", "RSAPSS");
        this.addEntries(CMSObjectIdentifiers.id_RSASSA_PSS_SHAKE256, "SHAKE256", "RSAPSS");
        this.addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, "RIPEMD128", "RSA");
        this.addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, "RIPEMD160", "RSA");
        this.addEntries(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, "RIPEMD256", "RSA");
        this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1", "ECDSA");
        this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224", "ECDSA");
        this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256", "ECDSA");
        this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384", "ECDSA");
        this.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512", "ECDSA");
        this.addEntries(CMSObjectIdentifiers.id_ecdsa_with_shake128, "SHAKE128", "ECDSA");
        this.addEntries(CMSObjectIdentifiers.id_ecdsa_with_shake256, "SHAKE256", "ECDSA");
        this.addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1", "DSA");
        this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1", "ECDSA");
        this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224", "ECDSA");
        this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256", "ECDSA");
        this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384", "ECDSA");
        this.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512", "ECDSA");
        this.addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "SHA1", "RSA");
        this.addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256", "RSA");
        this.addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1", "RSAandMGF1");
        this.addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256", "RSAandMGF1");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_224, "SHA3-224", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_256, "SHA3-256", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_384, "SHA3-384", "PLAIN-ECDSA");
        this.addEntries(BSIObjectIdentifiers.ecdsa_plain_SHA3_512, "SHA3-512", "PLAIN-ECDSA");
        this.addEntries(GMObjectIdentifiers.sm2sign_with_sha256, "SHA256", "SM2");
        this.addEntries(GMObjectIdentifiers.sm2sign_with_sm3, "SM3", "SM2");
        this.addEntries(BCObjectIdentifiers.sphincs256_with_SHA512, "SHA512", "SPHINCS256");
        this.addEntries(BCObjectIdentifiers.sphincs256_with_SHA3_512, "SHA3-512", "SPHINCS256");
        this.addEntries(BCObjectIdentifiers.picnic_with_shake256, "SHAKE256", "Picnic");
        this.addEntries(BCObjectIdentifiers.picnic_with_sha512, "SHA512", "Picnic");
        this.addEntries(BCObjectIdentifiers.picnic_with_sha3_512, "SHA3-512", "Picnic");
        this.encryptionAlgs.put(X9ObjectIdentifiers.id_dsa, "DSA");
        this.encryptionAlgs.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        this.encryptionAlgs.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
        this.encryptionAlgs.put(X509ObjectIdentifiers.id_ea_rsa, "RSA");
        this.encryptionAlgs.put(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAandMGF1");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.6.2"), "ECGOST3410");
        this.encryptionAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.1.5"), "GOST3410");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "ECGOST3410");
        this.encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
        this.encryptionAlgs.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
        this.digestAlgs.put(PKCSObjectIdentifiers.md2, "MD2");
        this.digestAlgs.put(PKCSObjectIdentifiers.md4, "MD4");
        this.digestAlgs.put(PKCSObjectIdentifiers.md5, "MD5");
        this.digestAlgs.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha512_224, "SHA512(224)");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha512_256, "SHA512(256)");
        this.digestAlgs.put(NISTObjectIdentifiers.id_shake128, "SHAKE128");
        this.digestAlgs.put(NISTObjectIdentifiers.id_shake256, "SHAKE256");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_224, "SHA3-224");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_256, "SHA3-256");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_384, "SHA3-384");
        this.digestAlgs.put(NISTObjectIdentifiers.id_sha3_512, "SHA3-512");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        this.digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        this.digestAlgs.put(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
        this.digestAlgs.put(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.2.1"), "GOST3411");
        this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, "GOST3411-2012-256");
        this.digestAlgs.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, "GOST3411-2012-512");
        this.digestAlgs.put(GMObjectIdentifiers.sm3, "SM3");
        this.simpleAlgs.put(EdECObjectIdentifiers.id_Ed25519, "Ed25519");
        this.simpleAlgs.put(EdECObjectIdentifiers.id_Ed448, "Ed448");
        this.simpleAlgs.put(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, "LMS");
        this.simpleAlgs.put(MiscObjectIdentifiers.id_alg_composite, "COMPOSITE");
        this.simpleAlgs.put(BCObjectIdentifiers.falcon_512, "Falcon-512");
        this.simpleAlgs.put(BCObjectIdentifiers.falcon_1024, "Falcon-1024");
        this.simpleAlgs.put(BCObjectIdentifiers.dilithium2, "Dilithium2");
        this.simpleAlgs.put(BCObjectIdentifiers.dilithium3, "Dilithium3");
        this.simpleAlgs.put(BCObjectIdentifiers.dilithium5, "Dilithium5");
        this.simpleAlgs.put(BCObjectIdentifiers.picnic_signature, "Picnic");
    }

    private String getDigestAlgName(ASN1ObjectIdentifier digestAlgOID) {
        String algName = (String)this.digestAlgs.get(digestAlgOID);
        if (algName != null) {
            return algName;
        }
        return digestAlgOID.getId();
    }

    private String getEncryptionAlgName(ASN1ObjectIdentifier encryptionAlgOID) {
        String algName = (String)this.encryptionAlgs.get(encryptionAlgOID);
        if (algName != null) {
            return algName;
        }
        return encryptionAlgOID.getId();
    }

    protected void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        this.encryptionAlgs.put(oid, algorithmName);
    }

    protected void setSigningDigestAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        this.digestAlgs.put(oid, algorithmName);
    }

    @Override
    public String getSignatureName(AlgorithmIdentifier digestAlg, AlgorithmIdentifier encryptionAlg) {
        ASN1ObjectIdentifier encryptionAlgOID = encryptionAlg.getAlgorithm();
        String simpleAlgName = (String)this.simpleAlgs.get(encryptionAlgOID);
        if (simpleAlgName != null) {
            return simpleAlgName;
        }
        if (encryptionAlgOID.on(BCObjectIdentifiers.sphincsPlus)) {
            return "SPHINCSPlus";
        }
        String digestName = this.getDigestAlgName(encryptionAlgOID);
        if (!digestName.equals(encryptionAlgOID.getId())) {
            return digestName + "with" + this.getEncryptionAlgName(encryptionAlgOID);
        }
        return this.getDigestAlgName(digestAlg.getAlgorithm()) + "with" + this.getEncryptionAlgName(encryptionAlgOID);
    }
}

