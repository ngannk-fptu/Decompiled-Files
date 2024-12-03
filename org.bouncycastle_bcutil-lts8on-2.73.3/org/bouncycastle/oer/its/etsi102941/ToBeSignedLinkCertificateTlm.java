/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedLinkCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificateTlm
extends ToBeSignedLinkCertificate {
    public ToBeSignedLinkCertificateTlm(Time32 expiryTime, HashedData certificateHash) {
        super(expiryTime, certificateHash);
    }

    protected ToBeSignedLinkCertificateTlm(ASN1Sequence seq) {
        super(seq);
    }

    private ToBeSignedLinkCertificateTlm(ToBeSignedLinkCertificate cert) {
        super(cert.getExpiryTime(), cert.getCertificateHash());
    }

    public static ToBeSignedLinkCertificateTlm getInstance(Object o) {
        if (o instanceof ToBeSignedLinkCertificateTlm) {
            return (ToBeSignedLinkCertificateTlm)((Object)o);
        }
        if (o instanceof ToBeSignedLinkCertificate) {
            return new ToBeSignedLinkCertificateTlm((ToBeSignedLinkCertificate)((Object)o));
        }
        if (o != null) {
            return new ToBeSignedLinkCertificateTlm(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}

