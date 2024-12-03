/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequenceGenerator
 *  org.bouncycastle.asn1.BERTaggedObject
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.SignerInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.cms.CMSSignedHelper;
import org.bouncycastle.cms.CMSStreamException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CMSSignedDataStreamGenerator
extends CMSSignedGenerator {
    private int _bufferSize;

    public CMSSignedDataStreamGenerator() {
    }

    public CMSSignedDataStreamGenerator(DigestAlgorithmIdentifierFinder digestAlgIdFinder) {
        super(digestAlgIdFinder);
    }

    public void setBufferSize(int bufferSize) {
        this._bufferSize = bufferSize;
    }

    public OutputStream open(OutputStream out) throws IOException {
        return this.open(out, false);
    }

    public OutputStream open(OutputStream out, boolean encapsulate) throws IOException {
        return this.open(CMSObjectIdentifiers.data, out, encapsulate);
    }

    public OutputStream open(OutputStream out, boolean encapsulate, OutputStream dataOutputStream) throws IOException {
        return this.open(CMSObjectIdentifiers.data, out, encapsulate, dataOutputStream);
    }

    public OutputStream open(ASN1ObjectIdentifier eContentType, OutputStream out, boolean encapsulate) throws IOException {
        return this.open(eContentType, out, encapsulate, null);
    }

    public OutputStream open(ASN1ObjectIdentifier eContentType, OutputStream out, boolean encapsulate, OutputStream dataOutputStream) throws IOException {
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject((ASN1Primitive)CMSObjectIdentifiers.signedData);
        BERSequenceGenerator sigGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        sigGen.addObject((ASN1Primitive)this.calculateVersion(eContentType));
        HashSet<AlgorithmIdentifier> digestAlgs = new HashSet<AlgorithmIdentifier>();
        for (SignerInformation signer : this._signers) {
            CMSUtils.addDigestAlgs(digestAlgs, signer, this.digestAlgIdFinder);
        }
        for (SignerInfoGenerator signerGen : this.signerGens) {
            digestAlgs.add(signerGen.getDigestAlgorithm());
        }
        sigGen.getRawOutputStream().write(CMSUtils.convertToDlSet(digestAlgs).getEncoded());
        BERSequenceGenerator eiGen = new BERSequenceGenerator(sigGen.getRawOutputStream());
        eiGen.addObject((ASN1Primitive)eContentType);
        OutputStream encapStream = encapsulate ? CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, true, this._bufferSize) : null;
        OutputStream contentStream = CMSUtils.getSafeTeeOutputStream(dataOutputStream, encapStream);
        OutputStream sigStream = CMSUtils.attachSignersToOutputStream(this.signerGens, contentStream);
        return new CmsSignedDataOutputStream(sigStream, eContentType, sGen, sigGen, eiGen);
    }

    public List<AlgorithmIdentifier> getDigestAlgorithms() {
        ArrayList<AlgorithmIdentifier> digestAlorithms = new ArrayList<AlgorithmIdentifier>();
        for (SignerInformation signer : this._signers) {
            AlgorithmIdentifier digAlg = CMSSignedHelper.INSTANCE.fixDigestAlgID(signer.getDigestAlgorithmID(), this.digestAlgIdFinder);
            digestAlorithms.add(digAlg);
        }
        for (SignerInfoGenerator signerGen : this.signerGens) {
            digestAlorithms.add(signerGen.getDigestAlgorithm());
        }
        return digestAlorithms;
    }

    private ASN1Integer calculateVersion(ASN1ObjectIdentifier contentOid) {
        boolean otherCert = false;
        boolean otherCrl = false;
        boolean attrCertV1Found = false;
        boolean attrCertV2Found = false;
        if (this.certs != null) {
            for (Object obj : this.certs) {
                if (!(obj instanceof ASN1TaggedObject)) continue;
                ASN1TaggedObject tagged = (ASN1TaggedObject)obj;
                if (tagged.getTagNo() == 1) {
                    attrCertV1Found = true;
                    continue;
                }
                if (tagged.getTagNo() == 2) {
                    attrCertV2Found = true;
                    continue;
                }
                if (tagged.getTagNo() != 3) continue;
                otherCert = true;
            }
        }
        if (otherCert) {
            return new ASN1Integer(5L);
        }
        if (this.crls != null) {
            for (Object obj : this.crls) {
                if (!(obj instanceof ASN1TaggedObject)) continue;
                otherCrl = true;
            }
        }
        if (otherCrl) {
            return new ASN1Integer(5L);
        }
        if (attrCertV2Found) {
            return new ASN1Integer(4L);
        }
        if (attrCertV1Found) {
            return new ASN1Integer(3L);
        }
        if (this.checkForVersion3(this._signers, this.signerGens)) {
            return new ASN1Integer(3L);
        }
        if (!CMSObjectIdentifiers.data.equals((ASN1Primitive)contentOid)) {
            return new ASN1Integer(3L);
        }
        return new ASN1Integer(1L);
    }

    private boolean checkForVersion3(List signerInfos, List signerInfoGens) {
        Iterator it = signerInfos.iterator();
        while (it.hasNext()) {
            Object s = SignerInfo.getInstance((Object)((SignerInformation)it.next()).toASN1Structure());
            if (s.getVersion().intValueExact() != 3) continue;
            return true;
        }
        for (Object s : signerInfoGens) {
            if (((SignerInfoGenerator)s).getGeneratedVersion() != 3) continue;
            return true;
        }
        return false;
    }

    private class CmsSignedDataOutputStream
    extends OutputStream {
        private OutputStream _out;
        private ASN1ObjectIdentifier _contentOID;
        private BERSequenceGenerator _sGen;
        private BERSequenceGenerator _sigGen;
        private BERSequenceGenerator _eiGen;

        public CmsSignedDataOutputStream(OutputStream out, ASN1ObjectIdentifier contentOID, BERSequenceGenerator sGen, BERSequenceGenerator sigGen, BERSequenceGenerator eiGen) {
            this._out = out;
            this._contentOID = contentOID;
            this._sGen = sGen;
            this._sigGen = sigGen;
            this._eiGen = eiGen;
        }

        @Override
        public void write(int b) throws IOException {
            this._out.write(b);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            this._out.write(bytes, off, len);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this._out.write(bytes);
        }

        @Override
        public void close() throws IOException {
            this._out.close();
            this._eiGen.close();
            CMSSignedDataStreamGenerator.this.digests.clear();
            if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
                ASN1Set certSet = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.certs);
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 0, (ASN1Encodable)certSet).getEncoded());
            }
            if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
                ASN1Set crlSet = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.crls);
                this._sigGen.getRawOutputStream().write(new BERTaggedObject(false, 1, (ASN1Encodable)crlSet).getEncoded());
            }
            ASN1EncodableVector signerInfos = new ASN1EncodableVector();
            for (SignerInfoGenerator sigGen : CMSSignedDataStreamGenerator.this.signerGens) {
                try {
                    signerInfos.add((ASN1Encodable)sigGen.generate(this._contentOID));
                    byte[] calculatedDigest = sigGen.getCalculatedDigest();
                    CMSSignedDataStreamGenerator.this.digests.put(sigGen.getDigestAlgorithm().getAlgorithm().getId(), calculatedDigest);
                }
                catch (CMSException e) {
                    throw new CMSStreamException("exception generating signers: " + e.getMessage(), e);
                }
            }
            for (SignerInformation signer : CMSSignedDataStreamGenerator.this._signers) {
                signerInfos.add((ASN1Encodable)signer.toASN1Structure());
            }
            this._sigGen.getRawOutputStream().write(new DERSet(signerInfos).getEncoded());
            this._sigGen.close();
            this._sGen.close();
        }
    }
}

