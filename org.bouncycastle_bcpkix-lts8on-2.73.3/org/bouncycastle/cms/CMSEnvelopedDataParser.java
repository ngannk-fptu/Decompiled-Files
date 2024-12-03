/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1OctetStringParser
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1SetParser
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.EncryptedContentInfoParser
 *  org.bouncycastle.asn1.cms.EnvelopedDataParser
 *  org.bouncycastle.asn1.cms.OriginatorInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.EnvelopedDataParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSContentInfoParser;
import org.bouncycastle.cms.CMSEnvelopedHelper;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableInputStream;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;

public class CMSEnvelopedDataParser
extends CMSContentInfoParser {
    RecipientInformationStore recipientInfoStore;
    EnvelopedDataParser envelopedData = new EnvelopedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
    private AlgorithmIdentifier encAlg;
    private AttributeTable unprotectedAttributes;
    private boolean attrNotRead = true;
    private OriginatorInformation originatorInfo;

    public CMSEnvelopedDataParser(byte[] envelopedData) throws CMSException, IOException {
        this(new ByteArrayInputStream(envelopedData));
    }

    public CMSEnvelopedDataParser(InputStream envelopedData) throws CMSException, IOException {
        super(envelopedData);
        OriginatorInfo info = this.envelopedData.getOriginatorInfo();
        if (info != null) {
            this.originatorInfo = new OriginatorInformation(info);
        }
        ASN1Set recipientInfos = ASN1Set.getInstance((Object)this.envelopedData.getRecipientInfos().toASN1Primitive());
        EncryptedContentInfoParser encInfo = this.envelopedData.getEncryptedContentInfo();
        this.encAlg = encInfo.getContentEncryptionAlgorithm();
        CMSProcessableInputStream readable = new CMSProcessableInputStream(((ASN1OctetStringParser)encInfo.getEncryptedContent(4)).getOctetStream());
        CMSEnvelopedHelper.CMSEnvelopedSecureReadable secureReadable = new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, encInfo.getContentType(), readable);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.encAlg, secureReadable);
    }

    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().toString();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return this.encodeObj(this.encAlg.getParameters());
        }
        catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.encAlg;
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public AttributeTable getUnprotectedAttributes() throws IOException {
        if (this.unprotectedAttributes == null && this.attrNotRead) {
            ASN1SetParser set = this.envelopedData.getUnprotectedAttrs();
            this.attrNotRead = false;
            if (set != null) {
                ASN1Encodable o;
                ASN1EncodableVector v = new ASN1EncodableVector();
                while ((o = set.readObject()) != null) {
                    ASN1SequenceParser seq = (ASN1SequenceParser)o;
                    v.add((ASN1Encodable)seq.toASN1Primitive());
                }
                this.unprotectedAttributes = new AttributeTable((ASN1Set)new DERSet(v));
            }
        }
        return this.unprotectedAttributes;
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }
}

