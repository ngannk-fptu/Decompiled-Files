/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.BEROctetString
 *  org.bouncycastle.asn1.BERSet
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
 *  org.bouncycastle.asn1.cms.EnvelopedData
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataGenerator
extends CMSEnvelopedGenerator {
    private CMSEnvelopedData doGenerate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            OutputStream cOut = contentEncryptor.getOutputStream(bOut);
            content.write(cOut);
            cOut.close();
            if (contentEncryptor instanceof OutputAEADEncryptor) {
                byte[] mac = ((OutputAEADEncryptor)contentEncryptor).getMAC();
                bOut.write(mac, 0, mac.length);
            }
        }
        catch (IOException e) {
            throw new CMSException("");
        }
        byte[] encryptedContent = bOut.toByteArray();
        AlgorithmIdentifier encAlgId = contentEncryptor.getAlgorithmIdentifier();
        BEROctetString encContent = new BEROctetString(encryptedContent);
        GenericKey encKey = contentEncryptor.getKey();
        for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
            recipientInfos.add((ASN1Encodable)recipient.generate(encKey));
        }
        EncryptedContentInfo eci = new EncryptedContentInfo(content.getContentType(), encAlgId, (ASN1OctetString)encContent);
        BERSet unprotectedAttrSet = null;
        if (this.unprotectedAttributeGenerator != null) {
            AttributeTable attrTable = this.unprotectedAttributeGenerator.getAttributes(Collections.EMPTY_MAP);
            unprotectedAttrSet = new BERSet(attrTable.toASN1EncodableVector());
        }
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.envelopedData, (ASN1Encodable)new EnvelopedData(this.originatorInfo, (ASN1Set)new DERSet(recipientInfos), eci, (ASN1Set)unprotectedAttrSet));
        return new CMSEnvelopedData(contentInfo);
    }

    public CMSEnvelopedData generate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        return this.doGenerate(content, contentEncryptor);
    }
}

