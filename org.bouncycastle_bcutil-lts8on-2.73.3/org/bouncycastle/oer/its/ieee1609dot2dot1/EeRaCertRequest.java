/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;
import org.bouncycastle.oer.its.ieee1609dot2dot1.AdditionalParams;

public class EeRaCertRequest
extends ASN1Object {
    private final UINT8 version;
    private final Time32 generationTime;
    private final CertificateType type;
    private final ToBeSignedCertificate tbsCert;
    private final AdditionalParams additionalParams;

    public EeRaCertRequest(UINT8 version, Time32 generationTime, CertificateType type, ToBeSignedCertificate tbsCert, AdditionalParams additionalParams) {
        this.version = version;
        this.generationTime = generationTime;
        this.type = type;
        this.tbsCert = tbsCert;
        this.additionalParams = additionalParams;
    }

    private EeRaCertRequest(ASN1Sequence seq) {
        if (seq.size() != 5) {
            throw new IllegalArgumentException("expected sequence size of 5");
        }
        this.version = UINT8.getInstance(seq.getObjectAt(0));
        this.generationTime = Time32.getInstance(seq.getObjectAt(1));
        this.type = CertificateType.getInstance(seq.getObjectAt(2));
        this.tbsCert = ToBeSignedCertificate.getInstance(seq.getObjectAt(3));
        this.additionalParams = OEROptional.getInstance(seq.getObjectAt(4)).getObject(AdditionalParams.class);
    }

    public static EeRaCertRequest getInstance(Object o) {
        if (o instanceof EeRaCertRequest) {
            return (EeRaCertRequest)((Object)o);
        }
        if (o != null) {
            return new EeRaCertRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UINT8 getVersion() {
        return this.version;
    }

    public Time32 getGenerationTime() {
        return this.generationTime;
    }

    public CertificateType getType() {
        return this.type;
    }

    public ToBeSignedCertificate getTbsCert() {
        return this.tbsCert;
    }

    public AdditionalParams getAdditionalParams() {
        return this.additionalParams;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.version, this.generationTime, this.type, this.tbsCert, OEROptional.getInstance((Object)this.additionalParams)});
    }

    public static class Builder {
        private UINT8 version;
        private Time32 generationTime;
        private CertificateType type;
        private ToBeSignedCertificate tbsCert;
        private AdditionalParams additionalParams;

        public Builder setVersion(UINT8 version) {
            this.version = version;
            return this;
        }

        public Builder setGenerationTime(Time32 generationTime) {
            this.generationTime = generationTime;
            return this;
        }

        public Builder setType(CertificateType type) {
            this.type = type;
            return this;
        }

        public Builder setTbsCert(ToBeSignedCertificate tbsCert) {
            this.tbsCert = tbsCert;
            return this;
        }

        public Builder setAdditionalParams(AdditionalParams additionalParams) {
            this.additionalParams = additionalParams;
            return this;
        }

        public EeRaCertRequest createEeRaCertRequest() {
            return new EeRaCertRequest(this.version, this.generationTime, this.type, this.tbsCert, this.additionalParams);
        }
    }
}

