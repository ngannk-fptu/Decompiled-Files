/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.CertOrEncCert
 *  org.bouncycastle.asn1.cmp.CertResponse
 *  org.bouncycastle.asn1.cmp.CertifiedKeyPair
 *  org.bouncycastle.asn1.cmp.PKIStatusInfo
 *  org.bouncycastle.asn1.cms.EnvelopedData
 *  org.bouncycastle.asn1.crmf.EncryptedKey
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertOrEncCert;
import org.bouncycastle.asn1.cmp.CertResponse;
import org.bouncycastle.asn1.cmp.CertifiedKeyPair;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateResponse;
import org.bouncycastle.cms.CMSEnvelopedData;

public class CertificateResponseBuilder {
    private final ASN1Integer certReqId;
    private final PKIStatusInfo statusInfo;
    private CertifiedKeyPair certKeyPair;
    private ASN1OctetString rspInfo;

    public CertificateResponseBuilder(ASN1Integer certReqId, PKIStatusInfo statusInfo) {
        this.certReqId = certReqId;
        this.statusInfo = statusInfo;
    }

    public CertificateResponseBuilder withCertificate(X509CertificateHolder certificate) {
        if (this.certKeyPair != null) {
            throw new IllegalStateException("certificate in response already set");
        }
        this.certKeyPair = new CertifiedKeyPair(new CertOrEncCert(new CMPCertificate(certificate.toASN1Structure())));
        return this;
    }

    public CertificateResponseBuilder withCertificate(CMPCertificate certificate) {
        if (this.certKeyPair != null) {
            throw new IllegalStateException("certificate in response already set");
        }
        this.certKeyPair = new CertifiedKeyPair(new CertOrEncCert(certificate));
        return this;
    }

    public CertificateResponseBuilder withCertificate(CMSEnvelopedData encryptedCertificate) {
        if (this.certKeyPair != null) {
            throw new IllegalStateException("certificate in response already set");
        }
        this.certKeyPair = new CertifiedKeyPair(new CertOrEncCert(new EncryptedKey(EnvelopedData.getInstance((Object)encryptedCertificate.toASN1Structure().getContent()))));
        return this;
    }

    public CertificateResponseBuilder withResponseInfo(byte[] responseInfo) {
        if (this.rspInfo != null) {
            throw new IllegalStateException("response info already set");
        }
        this.rspInfo = new DEROctetString(responseInfo);
        return this;
    }

    public CertificateResponse build() {
        return new CertificateResponse(new CertResponse(this.certReqId, this.statusInfo, this.certKeyPair, this.rspInfo));
    }
}

