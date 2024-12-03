/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.BEROctetString
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.CompressedData
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSCompressedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.operator.OutputCompressor;

public class CMSCompressedDataGenerator {
    public static final String ZLIB = CMSObjectIdentifiers.zlibCompress.getId();

    public CMSCompressedData generate(CMSTypedData content, OutputCompressor compressor) throws CMSException {
        BEROctetString comOcts;
        AlgorithmIdentifier comAlgId;
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            OutputStream zOut = compressor.getOutputStream(bOut);
            content.write(zOut);
            zOut.close();
            comAlgId = compressor.getAlgorithmIdentifier();
            comOcts = new BEROctetString(bOut.toByteArray());
        }
        catch (IOException e) {
            throw new CMSException("exception encoding data.", e);
        }
        ContentInfo comContent = new ContentInfo(content.getContentType(), (ASN1Encodable)comOcts);
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.compressedData, (ASN1Encodable)new CompressedData(comAlgId, comContent));
        return new CMSCompressedData(contentInfo);
    }
}

