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
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi102941.Url;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

public class TlmEntry
extends ASN1Object {
    private final EtsiTs103097Certificate selfSignedTLMCertificate;
    private final EtsiTs103097Certificate successorTo;
    private final Url accessPoint;

    public TlmEntry(EtsiTs103097Certificate selfSignedTLMCertificate, EtsiTs103097Certificate successorTo, Url accessPoint) {
        this.selfSignedTLMCertificate = selfSignedTLMCertificate;
        this.successorTo = successorTo;
        this.accessPoint = accessPoint;
    }

    private TlmEntry(ASN1Sequence sequence) {
        if (sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.selfSignedTLMCertificate = EtsiTs103097Certificate.getInstance(sequence.getObjectAt(0));
        this.successorTo = OEROptional.getValue(EtsiTs103097Certificate.class, sequence.getObjectAt(1));
        this.accessPoint = Url.getInstance(sequence.getObjectAt(2));
    }

    public static TlmEntry getInstance(Object o) {
        if (o instanceof TlmEntry) {
            return (TlmEntry)((Object)o);
        }
        if (o != null) {
            return new TlmEntry(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public EtsiTs103097Certificate getSelfSignedTLMCertificate() {
        return this.selfSignedTLMCertificate;
    }

    public EtsiTs103097Certificate getSuccessorTo() {
        return this.successorTo;
    }

    public Url getAccessPoint() {
        return this.accessPoint;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.selfSignedTLMCertificate, OEROptional.getInstance((Object)this.successorTo), this.accessPoint});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EtsiTs103097Certificate selfSignedTLMCertificate;
        private EtsiTs103097Certificate successorTo;
        private Url accessPoint;

        public Builder setSelfSignedTLMCertificate(EtsiTs103097Certificate selfSignedTLMCertificate) {
            this.selfSignedTLMCertificate = selfSignedTLMCertificate;
            return this;
        }

        public Builder setSuccessorTo(EtsiTs103097Certificate successorTo) {
            this.successorTo = successorTo;
            return this;
        }

        public Builder setAccessPoint(Url accessPoint) {
            this.accessPoint = accessPoint;
            return this;
        }

        public TlmEntry createTlmEntry() {
            return new TlmEntry(this.selfSignedTLMCertificate, this.successorTo, this.accessPoint);
        }
    }
}

