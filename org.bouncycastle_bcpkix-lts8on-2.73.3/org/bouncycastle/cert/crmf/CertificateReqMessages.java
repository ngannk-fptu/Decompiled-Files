/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cmp.PKIBody
 *  org.bouncycastle.asn1.crmf.CertReqMessages
 *  org.bouncycastle.asn1.crmf.CertReqMsg
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.crmf.CertReqMessages;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;

public class CertificateReqMessages {
    private final CertReqMsg[] reqs;

    public CertificateReqMessages(CertReqMessages certReqMessages) {
        this.reqs = certReqMessages.toCertReqMsgArray();
    }

    public static CertificateReqMessages fromPKIBody(PKIBody pkiBody) {
        if (!CertificateReqMessages.isCertificateRequestMessages(pkiBody.getType())) {
            throw new IllegalArgumentException("content of PKIBody wrong type: " + pkiBody.getType());
        }
        return new CertificateReqMessages(CertReqMessages.getInstance((Object)pkiBody.getContent()));
    }

    public static boolean isCertificateRequestMessages(int bodyType) {
        switch (bodyType) {
            case 0: 
            case 2: 
            case 7: 
            case 9: 
            case 13: {
                return true;
            }
        }
        return false;
    }

    public CertificateRequestMessage[] getRequests() {
        CertificateRequestMessage[] requestMessages = new CertificateRequestMessage[this.reqs.length];
        for (int i = 0; i != requestMessages.length; ++i) {
            requestMessages[i] = new CertificateRequestMessage(this.reqs[i]);
        }
        return requestMessages;
    }

    public CertReqMessages toASN1Structure() {
        return new CertReqMessages(this.reqs);
    }
}

