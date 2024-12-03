/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.BEROctetString
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DLSet
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.AuthEnvelopedData
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
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
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAuthEnvelopedData;
import org.bouncycastle.cms.CMSAuthEnvelopedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputAEADEncryptor;

public class CMSAuthEnvelopedDataGenerator
extends CMSAuthEnvelopedGenerator {
    private CMSAuthEnvelopedData doGenerate(CMSTypedData content, OutputAEADEncryptor contentEncryptor) throws CMSException {
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DERSet authenticatedAttrSet = null;
        try {
            OutputStream cOut = contentEncryptor.getOutputStream(bOut);
            content.write(cOut);
            if (this.authAttrsGenerator != null) {
                AttributeTable attrTable = this.authAttrsGenerator.getAttributes(Collections.EMPTY_MAP);
                authenticatedAttrSet = new DERSet(attrTable.toASN1EncodableVector());
                contentEncryptor.getAADStream().write(authenticatedAttrSet.getEncoded("DER"));
            }
            cOut.close();
        }
        catch (IOException e) {
            throw new CMSException("unable to process authenticated content: " + e.getMessage(), e);
        }
        byte[] encryptedContent = bOut.toByteArray();
        byte[] mac = contentEncryptor.getMAC();
        AlgorithmIdentifier encAlgId = contentEncryptor.getAlgorithmIdentifier();
        BEROctetString encContent = new BEROctetString(encryptedContent);
        GenericKey encKey = contentEncryptor.getKey();
        for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
            recipientInfos.add((ASN1Encodable)recipient.generate(encKey));
        }
        EncryptedContentInfo eci = new EncryptedContentInfo(content.getContentType(), encAlgId, (ASN1OctetString)encContent);
        DLSet unprotectedAttrSet = null;
        if (this.unauthAttrsGenerator != null) {
            AttributeTable attrTable = this.unauthAttrsGenerator.getAttributes(Collections.EMPTY_MAP);
            unprotectedAttrSet = new DLSet(attrTable.toASN1EncodableVector());
        }
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.authEnvelopedData, (ASN1Encodable)new AuthEnvelopedData(this.originatorInfo, (ASN1Set)new DERSet(recipientInfos), eci, (ASN1Set)authenticatedAttrSet, (ASN1OctetString)new DEROctetString(mac), (ASN1Set)unprotectedAttrSet));
        return new CMSAuthEnvelopedData(contentInfo);
    }

    public CMSAuthEnvelopedData generate(CMSTypedData content, OutputAEADEncryptor contentEncryptor) throws CMSException {
        return this.doGenerate(content, contentEncryptor);
    }
}

