/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.CRLReason;

public class RevokedInfo
extends ASN1Object {
    private ASN1GeneralizedTime revocationTime;
    private CRLReason revocationReason;

    public RevokedInfo(ASN1GeneralizedTime revocationTime) {
        this(revocationTime, null);
    }

    public RevokedInfo(ASN1GeneralizedTime revocationTime, CRLReason revocationReason) {
        this.revocationTime = revocationTime;
        this.revocationReason = revocationReason;
    }

    private RevokedInfo(ASN1Sequence seq) {
        this.revocationTime = ASN1GeneralizedTime.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.revocationReason = CRLReason.getInstance(ASN1Enumerated.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true));
        }
    }

    public static RevokedInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return RevokedInfo.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static RevokedInfo getInstance(Object obj) {
        if (obj instanceof RevokedInfo) {
            return (RevokedInfo)obj;
        }
        if (obj != null) {
            return new RevokedInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1GeneralizedTime getRevocationTime() {
        return this.revocationTime;
    }

    public CRLReason getRevocationReason() {
        return this.revocationReason;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.revocationTime);
        if (this.revocationReason != null) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.revocationReason));
        }
        return new DERSequence(v);
    }
}

