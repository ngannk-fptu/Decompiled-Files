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
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherKeyAttribute
extends ASN1Object {
    private ASN1ObjectIdentifier keyAttrId;
    private ASN1Encodable keyAttr;

    public static OtherKeyAttribute getInstance(Object o) {
        if (o instanceof OtherKeyAttribute) {
            return (OtherKeyAttribute)((Object)o);
        }
        if (o != null) {
            return new OtherKeyAttribute(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private OtherKeyAttribute(ASN1Sequence seq) {
        this.keyAttrId = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        this.keyAttr = seq.getObjectAt(1);
    }

    public OtherKeyAttribute(ASN1ObjectIdentifier keyAttrId, ASN1Encodable keyAttr) {
        this.keyAttrId = keyAttrId;
        this.keyAttr = keyAttr;
    }

    public ASN1ObjectIdentifier getKeyAttrId() {
        return this.keyAttrId;
    }

    public ASN1Encodable getKeyAttr() {
        return this.keyAttr;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.keyAttrId);
        v.add(this.keyAttr);
        return new DERSequence(v);
    }
}

