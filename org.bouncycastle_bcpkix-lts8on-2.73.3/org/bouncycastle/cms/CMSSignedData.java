/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.DLSet
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.SignedData
 *  org.bouncycastle.asn1.cms.SignerInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Encodable
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedHelper;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.PKCS7ProcessableObject;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.SignerInformationVerifierProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;

public class CMSSignedData
implements Encodable {
    private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
    private static final DefaultDigestAlgorithmIdentifierFinder DIGEST_ALG_ID_FINDER = new DefaultDigestAlgorithmIdentifierFinder();
    SignedData signedData;
    ContentInfo contentInfo;
    CMSTypedData signedContent;
    SignerInformationStore signerInfoStore;
    private Map hashes;

    private CMSSignedData(CMSSignedData c) {
        this.signedData = c.signedData;
        this.contentInfo = c.contentInfo;
        this.signedContent = c.signedContent;
        this.signerInfoStore = c.signerInfoStore;
    }

    public CMSSignedData(byte[] sigBlock) throws CMSException {
        this(CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(CMSProcessable signedContent, byte[] sigBlock) throws CMSException {
        this(signedContent, CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(Map hashes, byte[] sigBlock) throws CMSException {
        this(hashes, CMSUtils.readContentInfo(sigBlock));
    }

    public CMSSignedData(CMSProcessable signedContent, InputStream sigData) throws CMSException {
        this(signedContent, CMSUtils.readContentInfo((InputStream)new ASN1InputStream(sigData)));
    }

    public CMSSignedData(InputStream sigData) throws CMSException {
        this(CMSUtils.readContentInfo(sigData));
    }

    public CMSSignedData(final CMSProcessable signedContent, ContentInfo sigData) throws CMSException {
        this.signedContent = signedContent instanceof CMSTypedData ? (CMSTypedData)signedContent : new CMSTypedData(){

            @Override
            public ASN1ObjectIdentifier getContentType() {
                return CMSSignedData.this.signedData.getEncapContentInfo().getContentType();
            }

            @Override
            public void write(OutputStream out) throws IOException, CMSException {
                signedContent.write(out);
            }

            @Override
            public Object getContent() {
                return signedContent.getContent();
            }
        };
        this.contentInfo = sigData;
        this.signedData = this.getSignedData();
    }

    public CMSSignedData(Map hashes, ContentInfo sigData) throws CMSException {
        this.hashes = hashes;
        this.contentInfo = sigData;
        this.signedData = this.getSignedData();
    }

    public CMSSignedData(ContentInfo sigData) throws CMSException {
        this.contentInfo = sigData;
        this.signedData = this.getSignedData();
        ASN1Encodable content = this.signedData.getEncapContentInfo().getContent();
        this.signedContent = content != null ? (content instanceof ASN1OctetString ? new CMSProcessableByteArray(this.signedData.getEncapContentInfo().getContentType(), ((ASN1OctetString)content).getOctets()) : new PKCS7ProcessableObject(this.signedData.getEncapContentInfo().getContentType(), content)) : null;
    }

    private SignedData getSignedData() throws CMSException {
        try {
            return SignedData.getInstance((Object)this.contentInfo.getContent());
        }
        catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException("Malformed content.", e);
        }
    }

    public int getVersion() {
        return this.signedData.getVersion().intValueExact();
    }

    public SignerInformationStore getSignerInfos() {
        if (this.signerInfoStore == null) {
            ASN1Set s = this.signedData.getSignerInfos();
            ArrayList<SignerInformation> signerInfos = new ArrayList<SignerInformation>();
            for (int i = 0; i != s.size(); ++i) {
                SignerInfo info = SignerInfo.getInstance((Object)s.getObjectAt(i));
                ASN1ObjectIdentifier contentType = this.signedData.getEncapContentInfo().getContentType();
                if (this.hashes == null) {
                    signerInfos.add(new SignerInformation(info, contentType, this.signedContent, null));
                    continue;
                }
                Object obj = this.hashes.keySet().iterator().next();
                byte[] hash = obj instanceof String ? (byte[])this.hashes.get(info.getDigestAlgorithm().getAlgorithm().getId()) : (byte[])this.hashes.get(info.getDigestAlgorithm().getAlgorithm());
                signerInfos.add(new SignerInformation(info, contentType, null, hash));
            }
            this.signerInfoStore = new SignerInformationStore(signerInfos);
        }
        return this.signerInfoStore;
    }

    public boolean isDetachedSignature() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() > 0;
    }

    public boolean isCertificateManagementMessage() {
        return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() == 0;
    }

    public Store<X509CertificateHolder> getCertificates() {
        return HELPER.getCertificates(this.signedData.getCertificates());
    }

    public Store<X509CRLHolder> getCRLs() {
        return HELPER.getCRLs(this.signedData.getCRLs());
    }

    public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
        return HELPER.getAttributeCertificates(this.signedData.getCertificates());
    }

    public Store getOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat) {
        return HELPER.getOtherRevocationInfo(otherRevocationInfoFormat, this.signedData.getCRLs());
    }

    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        HashSet<AlgorithmIdentifier> digests = new HashSet<AlgorithmIdentifier>();
        Enumeration en = this.signedData.getDigestAlgorithms().getObjects();
        while (en.hasMoreElements()) {
            digests.add(AlgorithmIdentifier.getInstance(en.nextElement()));
        }
        return Collections.unmodifiableSet(digests);
    }

    public String getSignedContentTypeOID() {
        return this.signedData.getEncapContentInfo().getContentType().getId();
    }

    public CMSTypedData getSignedContent() {
        return this.signedContent;
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        return this.contentInfo.getEncoded(encoding);
    }

    public boolean verifySignatures(SignerInformationVerifierProvider verifierProvider) throws CMSException {
        return this.verifySignatures(verifierProvider, false);
    }

    public boolean verifySignatures(SignerInformationVerifierProvider verifierProvider, boolean ignoreCounterSignatures) throws CMSException {
        Collection<SignerInformation> signers = this.getSignerInfos().getSigners();
        for (SignerInformation signer : signers) {
            try {
                SignerInformationVerifier verifier = verifierProvider.get(signer.getSID());
                if (!signer.verify(verifier)) {
                    return false;
                }
                if (ignoreCounterSignatures) continue;
                Collection<SignerInformation> counterSigners = signer.getCounterSignatures().getSigners();
                Iterator<SignerInformation> cIt = counterSigners.iterator();
                while (cIt.hasNext()) {
                    if (this.verifyCounterSignature(cIt.next(), verifierProvider)) continue;
                    return false;
                }
            }
            catch (OperatorCreationException e) {
                throw new CMSException("failure in verifier provider: " + e.getMessage(), e);
            }
        }
        return true;
    }

    private boolean verifyCounterSignature(SignerInformation counterSigner, SignerInformationVerifierProvider verifierProvider) throws OperatorCreationException, CMSException {
        SignerInformationVerifier counterVerifier = verifierProvider.get(counterSigner.getSID());
        if (!counterSigner.verify(counterVerifier)) {
            return false;
        }
        Collection<SignerInformation> counterSigners = counterSigner.getCounterSignatures().getSigners();
        Iterator<SignerInformation> cIt = counterSigners.iterator();
        while (cIt.hasNext()) {
            if (this.verifyCounterSignature(cIt.next(), verifierProvider)) continue;
            return false;
        }
        return true;
    }

    public static CMSSignedData addDigestAlgorithm(CMSSignedData signedData, AlgorithmIdentifier digestAlgorithm) {
        return CMSSignedData.addDigestAlgorithm(signedData, digestAlgorithm, DIGEST_ALG_ID_FINDER);
    }

    public static CMSSignedData addDigestAlgorithm(CMSSignedData signedData, AlgorithmIdentifier digestAlgorithm, DigestAlgorithmIdentifierFinder digestAlgIdFinder) {
        AlgorithmIdentifier digestAlg;
        Set<AlgorithmIdentifier> digestAlgorithms = signedData.getDigestAlgorithmIDs();
        if (digestAlgorithms.contains(digestAlg = HELPER.fixDigestAlgID(digestAlgorithm, digestAlgIdFinder))) {
            return signedData;
        }
        CMSSignedData cms = new CMSSignedData(signedData);
        HashSet<AlgorithmIdentifier> digestAlgs = new HashSet<AlgorithmIdentifier>();
        Iterator<AlgorithmIdentifier> it = digestAlgorithms.iterator();
        while (it.hasNext()) {
            digestAlgs.add(HELPER.fixDigestAlgID(it.next(), digestAlgIdFinder));
        }
        digestAlgs.add(digestAlg);
        ASN1Set digestSet = CMSUtils.convertToDlSet(digestAlgs);
        ASN1Sequence sD = (ASN1Sequence)signedData.signedData.toASN1Primitive();
        ASN1EncodableVector vec = new ASN1EncodableVector(sD.size());
        vec.add(sD.getObjectAt(0));
        vec.add((ASN1Encodable)digestSet);
        for (int i = 2; i != sD.size(); ++i) {
            vec.add(sD.getObjectAt(i));
        }
        cms.signedData = SignedData.getInstance((Object)new BERSequence(vec));
        cms.contentInfo = new ContentInfo(cms.contentInfo.getContentType(), (ASN1Encodable)cms.signedData);
        return cms;
    }

    public static CMSSignedData replaceSigners(CMSSignedData signedData, SignerInformationStore signerInformationStore) {
        return CMSSignedData.replaceSigners(signedData, signerInformationStore, DIGEST_ALG_ID_FINDER);
    }

    public static CMSSignedData replaceSigners(CMSSignedData signedData, SignerInformationStore signerInformationStore, DigestAlgorithmIdentifierFinder digestAlgIdFinder) {
        CMSSignedData cms = new CMSSignedData(signedData);
        cms.signerInfoStore = signerInformationStore;
        HashSet<AlgorithmIdentifier> digestAlgs = new HashSet<AlgorithmIdentifier>();
        Collection<SignerInformation> signers = signerInformationStore.getSigners();
        ASN1EncodableVector vec = new ASN1EncodableVector(signers.size());
        for (SignerInformation signer : signers) {
            CMSUtils.addDigestAlgs(digestAlgs, signer, digestAlgIdFinder);
            vec.add((ASN1Encodable)signer.toASN1Structure());
        }
        ASN1Set digestSet = CMSUtils.convertToDlSet(digestAlgs);
        DLSet signerSet = new DLSet(vec);
        ASN1Sequence sD = (ASN1Sequence)signedData.signedData.toASN1Primitive();
        vec = new ASN1EncodableVector(sD.size());
        vec.add(sD.getObjectAt(0));
        vec.add((ASN1Encodable)digestSet);
        for (int i = 2; i != sD.size() - 1; ++i) {
            vec.add(sD.getObjectAt(i));
        }
        vec.add((ASN1Encodable)signerSet);
        cms.signedData = SignedData.getInstance((Object)new BERSequence(vec));
        cms.contentInfo = new ContentInfo(cms.contentInfo.getContentType(), (ASN1Encodable)cms.signedData);
        return cms;
    }

    public static CMSSignedData replaceCertificatesAndCRLs(CMSSignedData signedData, Store certificates, Store attrCerts, Store revocations) throws CMSException {
        ASN1Set set;
        CMSSignedData cms = new CMSSignedData(signedData);
        ASN1Set certSet = null;
        ASN1Set crlSet = null;
        if (certificates != null || attrCerts != null) {
            ASN1Set set2;
            ArrayList certs = new ArrayList();
            if (certificates != null) {
                certs.addAll(CMSUtils.getCertificatesFromStore(certificates));
            }
            if (attrCerts != null) {
                certs.addAll(CMSUtils.getAttributeCertificatesFromStore(attrCerts));
            }
            if ((set2 = CMSUtils.createBerSetFromList(certs)).size() != 0) {
                certSet = set2;
            }
        }
        if (revocations != null && (set = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(revocations))).size() != 0) {
            crlSet = set;
        }
        cms.signedData = new SignedData(signedData.signedData.getDigestAlgorithms(), signedData.signedData.getEncapContentInfo(), certSet, crlSet, signedData.signedData.getSignerInfos());
        cms.contentInfo = new ContentInfo(cms.contentInfo.getContentType(), (ASN1Encodable)cms.signedData);
        return cms;
    }
}

