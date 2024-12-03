/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERNull
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GeographicRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SubjectAssurance;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class ToBeSignedCertificate
extends ASN1Object {
    private final CertificateId id;
    private final HashedId3 cracaId;
    private final CrlSeries crlSeries;
    private final ValidityPeriod validityPeriod;
    private final GeographicRegion region;
    private final SubjectAssurance assuranceLevel;
    private final SequenceOfPsidSsp appPermissions;
    private final SequenceOfPsidGroupPermissions certIssuePermissions;
    private final SequenceOfPsidGroupPermissions certRequestPermissions;
    private final ASN1Null canRequestRollover;
    private final PublicEncryptionKey encryptionKey;
    private final VerificationKeyIndicator verifyKeyIndicator;

    public ToBeSignedCertificate(CertificateId certificateId, HashedId3 cracaId, CrlSeries crlSeries, ValidityPeriod validityPeriod, GeographicRegion geographicRegion, SubjectAssurance assuranceLevel, SequenceOfPsidSsp appPermissions, SequenceOfPsidGroupPermissions certIssuePermissions, SequenceOfPsidGroupPermissions certRequestPermissions, ASN1Null canRequestRollover, PublicEncryptionKey encryptionKey, VerificationKeyIndicator verificationKeyIndicator) {
        this.id = certificateId;
        this.cracaId = cracaId;
        this.crlSeries = crlSeries;
        this.validityPeriod = validityPeriod;
        this.region = geographicRegion;
        this.assuranceLevel = assuranceLevel;
        this.appPermissions = appPermissions;
        this.certIssuePermissions = certIssuePermissions;
        this.certRequestPermissions = certRequestPermissions;
        this.canRequestRollover = canRequestRollover;
        this.encryptionKey = encryptionKey;
        this.verifyKeyIndicator = verificationKeyIndicator;
    }

    private ToBeSignedCertificate(ASN1Sequence sequence) {
        Iterator seq = ASN1Sequence.getInstance((Object)sequence).iterator();
        if (sequence.size() != 12) {
            throw new IllegalArgumentException("expected sequence size of 12");
        }
        this.id = CertificateId.getInstance(seq.next());
        this.cracaId = HashedId3.getInstance(seq.next());
        this.crlSeries = CrlSeries.getInstance(seq.next());
        this.validityPeriod = ValidityPeriod.getInstance(seq.next());
        this.region = OEROptional.getValue(GeographicRegion.class, seq.next());
        this.assuranceLevel = OEROptional.getValue(SubjectAssurance.class, seq.next());
        this.appPermissions = OEROptional.getValue(SequenceOfPsidSsp.class, seq.next());
        this.certIssuePermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, seq.next());
        this.certRequestPermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, seq.next());
        this.canRequestRollover = OEROptional.getValue(ASN1Null.class, seq.next());
        this.encryptionKey = OEROptional.getValue(PublicEncryptionKey.class, seq.next());
        this.verifyKeyIndicator = VerificationKeyIndicator.getInstance(seq.next());
    }

    public static ToBeSignedCertificate getInstance(Object o) {
        if (o instanceof ToBeSignedCertificate) {
            return (ToBeSignedCertificate)((Object)o);
        }
        if (o != null) {
            return new ToBeSignedCertificate(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertificateId getId() {
        return this.id;
    }

    public HashedId3 getCracaId() {
        return this.cracaId;
    }

    public CrlSeries getCrlSeries() {
        return this.crlSeries;
    }

    public ValidityPeriod getValidityPeriod() {
        return this.validityPeriod;
    }

    public GeographicRegion getRegion() {
        return this.region;
    }

    public SubjectAssurance getAssuranceLevel() {
        return this.assuranceLevel;
    }

    public SequenceOfPsidSsp getAppPermissions() {
        return this.appPermissions;
    }

    public SequenceOfPsidGroupPermissions getCertIssuePermissions() {
        return this.certIssuePermissions;
    }

    public SequenceOfPsidGroupPermissions getCertRequestPermissions() {
        return this.certRequestPermissions;
    }

    public ASN1Null getCanRequestRollover() {
        return this.canRequestRollover;
    }

    public PublicEncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public VerificationKeyIndicator getVerifyKeyIndicator() {
        return this.verifyKeyIndicator;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.id, this.cracaId, this.crlSeries, this.validityPeriod, OEROptional.getInstance((Object)this.region), OEROptional.getInstance((Object)this.assuranceLevel), OEROptional.getInstance((Object)this.appPermissions), OEROptional.getInstance((Object)this.certIssuePermissions), OEROptional.getInstance((Object)this.certRequestPermissions), OEROptional.getInstance(this.canRequestRollover), OEROptional.getInstance((Object)this.encryptionKey), this.verifyKeyIndicator});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CertificateId id;
        private HashedId3 cracaId;
        private CrlSeries crlSeries;
        private ValidityPeriod validityPeriod;
        private GeographicRegion region;
        private SubjectAssurance assuranceLevel;
        private SequenceOfPsidSsp appPermissions;
        private SequenceOfPsidGroupPermissions certIssuePermissions;
        private SequenceOfPsidGroupPermissions certRequestPermissions;
        private ASN1Null canRequestRollover;
        private PublicEncryptionKey encryptionKey;
        private VerificationKeyIndicator verifyKeyIndicator;

        public Builder() {
        }

        public Builder(Builder o) {
            this.id = o.id;
            this.cracaId = o.cracaId;
            this.crlSeries = o.crlSeries;
            this.validityPeriod = o.validityPeriod;
            this.region = o.region;
            this.assuranceLevel = o.assuranceLevel;
            this.appPermissions = o.appPermissions;
            this.certIssuePermissions = o.certIssuePermissions;
            this.certRequestPermissions = o.certRequestPermissions;
            this.canRequestRollover = o.canRequestRollover;
            this.encryptionKey = o.encryptionKey;
            this.verifyKeyIndicator = o.verifyKeyIndicator;
        }

        public Builder(ToBeSignedCertificate o) {
            this.id = o.id;
            this.cracaId = o.cracaId;
            this.crlSeries = o.crlSeries;
            this.validityPeriod = o.validityPeriod;
            this.region = o.region;
            this.assuranceLevel = o.assuranceLevel;
            this.appPermissions = o.appPermissions;
            this.certIssuePermissions = o.certIssuePermissions;
            this.certRequestPermissions = o.certRequestPermissions;
            this.canRequestRollover = o.canRequestRollover;
            this.encryptionKey = o.encryptionKey;
            this.verifyKeyIndicator = o.verifyKeyIndicator;
        }

        public Builder setId(CertificateId id) {
            this.id = id;
            return this;
        }

        public Builder setCracaId(HashedId3 cracaId) {
            this.cracaId = cracaId;
            return this;
        }

        public Builder setCrlSeries(CrlSeries crlSeries) {
            this.crlSeries = crlSeries;
            return this;
        }

        public Builder setValidityPeriod(ValidityPeriod validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        public Builder setRegion(GeographicRegion region) {
            this.region = region;
            return this;
        }

        public Builder setAssuranceLevel(SubjectAssurance assuranceLevel) {
            this.assuranceLevel = assuranceLevel;
            return this;
        }

        public Builder setAppPermissions(SequenceOfPsidSsp appPermissions) {
            this.appPermissions = appPermissions;
            return this;
        }

        public Builder setCertIssuePermissions(SequenceOfPsidGroupPermissions certIssuePermissions) {
            this.certIssuePermissions = certIssuePermissions;
            return this;
        }

        public Builder setCertRequestPermissions(SequenceOfPsidGroupPermissions certRequestPermissions) {
            this.certRequestPermissions = certRequestPermissions;
            return this;
        }

        public Builder setCanRequestRollover() {
            this.canRequestRollover = DERNull.INSTANCE;
            return this;
        }

        public Builder setEncryptionKey(PublicEncryptionKey encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        public Builder setVerifyKeyIndicator(VerificationKeyIndicator verifyKeyIndicator) {
            this.verifyKeyIndicator = verifyKeyIndicator;
            return this;
        }

        public ToBeSignedCertificate createToBeSignedCertificate() {
            return new ToBeSignedCertificate(this.id, this.cracaId, this.crlSeries, this.validityPeriod, this.region, this.assuranceLevel, this.appPermissions, this.certIssuePermissions, this.certRequestPermissions, this.canRequestRollover, this.encryptionKey, this.verifyKeyIndicator);
        }
    }
}

