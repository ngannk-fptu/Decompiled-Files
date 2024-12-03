/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BEROctetStringGenerator
 *  org.bouncycastle.asn1.BERSet
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.DLSet
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.OtherRevocationInfoFormat
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.ocsp.OCSPResponse
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.io.Streams
 *  org.bouncycastle.util.io.TeeInputStream
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetStringGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedHelper;
import org.bouncycastle.cms.NullOutputStream;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

class CMSUtils {
    private static final Set<String> des = new HashSet<String>();
    private static final Set mqvAlgs = new HashSet();
    private static final Set ecAlgs = new HashSet();
    private static final Set gostAlgs = new HashSet();

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

    static boolean isDES(String algorithmID) {
        String name = Strings.toUpperCase((String)algorithmID);
        return des.contains(name);
    }

    static boolean isEquivalent(AlgorithmIdentifier algId1, AlgorithmIdentifier algId2) {
        if (algId1 == null || algId2 == null) {
            return false;
        }
        if (!algId1.getAlgorithm().equals((ASN1Primitive)algId2.getAlgorithm())) {
            return false;
        }
        ASN1Encodable params1 = algId1.getParameters();
        ASN1Encodable params2 = algId2.getParameters();
        if (params1 != null) {
            return params1.equals(params2) || params1.equals(DERNull.INSTANCE) && params2 == null;
        }
        return params2 == null || params2.equals(DERNull.INSTANCE);
    }

    static ContentInfo readContentInfo(byte[] input) throws CMSException {
        return CMSUtils.readContentInfo(new ASN1InputStream(input));
    }

    static ContentInfo readContentInfo(InputStream input) throws CMSException {
        return CMSUtils.readContentInfo(new ASN1InputStream(input));
    }

    static ASN1Set convertToDlSet(Set<AlgorithmIdentifier> digestAlgs) {
        return new DLSet((ASN1Encodable[])digestAlgs.toArray(new AlgorithmIdentifier[digestAlgs.size()]));
    }

