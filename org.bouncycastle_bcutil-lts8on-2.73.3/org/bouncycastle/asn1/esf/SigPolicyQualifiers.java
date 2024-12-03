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
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

public class SigPolicyQualifiers
extends ASN1Object {
    ASN1Sequence qualifiers;

    public static SigPolicyQualifiers getInstance(Object obj) {
        if (obj instanceof SigPolicyQualifiers) {
            return (SigPolicyQualifiers)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new SigPolicyQualifiers(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private SigPolicyQualifiers(ASN1Sequence seq) {
        this.qualifiers = seq;
    }

    public SigPolicyQualifiers(SigPolicyQualifierInfo[] qualifierInfos) {
        this.qualifiers = new DERSequence((ASN1Encodable[])qualifierInfos);
    }

    public int size() {
        return this.qualifiers.size();
    }

    public SigPolicyQualifierInfo getInfoAt(int i) {
        return SigPolicyQualifierInfo.getInstance(this.qualifiers.getObjectAt(i));
    }

    public ASN1Primitive toASN1Primitive() {
        return this.qualifiers;
    }
}

