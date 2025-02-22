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
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

public class RootCaEntry
extends ASN1Object {
    private final EtsiTs103097Certificate selfsignedRootCa;
    private final EtsiTs103097Certificate successorTo;

    public RootCaEntry(EtsiTs103097Certificate selfsignedRootCa, EtsiTs103097Certificate successorTo) {
        this.selfsignedRootCa = selfsignedRootCa;
        this.successorTo = successorTo;
    }

    private RootCaEntry(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.selfsignedRootCa = EtsiTs103097Certificate.getInstance(sequence.getObjectAt(0));
        this.successorTo = OEROptional.getValue(EtsiTs103097Certificate.class, sequence.getObjectAt(1));
    }

    public static RootCaEntry getInstance(Object o) {
        if (o instanceof RootCaEntry) {
            return (RootCaEntry)((Object)o);
        }
        if (o != null) {
            return new RootCaEntry(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public EtsiTs103097Certificate getSelfsignedRootCa() {
        return this.selfsignedRootCa;
    }

    public EtsiTs103097Certificate getSuccessorTo() {
        return this.successorTo;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.selfsignedRootCa, OEROptional.getInstance((Object)this.successorTo)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EtsiTs103097Certificate selfsignedRootCa;
        private EtsiTs103097Certificate successorTo;

        public Builder setSelfsignedRootCa(EtsiTs103097Certificate selfsignedRootCa) {
            this.selfsignedRootCa = selfsignedRootCa;
            return this;
        }

        public Builder setSuccessorTo(EtsiTs103097Certificate successorTo) {
            this.successorTo = successorTo;
            return this;
        }

        public RootCaEntry createRootCaEntry() {
            return new RootCaEntry(this.selfsignedRootCa, this.successorTo);
        }
    }
}

