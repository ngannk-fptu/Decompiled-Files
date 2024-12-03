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
package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941DeltaCtlRequest;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class EtsiTs102941CtlRequest
extends ASN1Object {
    private final HashedId8 issuerId;
    private final ASN1Integer lastKnownCtlSequence;

    protected EtsiTs102941CtlRequest(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.issuerId = HashedId8.getInstance(sequence.getObjectAt(0));
        this.lastKnownCtlSequence = sequence.size() == 2 ? OEROptional.getValue(ASN1Integer.class, sequence.getObjectAt(1)) : null;
    }

    public EtsiTs102941CtlRequest(HashedId8 issuerId, ASN1Integer lastKnownCtlSequence) {
        this.issuerId = issuerId;
        this.lastKnownCtlSequence = lastKnownCtlSequence;
    }

    public static EtsiTs102941CtlRequest getInstance(Object o) {
        if (o instanceof EtsiTs102941CtlRequest) {
            return (EtsiTs102941CtlRequest)((Object)o);
        }
        if (o != null) {
            return new EtsiTs102941CtlRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public HashedId8 getIssuerId() {
        return this.issuerId;
    }

    public ASN1Integer getLastKnownCtlSequence() {
        return this.lastKnownCtlSequence;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.issuerId, OEROptional.getInstance(this.lastKnownCtlSequence)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HashedId8 issuerId;
        private ASN1Integer lastKnownCtlSequence;

        public Builder setIssuerId(HashedId8 issuerId) {
            this.issuerId = issuerId;
            return this;
        }

        public Builder setLastKnownCtlSequence(ASN1Integer lastKnownCtlSequence) {
            this.lastKnownCtlSequence = lastKnownCtlSequence;
            return this;
        }

        public EtsiTs102941CtlRequest createEtsiTs102941CtlRequest() {
            return new EtsiTs102941CtlRequest(this.issuerId, this.lastKnownCtlSequence);
        }

        public EtsiTs102941DeltaCtlRequest createEtsiTs102941DeltaCtlRequest() {
            return new EtsiTs102941DeltaCtlRequest(this.issuerId, this.lastKnownCtlSequence);
        }
    }
}

