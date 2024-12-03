/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SigPolicyQualifierInfo
extends ASN1Object {
    private ASN1ObjectIdentifier sigPolicyQualifierId;
    private ASN1Encodable sigQualifier;

    public SigPolicyQualifierInfo(ASN1ObjectIdentifier sigPolicyQualifierId, ASN1Encodable sigQualifier) {
        this.sigPolicyQualifierId = sigPolicyQualifierId;
        this.sigQualifier = sigQualifier;
    }

    private SigPolicyQualifierInfo(ASN1Sequence seq) {
        this.sigPolicyQualifierId = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.sigQualifier = seq.getObjectAt(1);
    }

    public static SigPolicyQualifierInfo getInstance(Object obj) {
        if (obj instanceof SigPolicyQualifierInfo) {
            return (SigPolicyQualifierInfo)((Object)obj);
        }
        if (obj != null) {
            return new SigPolicyQualifierInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getSigPolicyQualifierId() {
        return new ASN1ObjectIdentifier(this.sigPolicyQualifierId.getId());
    }

    public ASN1Encodable getSigQualifier() {
        return this.sigQualifier;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.sigPolicyQualifierId);
        v.add(this.sigQualifier);
        return new DERSequence(v);
    }
}

