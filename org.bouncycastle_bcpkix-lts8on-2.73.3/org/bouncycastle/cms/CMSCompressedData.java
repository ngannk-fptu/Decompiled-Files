/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.cms.CompressedData
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.util.Encodable;

public class CMSCompressedData
implements Encodable {
    ContentInfo contentInfo;
    CompressedData comData;

    public CMSCompressedData(byte[] compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
    }

    public CMSCompressedData(InputStream compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
    }

    public CMSCompressedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            this.comData = CompressedData.getInstance((Object)contentInfo.getContent());
        }
        catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException("Malformed content.", e);
        }
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentInfo.getContentType();
    }

    public ASN1ObjectIdentifier getCompressedContentType() {
        return this.comData.getEncapContentInfo().getContentType();
    }

    public CMSTypedStream getContentStream(InputExpanderProvider expanderProvider) {
        ContentInfo content = this.comData.getEncapContentInfo();
        ASN1OctetString bytes = (ASN1OctetString)content.getContent();
        InputExpander expander = expanderProvider.get(this.comData.getCompressionAlgorithmIdentifier());
        InputStream zIn = expander.getInputStream(bytes.getOctetStream());
        return new CMSTypedStream(content.getContentType(), zIn);
    }

    public byte[] getContent(InputExpanderProvider expanderProvider) throws CMSException {
        ContentInfo content = this.comData.getEncapContentInfo();
        ASN1OctetString bytes = (ASN1OctetString)content.getContent();
        InputExpander expander = expanderProvider.get(this.comData.getCompressionAlgorithmIdentifier());
        InputStream zIn = expander.getInputStream(bytes.getOctetStream());
        try {
            return CMSUtils.streamToByteArray(zIn);
        }
        catch (IOException e) {
            throw new CMSException("exception reading compressed stream.", e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}

