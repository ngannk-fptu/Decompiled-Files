/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ParsingException
 *  org.bouncycastle.asn1.eac.CVCertificate
 *  org.bouncycastle.asn1.eac.PublicKeyDataObject
 */
package org.bouncycastle.eac;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.eac.CVCertificate;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import org.bouncycastle.eac.EACException;
import org.bouncycastle.eac.EACIOException;
import org.bouncycastle.eac.operator.EACSignatureVerifier;

public class EACCertificateHolder {
    private CVCertificate cvCertificate;

    private static CVCertificate parseBytes(byte[] certEncoding) throws IOException {
        try {
            return CVCertificate.getInstance((Object)certEncoding);
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

    public EACCertificateHolder(byte[] certEncoding) throws IOException {
        this(EACCertificateHolder.parseBytes(certEncoding));
    }

    public EACCertificateHolder(CVCertificate cvCertificate) {
        this.cvCertificate = cvCertificate;
    }

    public CVCertificate toASN1Structure() {
        return this.cvCertificate;
    }

    public PublicKeyDataObject getPublicKeyDataObject() {
        return this.cvCertificate.getBody().getPublicKey();
    }

    public boolean isSignatureValid(EACSignatureVerifier verifier) throws EACException {
        try {
            OutputStream vOut = verifier.getOutputStream();
            vOut.write(this.cvCertificate.getBody().getEncoded("DER"));
            vOut.close();
            return verifier.verify(this.cvCertificate.getSignature());
        }
        catch (Exception e) {
            throw new EACException("unable to process signature: " + e.getMessage(), e);
        }
    }
}

