/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedLinkCertificateRca;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedLinkCertificateTlm;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificate
extends ASN1Object {
    private final Time32 expiryTime;
    private final HashedData certificateHash;

    public ToBeSignedLinkCertificate(Time32 expiryTime, HashedData certificateHash) {
        this.expiryTime = expiryTime;
        this.certificateHash = certificateHash;
    }

    protected ToBeSignedLinkCertificate(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.expiryTime = Time32.getInstance(seq.getObjectAt(0));
        this.certificateHash = HashedData.getInstance(seq.getObjectAt(1));
    }

    public static ToBeSignedLinkCertificate getInstance(Object o) {
        if (o instanceof ToBeSignedLinkCertificate) {
            return (ToBeSignedLinkCertificate)((Object)o);
        }
        if (o != null) {
            return new ToBeSignedLinkCertificate(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public Time32 getExpiryTime() {
        return this.expiryTime;
    }

    public HashedData getCertificateHash() {
        return this.certificateHash;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.expiryTime, this.certificateHash});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Time32 expiryTime;
        private HashedData certificateHash;

        public Builder setExpiryTime(Time32 expiryTime) {
            this.expiryTime = expiryTime;
            return this;
        }

        public Builder setCertificateHash(HashedData certificateHash) {
            this.certificateHash = certificateHash;
            return this;
        }

        public ToBeSignedLinkCertificate createToBeSignedLinkCertificate() {
            return new ToBeSignedLinkCertificate(this.expiryTime, this.certificateHash);
        }

        public ToBeSignedLinkCertificateTlm createToBeSignedLinkCertificateTlm() {
            return new ToBeSignedLinkCertificateTlm(this.expiryTime, this.certificateHash);
        }

        public ToBeSignedLinkCertificateRca createToBeSignedLinkCertificateRca() {
            return new ToBeSignedLinkCertificateRca(this.expiryTime, this.certificateHash);
        }
    }
}

