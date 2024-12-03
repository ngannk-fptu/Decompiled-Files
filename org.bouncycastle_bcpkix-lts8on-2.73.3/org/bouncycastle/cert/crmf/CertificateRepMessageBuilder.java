/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.CertRepMessage
 *  org.bouncycastle.asn1.cmp.CertResponse
 */
package org.bouncycastle.cert.crmf;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertRepMessage;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateRepMessage;
import org.bouncycastle.cert.crmf.CertificateResponse;

public class CertificateRepMessageBuilder {
    private final List<CertResponse> responses = new ArrayList<CertResponse>();
    private final CMPCertificate[] caCerts;

    public CertificateRepMessageBuilder(X509CertificateHolder ... caCerts) {
        this.caCerts = new CMPCertificate[caCerts.length];
        for (int i = 0; i != caCerts.length; ++i) {
            this.caCerts[i] = new CMPCertificate(caCerts[i].toASN1Structure());
        }
    }

    public CertificateRepMessageBuilder addCertificateResponse(CertificateResponse response) {
        this.responses.add(response.toASN1Structure());
        return this;
    }

    public CertificateRepMessage build() {
        CertRepMessage repMessage = this.caCerts.length != 0 ? new CertRepMessage(this.caCerts, this.responses.toArray(new CertResponse[0])) : new CertRepMessage(null, this.responses.toArray(new CertResponse[0]));
        this.responses.clear();
        return new CertificateRepMessage(repMessage);
    }
}

