/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Exception
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ocsp.OCSPRequest
 *  org.bouncycastle.asn1.ocsp.Request
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPUtils;
import org.bouncycastle.cert.ocsp.Req;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;

public class OCSPReq {
    private static final X509CertificateHolder[] EMPTY_CERTS = new X509CertificateHolder[0];
    private OCSPRequest req;
    private Extensions extensions;

    public OCSPReq(OCSPRequest req) {
        this.req = req;
        this.extensions = req.getTbsRequest().getRequestExtensions();
    }

    public OCSPReq(byte[] req) throws IOException {
        this(new ASN1InputStream(req));
    }

    private OCSPReq(ASN1InputStream aIn) throws IOException {
        try {
            this.req = OCSPRequest.getInstance((Object)aIn.readObject());
            if (this.req == null) {
                throw new CertIOException("malformed request: no request data found");
            }
            this.extensions = this.req.getTbsRequest().getRequestExtensions();
        }
        catch (IllegalArgumentException e) {
            throw new CertIOException("malformed request: " + e.getMessage(), e);
        }
        catch (ClassCastException e) {
            throw new CertIOException("malformed request: " + e.getMessage(), e);
        }
        catch (ASN1Exception e) {
            throw new CertIOException("malformed request: " + e.getMessage(), e);
        }
    }

    public int getVersionNumber() {
        return this.req.getTbsRequest().getVersion().intValueExact() + 1;
    }

    public GeneralName getRequestorName() {
        return GeneralName.getInstance((Object)this.req.getTbsRequest().getRequestorName());
    }

    public Req[] getRequestList() {
        ASN1Sequence seq = this.req.getTbsRequest().getRequestList();
        Req[] requests = new Req[seq.size()];
        for (int i = 0; i != requests.length; ++i) {
            requests[i] = new Req(Request.getInstance((Object)seq.getObjectAt(i)));
        }
        return requests;
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        if (this.extensions != null) {
            return this.extensions.getExtension(oid);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return OCSPUtils.getExtensionOIDs(this.extensions);
    }

    public Set getCriticalExtensionOIDs() {
        return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
    }

    public ASN1ObjectIdentifier getSignatureAlgOID() {
        if (!this.isSigned()) {
            return null;
        }
        return this.req.getOptionalSignature().getSignatureAlgorithm().getAlgorithm();
    }

    public byte[] getSignature() {
        if (!this.isSigned()) {
            return null;
        }
        return this.req.getOptionalSignature().getSignature().getOctets();
    }

    public X509CertificateHolder[] getCerts() {
        if (this.req.getOptionalSignature() != null) {
            ASN1Sequence s = this.req.getOptionalSignature().getCerts();
            if (s != null) {
                X509CertificateHolder[] certs = new X509CertificateHolder[s.size()];
                for (int i = 0; i != certs.length; ++i) {
                    certs[i] = new X509CertificateHolder(Certificate.getInstance((Object)s.getObjectAt(i)));
                }
                return certs;
            }
            return EMPTY_CERTS;
        }
        return EMPTY_CERTS;
    }

    public boolean isSigned() {
        return this.req.getOptionalSignature() != null;
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws OCSPException {
        if (!this.isSigned()) {
            throw new OCSPException("attempt to verify signature on unsigned object");
        }
        try {
            ContentVerifier verifier = verifierProvider.get(this.req.getOptionalSignature().getSignatureAlgorithm());
            OutputStream sOut = verifier.getOutputStream();
            sOut.write(this.req.getTbsRequest().getEncoded("DER"));
            return verifier.verify(this.getSignature());
        }
        catch (Exception e) {
            throw new OCSPException("exception processing signature: " + e, e);
        }
    }

    public byte[] getEncoded() throws IOException {
        return this.req.getEncoded();
    }
}

