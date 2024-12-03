/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.ocsp.OCSPRequest
 *  org.bouncycastle.asn1.ocsp.Request
 *  org.bouncycastle.asn1.ocsp.Signature
 *  org.bouncycastle.asn1.ocsp.TBSRequest
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.ocsp.TBSRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.operator.ContentSigner;

public class OCSPReqBuilder {
    private List list = new ArrayList();
    private GeneralName requestorName = null;
    private Extensions requestExtensions = null;

    public OCSPReqBuilder addRequest(CertificateID certId) {
        this.list.add(new RequestObject(certId, null));
        return this;
    }

    public OCSPReqBuilder addRequest(CertificateID certId, Extensions singleRequestExtensions) {
        this.list.add(new RequestObject(certId, singleRequestExtensions));
        return this;
    }

    public OCSPReqBuilder setRequestorName(X500Name requestorName) {
        this.requestorName = new GeneralName(4, (ASN1Encodable)requestorName);
        return this;
    }

    public OCSPReqBuilder setRequestorName(GeneralName requestorName) {
        this.requestorName = requestorName;
        return this;
    }

    public OCSPReqBuilder setRequestExtensions(Extensions requestExtensions) {
        this.requestExtensions = requestExtensions;
        return this;
    }

    private OCSPReq generateRequest(ContentSigner contentSigner, X509CertificateHolder[] chain) throws OCSPException {
        Iterator it = this.list.iterator();
        ASN1EncodableVector requests = new ASN1EncodableVector();
        while (it.hasNext()) {
            try {
                requests.add((ASN1Encodable)((RequestObject)it.next()).toRequest());
            }
            catch (Exception e) {
                throw new OCSPException("exception creating Request", e);
            }
        }
        TBSRequest tbsReq = new TBSRequest(this.requestorName, (ASN1Sequence)new DERSequence(requests), this.requestExtensions);
        Signature signature = null;
        if (contentSigner != null) {
            if (this.requestorName == null) {
                throw new OCSPException("requestorName must be specified if request is signed.");
            }
            try {
                OutputStream sOut = contentSigner.getOutputStream();
                sOut.write(tbsReq.getEncoded("DER"));
                sOut.close();
            }
            catch (Exception e) {
                throw new OCSPException("exception processing TBSRequest: " + e, e);
            }
            DERBitString bitSig = new DERBitString(contentSigner.getSignature());
            AlgorithmIdentifier sigAlgId = contentSigner.getAlgorithmIdentifier();
            if (chain != null && chain.length > 0) {
                ASN1EncodableVector v = new ASN1EncodableVector();
                for (int i = 0; i != chain.length; ++i) {
                    v.add((ASN1Encodable)chain[i].toASN1Structure());
                }
                signature = new Signature(sigAlgId, bitSig, (ASN1Sequence)new DERSequence(v));
            } else {
                signature = new Signature(sigAlgId, bitSig);
            }
        }
        return new OCSPReq(new OCSPRequest(tbsReq, signature));
    }

    public OCSPReq build() throws OCSPException {
        return this.generateRequest(null, null);
    }

    public OCSPReq build(ContentSigner signer, X509CertificateHolder[] chain) throws OCSPException, IllegalArgumentException {
        if (signer == null) {
            throw new IllegalArgumentException("no signer specified");
        }
        return this.generateRequest(signer, chain);
    }

    private static class RequestObject {
        CertificateID certId;
        Extensions extensions;

        public RequestObject(CertificateID certId, Extensions extensions) {
            this.certId = certId;
            this.extensions = extensions;
        }

        public Request toRequest() throws Exception {
            return new Request(this.certId.toASN1Primitive(), this.extensions);
        }
    }
}

