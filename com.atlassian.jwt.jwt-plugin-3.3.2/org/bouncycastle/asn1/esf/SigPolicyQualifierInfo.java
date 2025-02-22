/*
 * Decompiled with CFR 0.152.
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

    public SigPolicyQualifierInfo(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.sigPolicyQualifierId = aSN1ObjectIdentifier;
        this.sigQualifier = aSN1Encodable;
    }

    private SigPolicyQualifierInfo(ASN1Sequence aSN1Sequence) {
        this.sigPolicyQualifierId = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.sigQualifier = aSN1Sequence.getObjectAt(1);
    }

    public static SigPolicyQualifierInfo getInstance(Object object) {
        if (object instanceof SigPolicyQualifierInfo) {
            return (SigPolicyQualifierInfo)object;
        }
        if (object != null) {
            return new SigPolicyQualifierInfo(ASN1Sequence.getInstance(object));
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
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.sigPolicyQualifierId);
        aSN1EncodableVector.add(this.sigQualifier);
        return new DERSequence(aSN1EncodableVector);
    }
}

