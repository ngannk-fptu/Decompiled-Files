/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 */
package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSRuntimeException;
import org.bouncycastle.cms.CMSTypedStream;

public class PKCS7TypedStream
extends CMSTypedStream {
    private final ASN1Encodable content;

    public PKCS7TypedStream(ASN1ObjectIdentifier oid, ASN1Encodable encodable) throws IOException {
        super(oid);
        this.content = encodable;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }

    @Override
    public InputStream getContentStream() {
        try {
            return this.getContentStream(this.content);
        }
        catch (IOException e) {
            throw new CMSRuntimeException("unable to convert content to stream: " + e.getMessage(), e);
        }
    }

    @Override
    public void drain() throws IOException {
        this.content.toASN1Primitive();
    }

    private InputStream getContentStream(ASN1Encodable encodable) throws IOException {
        byte dl;
        byte[] encoded = encodable.toASN1Primitive().getEncoded("DER");
        int index = 0;
        if ((encoded[index++] & 0x1F) == 31) {
            while ((encoded[index++] & 0x80) != 0) {
            }
        }
        if (((dl = encoded[index++]) & 0x80) != 0) {
            index += dl & 0x7F;
        }
        return new ByteArrayInputStream(encoded, index, encoded.length - index);
    }
}

