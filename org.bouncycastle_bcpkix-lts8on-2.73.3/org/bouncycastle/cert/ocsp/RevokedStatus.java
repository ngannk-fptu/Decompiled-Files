/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ocsp.RevokedInfo
 *  org.bouncycastle.asn1.x509.CRLReason
 */
package org.bouncycastle.cert.ocsp;

import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPUtils;

public class RevokedStatus
implements CertificateStatus {
    RevokedInfo info;

    public RevokedStatus(RevokedInfo info) {
        this.info = info;
    }

    public RevokedStatus(Date revocationDate) {
        this.info = new RevokedInfo(new ASN1GeneralizedTime(revocationDate));
    }

    public RevokedStatus(Date revocationDate, int reason) {
        this.info = new RevokedInfo(new ASN1GeneralizedTime(revocationDate), CRLReason.lookup((int)reason));
    }

    public Date getRevocationTime() {
        return OCSPUtils.extractDate(this.info.getRevocationTime());
    }

    public boolean hasRevocationReason() {
        return this.info.getRevocationReason() != null;
    }

    public int getRevocationReason() {
        if (this.info.getRevocationReason() == null) {
            throw new IllegalStateException("attempt to get a reason where none is available");
        }
        return this.info.getRevocationReason().getValue().intValue();
    }
}

