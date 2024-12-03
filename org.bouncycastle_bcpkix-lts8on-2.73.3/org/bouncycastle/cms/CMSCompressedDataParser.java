/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetStringParser
 *  org.bouncycastle.asn1.ASN1SequenceParser
 *  org.bouncycastle.asn1.cms.CompressedDataParser
 *  org.bouncycastle.asn1.cms.ContentInfoParser
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.cms.CompressedDataParser;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.cms.CMSContentInfoParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;

public class CMSCompressedDataParser
extends CMSContentInfoParser {
    public CMSCompressedDataParser(byte[] compressedData) throws CMSException {
        this(new ByteArrayInputStream(compressedData));
    }

    public CMSCompressedDataParser(InputStream compressedData) throws CMSException {
        super(compressedData);
    }

    public CMSTypedStream getContent(InputExpanderProvider expanderProvider) throws CMSException {
        try {
            CompressedDataParser comData = new CompressedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
            ContentInfoParser content = comData.getEncapContentInfo();
            InputExpander expander = expanderProvider.get(comData.getCompressionAlgorithmIdentifier());
            ASN1OctetStringParser bytes = (ASN1OctetStringParser)content.getContent(4);
            return new CMSTypedStream(content.getContentType(), expander.getInputStream(bytes.getOctetStream()));
        }
        catch (IOException e) {
            throw new CMSException("IOException reading compressed content.", e);
        }
    }
}

