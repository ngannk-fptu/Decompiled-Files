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
import org.bouncycastle.oer.its.etsi102941.Url;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

public class AaEntry
extends ASN1Object {
    private final EtsiTs103097Certificate aaCertificate;
    private final Url accessPoint;

    public AaEntry(EtsiTs103097Certificate aaCertificate, Url accessPoint) {
        this.aaCertificate = aaCertificate;
        this.accessPoint = accessPoint;
    }

    private AaEntry(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.aaCertificate = EtsiTs103097Certificate.getInstance(sequence.getObjectAt(0));
        this.accessPoint = Url.getInstance(sequence.getObjectAt(1));
    }

    public static AaEntry getInstance(Object o) {
        if (o instanceof AaEntry) {
            return (AaEntry)((Object)o);
        }
        if (o != null) {
            return new AaEntry(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public EtsiTs103097Certificate getAaCertificate() {
        return this.aaCertificate;
    }

    public Url getAccessPoint() {
        return this.accessPoint;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.aaCertificate, this.accessPoint});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EtsiTs103097Certificate aaCertificate;
        private Url accessPoint;

        public Builder setAaCertificate(EtsiTs103097Certificate aaCertificate) {
            this.aaCertificate = aaCertificate;
            return this;
        }

        public Builder setAccessPoint(Url accessPoint) {
            this.accessPoint = accessPoint;
            return this;
        }

        public AaEntry createAaEntry() {
            return new AaEntry(this.aaCertificate, this.accessPoint);
        }
    }
}

