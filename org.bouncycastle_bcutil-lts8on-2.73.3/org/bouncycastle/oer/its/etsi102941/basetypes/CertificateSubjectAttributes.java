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
package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GeographicRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SubjectAssurance;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class CertificateSubjectAttributes
extends ASN1Object {
    private final CertificateId id;
    private final ValidityPeriod validityPeriod;
    private final GeographicRegion region;
    private final SubjectAssurance assuranceLevel;
    private final SequenceOfPsidSsp appPermissions;
    private final SequenceOfPsidGroupPermissions certIssuePermissions;

    public CertificateSubjectAttributes(CertificateId id, ValidityPeriod validityPeriod, GeographicRegion region, SubjectAssurance assuranceLevel, SequenceOfPsidSsp appPermissions, SequenceOfPsidGroupPermissions certIssuePermissions) {
        this.id = id;
        this.validityPeriod = validityPeriod;
        this.region = region;
        this.assuranceLevel = assuranceLevel;
        this.appPermissions = appPermissions;
        this.certIssuePermissions = certIssuePermissions;
    }

    private CertificateSubjectAttributes(ASN1Sequence sequence) {
        if (sequence.size() != 6) {
            throw new IllegalArgumentException("expected sequence size of 6");
        }
        this.id = OEROptional.getValue(CertificateId.class, sequence.getObjectAt(0));
        this.validityPeriod = OEROptional.getValue(ValidityPeriod.class, sequence.getObjectAt(1));
        this.region = OEROptional.getValue(GeographicRegion.class, sequence.getObjectAt(2));
        this.assuranceLevel = OEROptional.getValue(SubjectAssurance.class, sequence.getObjectAt(3));
        this.appPermissions = OEROptional.getValue(SequenceOfPsidSsp.class, sequence.getObjectAt(4));
        this.certIssuePermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, sequence.getObjectAt(5));
    }

    public static CertificateSubjectAttributes getInstance(Object o) {
        if (o instanceof CertificateSubjectAttributes) {
            return (CertificateSubjectAttributes)((Object)o);
        }
        if (o != null) {
            return new CertificateSubjectAttributes(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertificateId getId() {
        return this.id;
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

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{OEROptional.getInstance((Object)this.id), OEROptional.getInstance((Object)this.validityPeriod), OEROptional.getInstance((Object)this.region), OEROptional.getInstance((Object)this.assuranceLevel), OEROptional.getInstance((Object)this.appPermissions), OEROptional.getInstance((Object)this.certIssuePermissions)});
    }
}

