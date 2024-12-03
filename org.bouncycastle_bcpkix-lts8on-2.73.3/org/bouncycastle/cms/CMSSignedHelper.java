/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.cms.OtherRevocationInfoFormat
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.eac.EACObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.AttributeCertificate
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.CertificateList
 *  org.bouncycastle.asn1.x509.X509ObjectIdentifiers
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.util.CollectionStore
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

class CMSSignedHelper {
    static final CMSSignedHelper INSTANCE = new CMSSignedHelper();
    private static final Map encryptionAlgs = new HashMap();

    CMSSignedHelper() {
    }

    private static void addEntries(ASN1ObjectIdentifier alias, String encryption) {
        encryptionAlgs.put(alias.getId(), encryption);
    }

    String getEncryptionAlgName(String encryptionAlgOID) {
        String algName = (String)encryptionAlgs.get(encryptionAlgOID);
        if (algName != null) {
            return algName;
        }
        return encryptionAlgOID;
    }

    AlgorithmIdentifier fixDigestAlgID(AlgorithmIdentifier algId, DigestAlgorithmIdentifierFinder dgstAlgFinder) {
        ASN1Encodable params = algId.getParameters();
        if (params == null || DERNull.INSTANCE.equals(params)) {
            return dgstAlgFinder.find(algId.getAlgorithm());
        }
        return algId;
    }

    void setSigningEncryptionAlgorithmMapping(ASN1ObjectIdentifier oid, String algorithmName) {
        CMSSignedHelper.addEntries(oid, algorithmName);
    }

    Store getCertificates(ASN1Set certSet) {
        if (certSet != null) {
            ArrayList<X509CertificateHolder> certList = new ArrayList<X509CertificateHolder>(certSet.size());
            Enumeration en = certSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable)en.nextElement()).toASN1Primitive();
                if (!(obj instanceof ASN1Sequence)) continue;
                certList.add(new X509CertificateHolder(Certificate.getInstance((Object)obj)));
            }
            return new CollectionStore(certList);
        }
        return new CollectionStore(new ArrayList());
    }

    Store getAttributeCertificates(ASN1Set certSet) {
        if (certSet != null) {
            ArrayList<X509AttributeCertificateHolder> certList = new ArrayList<X509AttributeCertificateHolder>(certSet.size());
            Enumeration en = certSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1TaggedObject tObj;
                ASN1Primitive obj = ((ASN1Encodable)en.nextElement()).toASN1Primitive();
                if (!(obj instanceof ASN1TaggedObject) || (tObj = (ASN1TaggedObject)obj).getTagNo() != 1 && tObj.getTagNo() != 2) continue;
                certList.add(new X509AttributeCertificateHolder(AttributeCertificate.getInstance((Object)tObj.getBaseUniversal(false, 16))));
            }
            return new CollectionStore(certList);
        }
        return new CollectionStore(new ArrayList());
    }

    Store getCRLs(ASN1Set crlSet) {
        if (crlSet != null) {
            ArrayList<X509CRLHolder> crlList = new ArrayList<X509CRLHolder>(crlSet.size());
            Enumeration en = crlSet.getObjects();
            while (en.hasMoreElements()) {
                ASN1Primitive obj = ((ASN1Encodable)en.nextElement()).toASN1Primitive();
                if (!(obj instanceof ASN1Sequence)) continue;
                crlList.add(new X509CRLHolder(CertificateList.getInstance((Object)obj)));
            }
            return new CollectionStore(crlList);
        }
        return new CollectionStore(new ArrayList());
    }

    Store getOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat, ASN1Set crlSet) {
        if (crlSet != null) {
            ArrayList<ASN1Encodable> crlList = new ArrayList<ASN1Encodable>(crlSet.size());
            Enumeration en = crlSet.getObjects();
            while (en.hasMoreElements()) {
                OtherRevocationInfoFormat other;
                ASN1TaggedObject tObj;
                ASN1Primitive obj = ((ASN1Encodable)en.nextElement()).toASN1Primitive();
                if (!(obj instanceof ASN1TaggedObject) || !(tObj = ASN1TaggedObject.getInstance((Object)obj)).hasContextTag(1) || !otherRevocationInfoFormat.equals((ASN1Primitive)(other = OtherRevocationInfoFormat.getInstance((ASN1TaggedObject)tObj, (boolean)false)).getInfoFormat())) continue;
                crlList.add(other.getInfo());
            }
            return new CollectionStore(crlList);
        }
        return new CollectionStore(new ArrayList());
    }

    static {
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.dsa_with_sha224, "DSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.dsa_with_sha256, "DSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.dsa_with_sha384, "DSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.dsa_with_sha512, "DSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_224, "DSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_256, "DSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_384, "DSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_dsa_with_sha3_512, "DSA");
        CMSSignedHelper.addEntries(OIWObjectIdentifiers.dsaWithSHA1, "DSA");
        CMSSignedHelper.addEntries(OIWObjectIdentifiers.md4WithRSA, "RSA");
        CMSSignedHelper.addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(OIWObjectIdentifiers.md5WithRSA, "RSA");
        CMSSignedHelper.addEntries(OIWObjectIdentifiers.sha1WithRSA, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "RSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, "RSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, "RSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, "RSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, "RSA");
        CMSSignedHelper.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, "ECDSA");
        CMSSignedHelper.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "ECDSA");
        CMSSignedHelper.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "ECDSA");
        CMSSignedHelper.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, "ECDSA");
        CMSSignedHelper.addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "ECDSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, "ECDSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, "ECDSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, "ECDSA");
        CMSSignedHelper.addEntries(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, "ECDSA");
        CMSSignedHelper.addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, "DSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "ECDSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "ECDSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "ECDSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "ECDSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "ECDSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "RSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "RSA");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "RSAandMGF1");
        CMSSignedHelper.addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "RSAandMGF1");
        CMSSignedHelper.addEntries(X9ObjectIdentifiers.id_dsa, "DSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        CMSSignedHelper.addEntries(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
        CMSSignedHelper.addEntries(X509ObjectIdentifiers.id_ea_rsa, "RSA");
        CMSSignedHelper.addEntries(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAandMGF1");
        CMSSignedHelper.addEntries(CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
        CMSSignedHelper.addEntries(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        CMSSignedHelper.addEntries(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.6.2"), "ECGOST3410");
        CMSSignedHelper.addEntries(new ASN1ObjectIdentifier("1.3.6.1.4.1.5849.1.1.5"), "GOST3410");
        CMSSignedHelper.addEntries(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
        CMSSignedHelper.addEntries(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
        CMSSignedHelper.addEntries(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "ECGOST3410");
        CMSSignedHelper.addEntries(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
        CMSSignedHelper.addEntries(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "ECGOST3410-2012-256");
        CMSSignedHelper.addEntries(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "ECGOST3410-2012-512");
    }
}

