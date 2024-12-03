/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.Attribute;

public class Attributes
extends ASN1Object {
    private ASN1Set attributes;

    private Attributes(ASN1Set aSN1Set) {
        this.attributes = aSN1Set;
    }

    public Attributes(ASN1EncodableVector aSN1EncodableVector) {
        this.attributes = new DLSet(aSN1EncodableVector);
    }

    public static Attributes getInstance(Object object) {
        if (object instanceof Attributes) {
            return (Attributes)object;
        }
        if (object != null) {
            return new Attributes(ASN1Set.getInstance(object));
        }
        return null;
    }

    public static Attributes getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return Attributes.getInstance(ASN1Set.getInstance(aSN1TaggedObject, bl));
    }

    public Attribute[] getAttributes() {
        Attribute[] attributeArray = new Attribute[this.attributes.size()];
        for (int i = 0; i != attributeArray.length; ++i) {
            attributeArray[i] = Attribute.getInstance(this.attributes.getObjectAt(i));
        }
        return attributeArray;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.attributes;
    }
}

