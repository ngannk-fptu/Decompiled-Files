/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.util.Encodable
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cmc.CMCException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;

public class SimplePKIResponse
implements Encodable {
    private final CMSSignedData certificateResponse;

    private static ContentInfo parseBytes(byte[] responseEncoding) throws CMCException {
        try {
            return ContentInfo.getInstance((Object)ASN1Primitive.fromByteArray((byte[])responseEncoding));
        }
        catch (Exception e) {
            throw new CMCException("malformed data: " + e.getMessage(), e);
        }
    }

    public SimplePKIResponse(byte[] responseEncoding) throws CMCException {
        this(SimplePKIResponse.parseBytes(responseEncoding));
    }

    public SimplePKIResponse(ContentInfo signedData) throws CMCException {
        try {
            this.certificateResponse = new CMSSignedData(signedData);
        }
        catch (CMSException e) {
            throw new CMCException("malformed response: " + e.getMessage(), e);
        }
        if (this.certificateResponse.getSignerInfos().size() != 0) {
            throw new CMCException("malformed response: SignerInfo structures found");
        }
        if (this.certificateResponse.getSignedContent() != null) {
            throw new CMCException("malformed response: Signed Content found");
        }
    }

    public Store<X509CertificateHolder> getCertificates() {
        return this.certificateResponse.getCertificates();
    }

    public Store<X509CRLHolder> getCRLs() {
        return this.certificateResponse.getCRLs();
    }

    public byte[] getEncoded() throws IOException {
        return this.certificateResponse.getEncoded();
    }
}

