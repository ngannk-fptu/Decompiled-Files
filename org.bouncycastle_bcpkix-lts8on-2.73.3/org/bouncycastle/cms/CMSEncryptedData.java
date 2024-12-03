/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedContentInfo
 *  org.bouncycastle.asn1.cms.EncryptedData
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;

public class CMSEncryptedData {
    private ContentInfo contentInfo;
    private EncryptedData encryptedData;

    public CMSEncryptedData(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
        this.encryptedData = EncryptedData.getInstance((Object)contentInfo.getContent());
    }

    public byte[] getContent(InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(this.getContentStream(inputDecryptorProvider).getContentStream());
        }
        catch (IOException e) {
            throw new CMSException("unable to parse internal stream: " + e.getMessage(), e);
        }
    }

    public CMSTypedStream getContentStream(InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            EncryptedContentInfo encContentInfo = this.encryptedData.getEncryptedContentInfo();
            InputDecryptor decrytor = inputDecryptorProvider.get(encContentInfo.getContentEncryptionAlgorithm());
            ByteArrayInputStream encIn = new ByteArrayInputStream(encContentInfo.getEncryptedContent().getOctets());
            return new CMSTypedStream(encContentInfo.getContentType(), decrytor.getInputStream(encIn));
        }
        catch (Exception e) {
            throw new CMSException("unable to create stream: " + e.getMessage(), e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
}

