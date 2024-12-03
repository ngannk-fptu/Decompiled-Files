/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
 *  org.bouncycastle.asn1.cms.EnvelopedData
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedHelper;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.util.Encodable;

public class CMSEnvelopedData
implements Encodable {
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private AlgorithmIdentifier encAlg;
    private ASN1Set unprotectedAttributes;
    private OriginatorInformation originatorInfo;

    public CMSEnvelopedData(byte[] envelopedData) throws CMSException {
        this(CMSUtils.readContentInfo(envelopedData));
    }

    public CMSEnvelopedData(InputStream envelopedData) throws CMSException {
        this(CMSUtils.readContentInfo(envelopedData));
    }

    public CMSEnvelopedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            EnvelopedData envData = EnvelopedData.getInstance((Object)contentInfo.getContent());
            if (envData.getOriginatorInfo() != null) {
                this.originatorInfo = new OriginatorInformation(envData.getOriginatorInfo());
            }
            ASN1Set recipientInfos = envData.getRecipientInfos();
            EncryptedContentInfo encInfo = envData.getEncryptedContentInfo();
            this.encAlg = encInfo.getContentEncryptionAlgorithm();
            CMSProcessableByteArray readable = new CMSProcessableByteArray(encInfo.getEncryptedContent().getOctets());
            CMSEnvelopedHelper.CMSEnvelopedSecureReadable secureReadable = new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, encInfo.getContentType(), readable);
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.encAlg, secureReadable);
            this.unprotectedAttributes = envData.getUnprotectedAttrs();
        }
        catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException("Malformed content.", e);
        }
    }

    private byte[] encodeObj(ASN1Encodable obj) throws IOException {
        if (obj != null) {
            return obj.toASN1Primitive().getEncoded();
        }
        return null;
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.encAlg;
    }

    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().getId();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return this.encodeObj(this.encAlg.getParameters());
        }
        catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public AttributeTable getUnprotectedAttributes() {
        if (this.unprotectedAttributes == null) {
            return null;
        }
        return new AttributeTable(this.unprotectedAttributes);
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}

