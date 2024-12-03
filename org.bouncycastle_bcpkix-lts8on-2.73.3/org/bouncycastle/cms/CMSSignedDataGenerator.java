/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.BEROctetString
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.SignedData
 *  org.bouncycastle.asn1.cms.SignerInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CMSSignedDataGenerator
extends CMSSignedGenerator {
    private List signerInfs = new ArrayList();
    private boolean isDefiniteLength = false;

    public CMSSignedDataGenerator() {
    }

    public CMSSignedDataGenerator(DigestAlgorithmIdentifierFinder digestAlgIdFinder) {
        super(digestAlgIdFinder);
    }

    public void setDefiniteLengthEncoding(boolean isDefiniteLength) {
        this.isDefiniteLength = isDefiniteLength;
    }

    public CMSSignedData generate(CMSTypedData content) throws CMSException {
        return this.generate(content, false);
    }

    public CMSSignedData generate(CMSTypedData content, boolean encapsulate) throws CMSException {
        if (!this.signerInfs.isEmpty()) {
            throw new IllegalStateException("this method can only be used with SignerInfoGenerator");
        }
        LinkedHashSet<AlgorithmIdentifier> digestAlgs = new LinkedHashSet<AlgorithmIdentifier>();
        ASN1EncodableVector signerInfos = new ASN1EncodableVector();
        this.digests.clear();
        for (SignerInformation signer : this._signers) {
            CMSUtils.addDigestAlgs(digestAlgs, signer, this.digestAlgIdFinder);
            signerInfos.add((ASN1Encodable)signer.toASN1Structure());
        }
        ASN1ObjectIdentifier contentTypeOID = content.getContentType();
        Object octs = null;
        if (content.getContent() != null) {
            ByteArrayOutputStream bOut = null;
            if (encapsulate) {
                bOut = new ByteArrayOutputStream();
            }
            OutputStream cOut = CMSUtils.attachSignersToOutputStream(this.signerGens, bOut);
            cOut = CMSUtils.getSafeOutputStream(cOut);
            try {
                content.write(cOut);
                cOut.close();
            }
            catch (IOException e) {
                throw new CMSException("data processing exception: " + e.getMessage(), e);
            }
            if (encapsulate) {
                octs = this.isDefiniteLength ? new DEROctetString(bOut.toByteArray()) : new BEROctetString(bOut.toByteArray());
            }
        }
        for (SignerInfoGenerator sGen : this.signerGens) {
            SignerInfo inf = sGen.generate(contentTypeOID);
            digestAlgs.add(inf.getDigestAlgorithm());
            signerInfos.add((ASN1Encodable)inf);
            byte[] calcDigest = sGen.getCalculatedDigest();
            if (calcDigest == null) continue;
            this.digests.put(inf.getDigestAlgorithm().getAlgorithm().getId(), calcDigest);
        }
        ASN1Set certificates = null;
        if (this.certs.size() != 0) {
            certificates = this.isDefiniteLength ? CMSUtils.createDlSetFromList(this.certs) : CMSUtils.createBerSetFromList(this.certs);
        }
        ASN1Set certrevlist = null;
        if (this.crls.size() != 0) {
            certrevlist = this.isDefiniteLength ? CMSUtils.createDlSetFromList(this.crls) : CMSUtils.createBerSetFromList(this.crls);
        }
        ContentInfo encInfo = new ContentInfo(contentTypeOID, octs);
        SignedData sd = new SignedData(CMSUtils.convertToDlSet(digestAlgs), encInfo, certificates, certrevlist, (ASN1Set)new DERSet(signerInfos));
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.signedData, (ASN1Encodable)sd);
        return new CMSSignedData((CMSProcessable)content, contentInfo);
    }

    public SignerInformationStore generateCounterSigners(SignerInformation signer) throws CMSException {
        return this.generate(new CMSProcessableByteArray(null, signer.getSignature()), false).getSignerInfos();
    }
}

