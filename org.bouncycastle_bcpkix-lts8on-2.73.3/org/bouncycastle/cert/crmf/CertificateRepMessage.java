/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.CertRepMessage
 *  org.bouncycastle.asn1.cmp.CertResponse
 *  org.bouncycastle.asn1.cmp.PKIBody
 */
package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateResponse;

public class CertificateRepMessage {
    private final CertResponse[] resps;
    private final CMPCertificate[] caCerts;

    public CertificateRepMessage(CertRepMessage repMessage) {
        this.resps = repMessage.getResponse();
        this.caCerts = repMessage.getCaPubs();
    }

    public static CertificateRepMessage fromPKIBody(PKIBody pkiBody) {
        if (!CertificateRepMessage.isCertificateRepMessage(pkiBody.getType())) {
            throw new IllegalArgumentException("content of PKIBody wrong type: " + pkiBody.getType());
        }
        return new CertificateRepMessage(CertRepMessage.getInstance((Object)pkiBody.getContent()));
    }

    public static boolean isCertificateRepMessage(int bodyType) {
        switch (bodyType) {
            case 1: 
            case 3: 
            case 8: 
            case 14: {
                return true;
            }
        }
        return false;
    }

    public CertificateResponse[] getResponses() {
        CertificateResponse[] responses = new CertificateResponse[this.resps.length];
        for (int i = 0; i != responses.length; ++i) {
            responses[i] = new CertificateResponse(this.resps[i]);
        }
        return responses;
    }

    public X509CertificateHolder[] getX509Certificates() {
        ArrayList<X509CertificateHolder> certs = new ArrayList<X509CertificateHolder>();
        for (int i = 0; i != this.caCerts.length; ++i) {
            if (!this.caCerts[i].isX509v3PKCert()) continue;
            certs.add(new X509CertificateHolder(this.caCerts[i].getX509v3PKCert()));
        }
        return certs.toArray(new X509CertificateHolder[0]);
    }

    public boolean isOnlyX509PKCertificates() {
        boolean isOnlyX509 = true;
        for (int i = 0; i != this.caCerts.length; ++i) {
            isOnlyX509 &= this.caCerts[i].isX509v3PKCert();
        }
        return isOnlyX509;
    }

    public CMPCertificate[] getCMPCertificates() {
        CMPCertificate[] certs = new CMPCertificate[this.caCerts.length];
        System.arraycopy(this.caCerts, 0, certs, 0, certs.length);
        return certs;
    }

    public ASN1Encodable toASN1Structure() {
        return new CertRepMessage(this.caCerts, this.resps);
    }
}

