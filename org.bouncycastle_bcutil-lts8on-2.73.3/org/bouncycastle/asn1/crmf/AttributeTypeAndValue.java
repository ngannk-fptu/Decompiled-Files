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
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class AttributeTypeAndValue
extends ASN1Object {
    private ASN1ObjectIdentifier type;
    private ASN1Encodable value;

    private AttributeTypeAndValue(ASN1Sequence seq) {
        this.type = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        this.value = seq.getObjectAt(1);
    }

    public static AttributeTypeAndValue getInstance(Object o) {
        if (o instanceof AttributeTypeAndValue) {
            return (AttributeTypeAndValue)((Object)o);
        }
        if (o != null) {
            return new AttributeTypeAndValue(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public AttributeTypeAndValue(String oid, ASN1Encodable value) {
        this(new ASN1ObjectIdentifier(oid), value);
    }

    public AttributeTypeAndValue(ASN1ObjectIdentifier type, ASN1Encodable value) {
        this.type = type;
        this.value = value;
    }

    public ASN1ObjectIdentifier getType() {
        return this.type;
    }

    public ASN1Encodable getValue() {
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.type);
        v.add(this.value);
        return new DERSequence(v);
    }
}

