/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.EndEntityType;
import org.bouncycastle.oer.its.ieee1609dot2.SubjectPermissions;

public class PsidGroupPermissions
extends ASN1Object {
    private final SubjectPermissions subjectPermissions;
    private final ASN1Integer minChainLength;
    private final ASN1Integer chainLengthRange;
    private final EndEntityType eeType;

    private PsidGroupPermissions(ASN1Sequence seq) {
        if (seq.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.subjectPermissions = SubjectPermissions.getInstance(seq.getObjectAt(0));
        this.minChainLength = OEROptional.getInstance(seq.getObjectAt(1)).getObject(ASN1Integer.class);
        this.chainLengthRange = OEROptional.getInstance(seq.getObjectAt(2)).getObject(ASN1Integer.class);
        this.eeType = OEROptional.getInstance(seq.getObjectAt(3)).getObject(EndEntityType.class);
    }

    public PsidGroupPermissions(SubjectPermissions subjectPermissions, ASN1Integer minChainLength, ASN1Integer chainLengthRange, EndEntityType eeType) {
        this.subjectPermissions = subjectPermissions;
        this.minChainLength = minChainLength;
        this.chainLengthRange = chainLengthRange;
        this.eeType = eeType;
    }

    public static PsidGroupPermissions getInstance(Object src) {
        if (src instanceof PsidGroupPermissions) {
            return (PsidGroupPermissions)((Object)src);
        }
        if (src != null) {
            return new PsidGroupPermissions(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SubjectPermissions getSubjectPermissions() {
        return this.subjectPermissions;
    }

    public ASN1Integer getMinChainLength() {
        return this.minChainLength;
    }

    public EndEntityType getEeType() {
        return this.eeType;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.subjectPermissions, OEROptional.getInstance(this.minChainLength), OEROptional.getInstance(this.chainLengthRange), OEROptional.getInstance((Object)this.eeType)});
    }

    public ASN1Integer getChainLengthRange() {
        return this.chainLengthRange;
    }

    public static class Builder {
        private SubjectPermissions subjectPermissions;
        private ASN1Integer minChainLength;
        private ASN1Integer chainLengthRange;
        private EndEntityType eeType;

        public Builder setSubjectPermissions(SubjectPermissions subjectPermissions) {
            this.subjectPermissions = subjectPermissions;
            return this;
        }

        public Builder setMinChainLength(BigInteger minChainLength) {
            this.minChainLength = new ASN1Integer(minChainLength);
            return this;
        }

        public Builder setMinChainLength(long minChainLength) {
            this.minChainLength = new ASN1Integer(minChainLength);
            return this;
        }

        public Builder setChainLengthRange(ASN1Integer chainLengthRange) {
            this.chainLengthRange = chainLengthRange;
            return this;
        }

        public Builder setMinChainLength(ASN1Integer minChainLength) {
            this.minChainLength = minChainLength;
            return this;
        }

        public Builder setChainLengthRange(BigInteger chainLengthRange) {
            this.chainLengthRange = new ASN1Integer(chainLengthRange);
            return this;
        }

        public Builder setChainLengthRange(long chainLengthRange) {
            this.chainLengthRange = new ASN1Integer(chainLengthRange);
            return this;
        }

        public Builder setEeType(EndEntityType eeType) {
            this.eeType = eeType;
            return this;
        }

        public PsidGroupPermissions createPsidGroupPermissions() {
            return new PsidGroupPermissions(this.subjectPermissions, this.minChainLength, this.chainLengthRange, this.eeType);
        }
    }
}

