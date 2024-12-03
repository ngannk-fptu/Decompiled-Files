/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.BEROctetString
 *  org.bouncycastle.asn1.BERSet
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedData
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.cms.CMSEncryptedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEncryptedDataGenerator
extends CMSEncryptedGenerator {
    private CMSEncryptedData doGenerate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            OutputStream cOut = contentEncryptor.getOutputStream(bOut);
            content.write(cOut);
            cOut.close();
        }
        catch (IOException e) {
            throw new CMSException("");
        }
        byte[] encryptedContent = bOut.toByteArray();
        AlgorithmIdentifier encAlgId = contentEncryptor.getAlgorithmIdentifier();
        BEROctetString encContent = new BEROctetString(encryptedContent);
        EncryptedContentInfo eci = new EncryptedContentInfo(content.getContentType(), encAlgId, (ASN1OctetString)encContent);
        BERSet unprotectedAttrSet = null;
        if (this.unprotectedAttributeGenerator != null) {
            AttributeTable attrTable = this.unprotectedAttributeGenerator.getAttributes(Collections.EMPTY_MAP);
            unprotectedAttrSet = new BERSet(attrTable.toASN1EncodableVector());
        }
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.encryptedData, (ASN1Encodable)new EncryptedData(eci, unprotectedAttrSet));
        return new CMSEncryptedData(contentInfo);
    }

    public CMSEncryptedData generate(CMSTypedData content, OutputEncryptor contentEncryptor) throws CMSException {
        return this.doGenerate(content, contentEncryptor);
    }
}

