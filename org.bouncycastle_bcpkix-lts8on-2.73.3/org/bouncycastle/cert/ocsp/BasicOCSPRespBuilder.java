/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERGeneralizedTime
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.ocsp.BasicOCSPResponse
 *  org.bouncycastle.asn1.ocsp.CertStatus
 *  org.bouncycastle.asn1.ocsp.ResponseData
 *  org.bouncycastle.asn1.ocsp.RevokedInfo
 *  org.bouncycastle.asn1.ocsp.SingleResponse
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.CRLReason
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;

public class BasicOCSPRespBuilder {
    private List list = new ArrayList();
    private Extensions responseExtensions = null;
    private RespID responderID;

    public BasicOCSPRespBuilder(RespID responderID) {
        this.responderID = responderID;
    }

    public BasicOCSPRespBuilder(SubjectPublicKeyInfo key, DigestCalculator digCalc) throws OCSPException {
        this.responderID = new RespID(key, digCalc);
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus) {
        this.addResponse(certID, certStatus, new Date(), null, null);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Extensions singleExtensions) {
        this.addResponse(certID, certStatus, new Date(), null, singleExtensions);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Date nextUpdate, Extensions singleExtensions) {
        this.addResponse(certID, certStatus, new Date(), nextUpdate, singleExtensions);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Date thisUpdate, Date nextUpdate) {
        this.addResponse(certID, certStatus, thisUpdate, nextUpdate, null);
        return this;
    }

    public BasicOCSPRespBuilder addResponse(CertificateID certID, CertificateStatus certStatus, Date thisUpdate, Date nextUpdate, Extensions singleExtensions) {
        this.list.add(new ResponseObject(certID, certStatus, thisUpdate, nextUpdate, singleExtensions));
        return this;
    }

    public BasicOCSPRespBuilder setResponseExtensions(Extensions responseExtensions) {
        this.responseExtensions = responseExtensions;
        return this;
    }

    public BasicOCSPResp build(ContentSigner signer, X509CertificateHolder[] chain, Date producedAt) throws OCSPException {
        DERBitString bitSig;
        Iterator it = this.list.iterator();
        ASN1EncodableVector responses = new ASN1EncodableVector();
        while (it.hasNext()) {
            try {
                responses.add((ASN1Encodable)((ResponseObject)it.next()).toResponse());
            }
            catch (Exception e) {
                throw new OCSPException("exception creating Request", e);
            }
        }
        ResponseData tbsResp = new ResponseData(this.responderID.toASN1Primitive(), new ASN1GeneralizedTime(producedAt), (ASN1Sequence)new DERSequence(responses), this.responseExtensions);
        try {
            OutputStream sigOut = signer.getOutputStream();
            sigOut.write(tbsResp.getEncoded("DER"));
            sigOut.close();
            bitSig = new DERBitString(signer.getSignature());
        }
        catch (Exception e) {
            throw new OCSPException("exception processing TBSRequest: " + e.getMessage(), e);
        }
        AlgorithmIdentifier sigAlgId = signer.getAlgorithmIdentifier();
        DERSequence chainSeq = null;
        if (chain != null && chain.length > 0) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            for (int i = 0; i != chain.length; ++i) {
                v.add((ASN1Encodable)chain[i].toASN1Structure());
            }
            chainSeq = new DERSequence(v);
        }
        return new BasicOCSPResp(new BasicOCSPResponse(tbsResp, sigAlgId, bitSig, chainSeq));
    }

    private static class ResponseObject {
        CertificateID certId;
        CertStatus certStatus;
        ASN1GeneralizedTime thisUpdate;
        ASN1GeneralizedTime nextUpdate;
        Extensions extensions;

        public ResponseObject(CertificateID certId, CertificateStatus certStatus, Date thisUpdate, Date nextUpdate, Extensions extensions) {
            RevokedStatus rs;
            this.certId = certId;
            this.certStatus = certStatus == null ? new CertStatus() : (certStatus instanceof UnknownStatus ? new CertStatus(2, (ASN1Encodable)DERNull.INSTANCE) : ((rs = (RevokedStatus)certStatus).hasRevocationReason() ? new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(rs.getRevocationTime()), CRLReason.lookup((int)rs.getRevocationReason()))) : new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(rs.getRevocationTime()), null))));
            this.thisUpdate = new DERGeneralizedTime(thisUpdate);
            this.nextUpdate = nextUpdate != null ? new DERGeneralizedTime(nextUpdate) : null;
            this.extensions = extensions;
        }

        public SingleResponse toResponse() throws Exception {
            return new SingleResponse(this.certId.toASN1Primitive(), this.certStatus, this.thisUpdate, this.nextUpdate, this.extensions);
        }
    }
}

