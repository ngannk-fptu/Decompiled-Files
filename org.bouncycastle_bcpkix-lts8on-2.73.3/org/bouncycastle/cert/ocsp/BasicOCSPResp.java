/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ocsp.BasicOCSPResponse
 *  org.bouncycastle.asn1.ocsp.ResponseData
 *  org.bouncycastle.asn1.ocsp.SingleResponse
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPUtils;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class BasicOCSPResp
implements Encodable {
    private BasicOCSPResponse resp;
    private ResponseData data;
    private Extensions extensions;

    public BasicOCSPResp(BasicOCSPResponse resp) {
        this.resp = resp;
        this.data = resp.getTbsResponseData();
        this.extensions = Extensions.getInstance((Object)resp.getTbsResponseData().getResponseExtensions());
    }

    public byte[] getTBSResponseData() {
        try {
            return this.resp.getTbsResponseData().getEncoded("DER");
        }
        catch (IOException e) {
            return null;
        }
    }

    public AlgorithmIdentifier getSignatureAlgorithmID() {
        return this.resp.getSignatureAlgorithm();
    }

    public int getVersion() {
        return this.data.getVersion().intValueExact() + 1;
    }

    public RespID getResponderId() {
        return new RespID(this.data.getResponderID());
    }

    public Date getProducedAt() {
        return OCSPUtils.extractDate(this.data.getProducedAt());
    }

    public SingleResp[] getResponses() {
        ASN1Sequence s = this.data.getResponses();
        SingleResp[] rs = new SingleResp[s.size()];
        for (int i = 0; i != rs.length; ++i) {
            rs[i] = new SingleResp(SingleResponse.getInstance((Object)s.getObjectAt(i)));
        }
        return rs;
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
        return this.resp.getSignatureAlgorithm().getAlgorithm();
    }

    public byte[] getSignature() {
        return this.resp.getSignature().getOctets();
    }

    public X509CertificateHolder[] getCerts() {
        if (this.resp.getCerts() != null) {
            ASN1Sequence s = this.resp.getCerts();
            if (s != null) {
                X509CertificateHolder[] certs = new X509CertificateHolder[s.size()];
                for (int i = 0; i != certs.length; ++i) {
                    certs[i] = new X509CertificateHolder(Certificate.getInstance((Object)s.getObjectAt(i)));
                }
                return certs;
            }
            return OCSPUtils.EMPTY_CERTS;
        }
        return OCSPUtils.EMPTY_CERTS;
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws OCSPException {
        try {
            ContentVerifier verifier = verifierProvider.get(this.resp.getSignatureAlgorithm());
            OutputStream vOut = verifier.getOutputStream();
            vOut.write(this.resp.getTbsResponseData().getEncoded("DER"));
            vOut.close();
            return verifier.verify(this.getSignature());
        }
        catch (Exception e) {
            throw new OCSPException("exception processing sig: " + e, e);
        }
    }

    public byte[] getEncoded() throws IOException {
        return this.resp.getEncoded();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BasicOCSPResp)) {
            return false;
        }
        BasicOCSPResp r = (BasicOCSPResp)o;
        return this.resp.equals((Object)r.resp);
    }

    public int hashCode() {
        return this.resp.hashCode();
    }
}