    static void addDigestAlgs(Set<AlgorithmIdentifier> digestAlgs, SignerInformation signer, DigestAlgorithmIdentifierFinder dgstAlgFinder) {
        digestAlgs.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(signer.getDigestAlgorithmID(), dgstAlgFinder));
        SignerInformationStore counterSignaturesStore = signer.getCounterSignatures();
        Iterator<SignerInformation> counterSignatureIt = counterSignaturesStore.iterator();
        while (counterSignatureIt.hasNext()) {
            SignerInformation counterSigner = counterSignatureIt.next();
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(counterSigner.getDigestAlgorithmID(), dgstAlgFinder));
        }
    }

    static List getCertificatesFromStore(Store certStore) throws CMSException {
        ArrayList<Certificate> certs = new ArrayList<Certificate>();
        try {
            for (X509CertificateHolder c : certStore.getMatches(null)) {
                certs.add(c.toASN1Structure());
            }
            return certs;
        }
        catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static List getAttributeCertificatesFromStore(Store attrStore) throws CMSException {
        ArrayList<DERTaggedObject> certs = new ArrayList<DERTaggedObject>();
        try {
            for (X509AttributeCertificateHolder attrCert : attrStore.getMatches(null)) {
                certs.add(new DERTaggedObject(false, 2, (ASN1Encodable)attrCert.toASN1Structure()));
            }
            return certs;
        }
        catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static List getCRLsFromStore(Store crlStore) throws CMSException {
        ArrayList<Object> crls = new ArrayList<Object>();
        try {
            for (Object rev : crlStore.getMatches(null)) {
                if (rev instanceof X509CRLHolder) {
                    X509CRLHolder c = (X509CRLHolder)rev;
                    crls.add(c.toASN1Structure());
                    continue;
                }
                if (rev instanceof OtherRevocationInfoFormat) {
                    OtherRevocationInfoFormat infoFormat = OtherRevocationInfoFormat.getInstance(rev);
                    CMSUtils.validateInfoFormat(infoFormat);
                    crls.add(new DERTaggedObject(false, 1, (ASN1Encodable)infoFormat));
                    continue;
                }
                if (!(rev instanceof ASN1TaggedObject)) continue;
                crls.add(rev);
            }
            return crls;
        }
        catch (ClassCastException e) {
            throw new CMSException("error processing certs", e);
        }
    }

    static void validateInfoFormat(OtherRevocationInfoFormat infoFormat) {
        OCSPResponse resp;
        if (CMSObjectIdentifiers.id_ri_ocsp_response.equals((ASN1Primitive)infoFormat.getInfoFormat()) && 0 != (resp = OCSPResponse.getInstance((Object)infoFormat.getInfo())).getResponseStatus().getIntValue()) {
            throw new IllegalArgumentException("cannot add unsuccessful OCSP response to CMS SignedData");
        }
    }

    static Collection getOthersFromStore(ASN1ObjectIdentifier otherRevocationInfoFormat, Store otherRevocationInfos) {
        ArrayList<DERTaggedObject> others = new ArrayList<DERTaggedObject>();
        for (ASN1Encodable info : otherRevocationInfos.getMatches(null)) {
            OtherRevocationInfoFormat infoFormat = new OtherRevocationInfoFormat(otherRevocationInfoFormat, info);
            CMSUtils.validateInfoFormat(infoFormat);
            others.add(new DERTaggedObject(false, 1, (ASN1Encodable)infoFormat));
        }
        return others;
    }

    static ASN1Set createBerSetFromList(List derObjects) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        Iterator it = derObjects.iterator();
        while (it.hasNext()) {
            v.add((ASN1Encodable)it.next());
        }
        return new BERSet(v);
    }

    static ASN1Set createDlSetFromList(List derObjects) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        Iterator it = derObjects.iterator();
        while (it.hasNext()) {
            v.add((ASN1Encodable)it.next());
        }
        return new DLSet(v);
    }

    static ASN1Set createDerSetFromList(List derObjects) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        Iterator it = derObjects.iterator();
        while (it.hasNext()) {
            v.add((ASN1Encodable)it.next());
        }
        return new DERSet(v);
    }

    static OutputStream createBEROctetOutputStream(OutputStream s, int tagNo, boolean isExplicit, int bufferSize) throws IOException {
        BEROctetStringGenerator octGen = new BEROctetStringGenerator(s, tagNo, isExplicit);
        if (bufferSize != 0) {
            return octGen.getOctetOutputStream(new byte[bufferSize]);
        }
        return octGen.getOctetOutputStream();
    }

    private static ContentInfo readContentInfo(ASN1InputStream in) throws CMSException {
        try {
            ContentInfo info = ContentInfo.getInstance((Object)in.readObject());
            if (info == null) {
                throw new CMSException("No content found.");
            }
            return info;
        }
        catch (IOException e) {
            throw new CMSException("IOException reading content.", e);
        }
        catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException("Malformed content.", e);
        }
    }

    public static byte[] streamToByteArray(InputStream in) throws IOException {
        return Streams.readAll((InputStream)in);
    }

    public static byte[] streamToByteArray(InputStream in, int limit) throws IOException {
        return Streams.readAllLimited((InputStream)in, (int)limit);
    }

    static InputStream attachDigestsToInputStream(Collection digests, InputStream s) {
        InputStream result = s;
        for (DigestCalculator digest : digests) {
            result = new TeeInputStream(result, digest.getOutputStream());
        }
        return result;
    }

    static OutputStream attachSignersToOutputStream(Collection signers, OutputStream s) {
        OutputStream result = s;
        for (SignerInfoGenerator signerGen : signers) {
            result = CMSUtils.getSafeTeeOutputStream(result, signerGen.getCalculatingOutputStream());
        }
        return result;
    }

    static OutputStream getSafeOutputStream(OutputStream s) {
        return s == null ? new NullOutputStream() : s;
    }

    static OutputStream getSafeTeeOutputStream(OutputStream s1, OutputStream s2) {
        return s1 == null ? CMSUtils.getSafeOutputStream(s2) : (s2 == null ? CMSUtils.getSafeOutputStream(s1) : new TeeOutputStream(s1, s2));
    }

    static {
        des.add("DES");
        des.add("DESEDE");
        des.add(OIWObjectIdentifiers.desCBC.getId());
        des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
        des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
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
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
    }
}

