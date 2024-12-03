/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ParsingException
 *  org.bouncycastle.asn1.eac.CVCertificateRequest
 *  org.bouncycastle.asn1.eac.PublicKeyDataObject
 */
package org.bouncycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.eac.CVCertificateRequest;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.eac.EACException;
import org.bouncycastle.eac.EACIOException;
import org.bouncycastle.eac.operator.EACSignatureVerifier;

public class EACCertificateRequestHolder {
    private CVCertificateRequest request;

    private static CVCertificateRequest parseBytes(byte[] requestEncoding) throws IOException {
        try {
            return CVCertificateRequest.getInstance((Object)requestEncoding);
        }
        catch (ClassCastException e) {
            throw new EACIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new EACIOException("malformed data: " + e.getMessage(), e);
        }
        catch (ASN1ParsingException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            }
            throw new EACIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public EACCertificateRequestHolder(byte[] certEncoding) throws IOException {
        this(EACCertificateRequestHolder.parseBytes(certEncoding));
    }

    public EACCertificateRequestHolder(CVCertificateRequest request) {
        this.request = request;
    }

    public CVCertificateRequest toASN1Structure() {
        return this.request;
    }

    public PublicKeyDataObject getPublicKeyDataObject() {
        return this.request.getPublicKey();
    }

    public boolean isInnerSignatureValid(EACSignatureVerifier verifier) throws EACException {
        try {
            OutputStream vOut = verifier.getOutputStream();
            vOut.write(this.request.getCertificateBody().getEncoded("DER"));
            vOut.close();
            return verifier.verify(this.request.getInnerSignature());
        }
        catch (Exception e) {
            throw new EACException("unable to process signature: " + e.getMessage(), e);
        }
    }
}

