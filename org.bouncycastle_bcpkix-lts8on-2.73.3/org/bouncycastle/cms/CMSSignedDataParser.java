/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Generator
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetStringParser
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1SetParser
 *  org.bouncycastle.asn1.ASN1StreamParser
 *  org.bouncycastle.asn1.BERSequenceGenerator
 *  org.bouncycastle.asn1.BERSetParser
 *  org.bouncycastle.asn1.BERTaggedObject
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfoParser
 *  org.bouncycastle.asn1.cms.SignedDataParser
 *  org.bouncycastle.asn1.cms.SignerInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Store
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Generator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERSetParser;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.SignedDataParser;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSContentInfoParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedHelper;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.PKCS7TypedStream;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.io.Streams;

public class CMSSignedDataParser
extends CMSContentInfoParser {
    private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
    private static final DefaultDigestAlgorithmIdentifierFinder dgstAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
    private SignedDataParser _signedData;
    private ASN1ObjectIdentifier _signedContentType;
    private CMSTypedStream _signedContent;
    private Map digests;
    private Set<AlgorithmIdentifier> digestAlgorithms;
    private SignerInformationStore _signerInfoStore;
    private ASN1Set _certSet;
    private ASN1Set _crlSet;
    private boolean _isCertCrlParsed;

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, byte[] sigBlock) throws CMSException {
        this(digestCalculatorProvider, new ByteArrayInputStream(sigBlock));
    }

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, CMSTypedStream signedContent, byte[] sigBlock) throws CMSException {
        this(digestCalculatorProvider, signedContent, new ByteArrayInputStream(sigBlock));
    }

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, InputStream sigData) throws CMSException {
        this(digestCalculatorProvider, null, sigData);
    }

    public CMSSignedDataParser(DigestCalculatorProvider digestCalculatorProvider, CMSTypedStream signedContent, InputStream sigData) throws CMSException {
        super(sigData);
        try {
            ASN1Encodable o;
            this._signedContent = signedContent;
            this._signedData = SignedDataParser.getInstance((Object)this._contentInfo.getContent(16));
            this.digests = new HashMap();
            ASN1SetParser digAlgs = this._signedData.getDigestAlgorithms();
            HashSet<AlgorithmIdentifier> algSet = new HashSet<AlgorithmIdentifier>();
            while ((o = digAlgs.readObject()) != null) {
                AlgorithmIdentifier algId = AlgorithmIdentifier.getInstance((Object)o);
                algSet.add(algId);
                try {
                    DigestCalculator calculator = digestCalculatorProvider.get(algId);
                    if (calculator == null) continue;
                    this.digests.put(algId.getAlgorithm(), calculator);
                }
                catch (OperatorCreationException calculator) {}
            }
            this.digestAlgorithms = Collections.unmodifiableSet(algSet);
            ContentInfoParser cont = this._signedData.getEncapContentInfo();
            ASN1Encodable contentParser = cont.getContent(4);
            if (contentParser instanceof ASN1OctetStringParser) {
                ASN1OctetStringParser octs = (ASN1OctetStringParser)contentParser;
                CMSTypedStream ctStr = new CMSTypedStream(cont.getContentType(), octs.getOctetStream());
                if (this._signedContent == null) {
                    this._signedContent = ctStr;
                } else {
                    ctStr.drain();
                }
            } else if (contentParser != null) {
                PKCS7TypedStream pkcs7Stream = new PKCS7TypedStream(cont.getContentType(), contentParser);
                if (this._signedContent == null) {
                    this._signedContent = pkcs7Stream;
                } else {
                    pkcs7Stream.drain();
                }
            }
            this._signedContentType = signedContent == null ? cont.getContentType() : this._signedContent.getContentType();
        }
        catch (IOException e) {
            throw new CMSException("io exception: " + e.getMessage(), e);
        }
    }

    public int getVersion() {
        return this._signedData.getVersion().intValueExact();
    }

    public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
        return this.digestAlgorithms;
    }

    public SignerInformationStore getSignerInfos() throws CMSException {
        if (this._signerInfoStore == null) {
            this.populateCertCrlSets();
            ArrayList<SignerInformation> signerInfos = new ArrayList<SignerInformation>();
            HashMap hashes = new HashMap();
            for (Object digestKey : this.digests.keySet()) {
                hashes.put(digestKey, ((DigestCalculator)this.digests.get(digestKey)).getDigest());
            }
            try {
                ASN1Encodable o;
                ASN1SetParser s = this._signedData.getSignerInfos();
                while ((o = s.readObject()) != null) {
                    SignerInfo info = SignerInfo.getInstance((Object)o.toASN1Primitive());
                    byte[] hash = (byte[])hashes.get(info.getDigestAlgorithm().getAlgorithm());
                    signerInfos.add(new SignerInformation(info, this._signedContentType, null, hash));
                }
            }
            catch (IOException e) {
                throw new CMSException("io exception: " + e.getMessage(), e);
            }
            this._signerInfoStore = new SignerInformationStore(signerInfos);
        }
        return this._signerInfoStore;
    }

    public Store getCertificates() throws CMSException {
        this.populateCertCrlSets();
        return HELPER.getCertificates(this._certSet);
    }

    public Store getCRLs() throws CMSException {
        this.populateCertCrlSets();
        return HELPER.getCRLs(this._crlSet);
    }

    public Store getAttributeCertificates() throws CMSException {
        this.populateCertCrlSets();
        return HELPER.getAttributeCertificates(this._certSet);
    }

    public Store getOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat) throws CMSException {
        this.populateCertCrlSets();
        return HELPER.getOtherRevocationInfo(otherRevocationInfoFormat, this._crlSet);
    }

    private void populateCertCrlSets() throws CMSException {
        if (this._isCertCrlParsed) {
            return;
        }
        this._isCertCrlParsed = true;
        try {
            this._certSet = CMSSignedDataParser.getASN1Set(this._signedData.getCertificates());
            this._crlSet = CMSSignedDataParser.getASN1Set(this._signedData.getCrls());
        }
        catch (IOException e) {
            throw new CMSException("problem parsing cert/crl sets", e);
        }
    }

    public String getSignedContentTypeOID() {
        return this._signedContentType.getId();
    }

    public CMSTypedStream getSignedContent() {
        if (this._signedContent == null) {
            return null;
        }
        InputStream digStream = CMSUtils.attachDigestsToInputStream(this.digests.values(), this._signedContent.getContentStream());
        return new CMSTypedStream(this._signedContent.getContentType(), digStream);
    }

    public static OutputStream replaceSigners(InputStream original, SignerInformationStore signerInformationStore, OutputStream out) throws CMSException, IOException {
        ASN1StreamParser in = new ASN1StreamParser(original);
        ContentInfoParser contentInfo = new ContentInfoParser((ASN1SequenceParser)in.readObject());
        SignedDataParser signedData = SignedDataParser.getInstance((Object)contentInfo.getContent(16));
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject((ASN1Primitive)CMSObjectIdentifiers.signedData);
        BERSequenceGenerator sigGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        sigGen.addObject((ASN1Primitive)signedData.getVersion());
        signedData.getDigestAlgorithms().toASN1Primitive();
        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        for (SignerInformation signer : signerInformationStore.getSigners()) {
            digestAlgs.add((ASN1Encodable)HELPER.fixDigestAlgID(signer.getDigestAlgorithmID(), dgstAlgFinder));
        }
        sigGen.getRawOutputStream().write(new DERSet(digestAlgs).getEncoded());
        ContentInfoParser encapContentInfo = signedData.getEncapContentInfo();
        BERSequenceGenerator eiGen = new BERSequenceGenerator(sigGen.getRawOutputStream());
        eiGen.addObject((ASN1Primitive)encapContentInfo.getContentType());
        CMSSignedDataParser.pipeEncapsulatedOctetString(encapContentInfo, eiGen.getRawOutputStream());
        eiGen.close();
        CMSSignedDataParser.writeSetToGeneratorTagged((ASN1Generator)sigGen, signedData.getCertificates(), 0);
        CMSSignedDataParser.writeSetToGeneratorTagged((ASN1Generator)sigGen, signedData.getCrls(), 1);
        ASN1EncodableVector signerInfos = new ASN1EncodableVector();
        for (SignerInformation signer : signerInformationStore.getSigners()) {
            signerInfos.add((ASN1Encodable)signer.toASN1Structure());
        }
        sigGen.getRawOutputStream().write(new DERSet(signerInfos).getEncoded());
        sigGen.close();
        sGen.close();
        return out;
    }

    public static OutputStream replaceCertificatesAndCRLs(InputStream original, Store certs, Store crls, Store attrCerts, OutputStream out) throws CMSException, IOException {
        ASN1Set asn1Crls;
        ASN1StreamParser in = new ASN1StreamParser(original);
        ContentInfoParser contentInfo = new ContentInfoParser((ASN1SequenceParser)in.readObject());
        SignedDataParser signedData = SignedDataParser.getInstance((Object)contentInfo.getContent(16));
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject((ASN1Primitive)CMSObjectIdentifiers.signedData);
        BERSequenceGenerator sigGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        sigGen.addObject((ASN1Primitive)signedData.getVersion());
        sigGen.getRawOutputStream().write(signedData.getDigestAlgorithms().toASN1Primitive().getEncoded());
        ContentInfoParser encapContentInfo = signedData.getEncapContentInfo();
        BERSequenceGenerator eiGen = new BERSequenceGenerator(sigGen.getRawOutputStream());
        eiGen.addObject((ASN1Primitive)encapContentInfo.getContentType());
        CMSSignedDataParser.pipeEncapsulatedOctetString(encapContentInfo, eiGen.getRawOutputStream());
        eiGen.close();
        CMSSignedDataParser.getASN1Set(signedData.getCertificates());
        CMSSignedDataParser.getASN1Set(signedData.getCrls());
        if (certs != null || attrCerts != null) {
            ASN1Set asn1Certs;
            ArrayList certificates = new ArrayList();
            if (certs != null) {
                certificates.addAll(CMSUtils.getCertificatesFromStore(certs));
            }
            if (attrCerts != null) {
                certificates.addAll(CMSUtils.getAttributeCertificatesFromStore(attrCerts));
            }
            if ((asn1Certs = CMSUtils.createBerSetFromList(certificates)).size() > 0) {
                sigGen.getRawOutputStream().write(new DERTaggedObject(false, 0, (ASN1Encodable)asn1Certs).getEncoded());
            }
        }
        if (crls != null && (asn1Crls = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(crls))).size() > 0) {
            sigGen.getRawOutputStream().write(new DERTaggedObject(false, 1, (ASN1Encodable)asn1Crls).getEncoded());
        }
        sigGen.getRawOutputStream().write(signedData.getSignerInfos().toASN1Primitive().getEncoded());
        sigGen.close();
        sGen.close();
        return out;
    }

    private static void writeSetToGeneratorTagged(ASN1Generator asn1Gen, ASN1SetParser asn1SetParser, int tagNo) throws IOException {
        ASN1Set asn1Set = CMSSignedDataParser.getASN1Set(asn1SetParser);
        if (asn1Set != null) {
            if (asn1SetParser instanceof BERSetParser) {
                asn1Gen.getRawOutputStream().write(new BERTaggedObject(false, tagNo, (ASN1Encodable)asn1Set).getEncoded());
            } else {
                asn1Gen.getRawOutputStream().write(new DERTaggedObject(false, tagNo, (ASN1Encodable)asn1Set).getEncoded());
            }
        }
    }

    private static ASN1Set getASN1Set(ASN1SetParser asn1SetParser) {
        return asn1SetParser == null ? null : ASN1Set.getInstance((Object)asn1SetParser.toASN1Primitive());
    }

    private static void pipeEncapsulatedOctetString(ContentInfoParser encapContentInfo, OutputStream rawOutputStream) throws IOException {
        ASN1OctetStringParser octs = (ASN1OctetStringParser)encapContentInfo.getContent(4);
        if (octs != null) {
            CMSSignedDataParser.pipeOctetString(octs, rawOutputStream);
        }
    }

    private static void pipeOctetString(ASN1OctetStringParser octs, OutputStream output) throws IOException {
        OutputStream outOctets = CMSUtils.createBEROctetOutputStream(output, 0, true, 0);
        Streams.pipeAll((InputStream)octs.getOctetStream(), (OutputStream)outOctets);
        outOctets.close();
    }
}

